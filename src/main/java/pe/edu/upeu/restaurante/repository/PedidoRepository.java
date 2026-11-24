package pe.edu.upeu.restaurante.repository;

import pe.edu.upeu.restaurante.config.DatabaseConfig;
import pe.edu.upeu.restaurante.dto.PedidoItemDTO;
import pe.edu.upeu.restaurante.model.Pedido;
import java.math.BigDecimal;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Acceso a pedidos y sus líneas; guarda pedido, detalle y mesa en una transacción. */
public class PedidoRepository extends AbstractJpaRepository<Pedido,Long> {
    @Override protected String getTableName() { return "pedidos"; }
    @Override protected String getPkColumn() { return "id"; }
    @Override protected Pedido mapRow(ResultSet rs) throws SQLException {
        Long mesaId=rs.getObject("mesa_id")==null?null:rs.getLong("mesa_id");
        return new Pedido(rs.getLong("id"),rs.getTimestamp("fecha").toLocalDateTime(),mesaId,rs.getString("cliente"),rs.getBigDecimal("total"),rs.getString("estado"));
    }
    @Override protected Pedido insert(Connection conn, Pedido e) throws SQLException {
        long id=executeInsertGetKey(conn,"INSERT INTO pedidos(mesa_id,cliente,total,estado) VALUES(?,?,?,?)",e.getMesaId(),e.getCliente(),e.getTotal(),e.getEstado());
        return new Pedido(id,e.getFecha(),e.getMesaId(),e.getCliente(),e.getTotal(),e.getEstado());
    }
    @Override protected Pedido updateRow(Connection conn, Pedido e) throws SQLException {
        executeUpdate(conn,"UPDATE pedidos SET mesa_id=?,cliente=?,total=?,estado=? WHERE id=?",e.getMesaId(),e.getCliente(),e.getTotal(),e.getEstado(),e.getId());
        return e;
    }

    public long registrar(Long mesaId, String cliente, List<PedidoItemDTO> items) {
        if (items==null || items.isEmpty()) throw new IllegalArgumentException("Agrega al menos un plato.");
        // BigDecimal: suma importes sin los errores de redondeo de double.
        BigDecimal total=items.stream().map(PedidoItemDTO::getSubtotal).reduce(BigDecimal.ZERO,BigDecimal::add);
        try (Connection c=DatabaseConfig.getConnection()) {
            // Transacción JDBC: se guardan juntos la mesa, el pedido y su detalle.
            c.setAutoCommit(false);
            try {
                // Misma transacción para evitar pedidos duplicados en una mesa ocupada.
                if (mesaId!=null) {
                    try (PreparedStatement s=c.prepareStatement("UPDATE mesas SET estado='OCUPADA' WHERE id=? AND estado='LIBRE'")) {
                        s.setLong(1,mesaId);
                        if(s.executeUpdate()!=1) throw new IllegalArgumentException("La mesa seleccionada ya no está libre.");
                    }
                }
                long pedidoId;
                try (PreparedStatement s=c.prepareStatement("INSERT INTO pedidos(mesa_id,cliente,total,estado) VALUES(?,?,?,'PENDIENTE')",Statement.RETURN_GENERATED_KEYS)) {
                    if(mesaId==null)s.setNull(1,Types.BIGINT); else s.setLong(1,mesaId);
                    s.setString(2,cliente==null||cliente.isBlank()?"Cliente general":cliente.trim());
                    s.setBigDecimal(3,total);s.executeUpdate();
                    try(ResultSet keys=s.getGeneratedKeys()){
                        if(!keys.next())throw new SQLException("No se generó el ID del pedido.");
                        pedidoId=keys.getLong(1);
                    }
                }
                try(PreparedStatement s=c.prepareStatement("INSERT INTO detalle_pedido(pedido_id,plato_id,plato,cantidad,precio,subtotal) VALUES(?,?,?,?,?,?)")) {
                    for(PedidoItemDTO item:items){
                        if(item.getCantidad()<1||item.getPrecio().signum()<0) throw new IllegalArgumentException("Cantidad o precio no válido.");
                        s.setLong(1,pedidoId);s.setLong(2,item.getPlatoId());s.setString(3,item.getPlato());
                        s.setInt(4,item.getCantidad());s.setBigDecimal(5,item.getPrecio());s.setBigDecimal(6,item.getSubtotal());s.addBatch();
                    }
                    s.executeBatch();
                }
                // commit confirma todos los cambios; rollback los revierte si falla alguno.
                c.commit();return pedidoId;
            }catch(Exception ex){
                c.rollback();
                if(ex instanceof RuntimeException) throw (RuntimeException)ex;
                throw new IllegalStateException("No se pudo registrar el pedido.",ex);
            }
        }catch(SQLException ex){throw new IllegalStateException("Error en base de datos: "+ex.getMessage(),ex);}
    }

    public List<Pedido> recientes() { return executeQuery("SELECT * FROM pedidos ORDER BY id DESC LIMIT 8"); }
    @Override public List<Pedido> findAll() { return executeQuery("SELECT * FROM pedidos ORDER BY id DESC"); }

    public List<String[]> historial() {
        List<String[]> datos = new ArrayList<>();
        // Leer el timestamp como fecha Java evita errores con FORMATDATETIME.
        String sql = "SELECT p.id,p.fecha,m.numero,p.cliente,p.total,p.estado " +
                     "FROM pedidos p LEFT JOIN mesas m ON p.mesa_id=m.id ORDER BY p.id DESC";
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement s = c.prepareStatement(sql);
             ResultSet r = s.executeQuery()) {
            while (r.next()) {
                Timestamp fecha = r.getTimestamp(2);
                Object numeroMesa = r.getObject(3);
                datos.add(new String[] {
                    r.getString(1),
                    fecha == null ? "-" : fecha.toLocalDateTime().format(formato),
                    numeroMesa == null ? "Sin mesa" : String.valueOf(numeroMesa),
                    r.getString(4) == null ? "Cliente general" : r.getString(4),
                    r.getBigDecimal(5).setScale(2, java.math.RoundingMode.HALF_UP).toPlainString(),
                    r.getString(6)
                });
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("No se pudo leer el historial: " + ex.getMessage(), ex);
        }
        return datos;
    }

    public List<PedidoItemDTO> detalle(long pedidoId) {
        List<PedidoItemDTO> rows=new ArrayList<>();
        try(Connection c=DatabaseConfig.getConnection();PreparedStatement s=c.prepareStatement("SELECT plato_id,plato,cantidad,precio,subtotal FROM detalle_pedido WHERE pedido_id=?")) {
            s.setLong(1,pedidoId);
            try(ResultSet r=s.executeQuery()) {
                while(r.next()) rows.add(new PedidoItemDTO(r.getLong(1),r.getString(2),r.getInt(3),r.getBigDecimal(4),r.getBigDecimal(5)));
            }
        }catch(SQLException ex){throw new IllegalStateException(ex.getMessage(),ex);}
        return rows;
    }

    public void actualizarEstado(long pedidoId,String estado) {
        if(!java.util.Set.of("PENDIENTE","EN PREPARACION","LISTO","ENTREGADO","CANCELADO").contains(estado))
            throw new IllegalArgumentException("Estado no válido.");
        try(Connection c=DatabaseConfig.getConnection()) {
            c.setAutoCommit(false);
            try {
                Long mesaId;String anterior;
                try(PreparedStatement s=c.prepareStatement("SELECT mesa_id,estado FROM pedidos WHERE id=? FOR UPDATE")) {
                    s.setLong(1,pedidoId);
                    try(ResultSet r=s.executeQuery()) {
                        if(!r.next())throw new IllegalArgumentException("El pedido no existe.");
                        mesaId=r.getObject(1)==null?null:r.getLong(1);anterior=r.getString(2);
                    }
                }
                boolean cerrado=estado.equals("ENTREGADO")||estado.equals("CANCELADO");
                boolean antesCerrado=anterior.equals("ENTREGADO")||anterior.equals("CANCELADO");
                if(mesaId!=null && !cerrado && antesCerrado) {
                    try(PreparedStatement s=c.prepareStatement("UPDATE mesas SET estado='OCUPADA' WHERE id=? AND estado='LIBRE'")) {
                        s.setLong(1,mesaId);
                        if(s.executeUpdate()!=1)throw new IllegalArgumentException("La mesa ya está ocupada; no se puede reabrir el pedido.");
                    }
                }
                try(PreparedStatement s=c.prepareStatement("UPDATE pedidos SET estado=? WHERE id=?")) {
                    s.setString(1,estado);s.setLong(2,pedidoId);s.executeUpdate();
                }
                if(mesaId!=null && cerrado && !antesCerrado) {
                    try(PreparedStatement s=c.prepareStatement("UPDATE mesas SET estado='LIBRE' WHERE id=?")) {
                        s.setLong(1,mesaId);s.executeUpdate();
                    }
                }
                c.commit();
            }catch(Exception ex){
                c.rollback();
                if(ex instanceof RuntimeException)throw (RuntimeException)ex;
                throw new IllegalStateException("No se pudo actualizar el estado.",ex);
            }
        }catch(SQLException ex){throw new IllegalStateException(ex.getMessage(),ex);}
    }
}
