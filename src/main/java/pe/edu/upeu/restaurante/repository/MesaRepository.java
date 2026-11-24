package pe.edu.upeu.restaurante.repository;

import pe.edu.upeu.restaurante.model.Mesa;
import java.sql.*;
import java.util.List;

/** Repositorio JDBC de mesas, con el patrón AbstractJpaRepository de SysVentas. */
public class MesaRepository extends AbstractJpaRepository<Mesa, Long> {
    @Override protected String getTableName() { return "mesas"; }
    @Override protected String getPkColumn() { return "id"; }
    @Override protected Mesa mapRow(ResultSet rs) throws SQLException { return new Mesa(rs.getLong("id"),rs.getInt("numero"),rs.getInt("capacidad"),rs.getString("estado")); }
    @Override protected Mesa insert(Connection conn, Mesa e) throws SQLException {
        long id = executeInsertGetKey(conn, "INSERT INTO mesas(numero,capacidad,estado) VALUES(?,?,?)", e.getNumero(), e.getCapacidad(), e.getEstado());
        return new Mesa(id, e.getNumero(), e.getCapacidad(), e.getEstado());
    }
    @Override protected Mesa updateRow(Connection conn, Mesa e) throws SQLException {
        if ("LIBRE".equals(e.getEstado())) {
            try(PreparedStatement check=conn.prepareStatement("SELECT COUNT(*) FROM pedidos WHERE mesa_id=? AND estado NOT IN ('ENTREGADO','CANCELADO')")) {
                check.setLong(1,e.getId());
                try(ResultSet r=check.executeQuery()) {
                    r.next();
                    if(r.getLong(1)>0) throw new IllegalStateException("No se puede liberar una mesa con pedidos pendientes.");
                }
            }
        }
        executeUpdate(conn, "UPDATE mesas SET numero=?,capacidad=?,estado=? WHERE id=?", e.getNumero(), e.getCapacidad(), e.getEstado(), e.getId());
        return e;
    }
    @Override public List<Mesa> findAll() { return executeQuery("SELECT * FROM mesas ORDER BY numero"); }
    public List<Mesa> libres() { return executeQuery("SELECT * FROM mesas WHERE estado='LIBRE' ORDER BY numero"); }
    public int eliminarSiLibre(long id) { return executeUpdateStandalone("DELETE FROM mesas WHERE id=? AND estado='LIBRE'",id); }
}
