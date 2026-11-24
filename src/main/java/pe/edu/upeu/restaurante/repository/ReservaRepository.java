package pe.edu.upeu.restaurante.repository;

import pe.edu.upeu.restaurante.model.Reserva;
import java.sql.*;
import java.util.List;

/** Repositorio JDBC de reservas, con el patrón AbstractJpaRepository de SysVentas. */
public class ReservaRepository extends AbstractJpaRepository<Reserva, Long> {
    @Override protected String getTableName() { return "reservas"; }
    @Override protected String getPkColumn() { return "id"; }
    @Override protected Reserva mapRow(ResultSet rs) throws SQLException { return new Reserva(rs.getLong("id"),rs.getString("cliente"),rs.getString("telefono"),rs.getDate("fecha").toLocalDate(),rs.getString("hora"),rs.getInt("personas"),rs.getString("estado")); }
    @Override protected Reserva insert(Connection conn, Reserva e) throws SQLException {
        long id = executeInsertGetKey(conn, "INSERT INTO reservas(cliente,telefono,fecha,hora,personas,estado) VALUES(?,?,?,?,?,?)", e.getCliente(), e.getTelefono(), java.sql.Date.valueOf(e.getFecha()), e.getHora(), e.getPersonas(), e.getEstado());
        return new Reserva(id, e.getCliente(), e.getTelefono(), e.getFecha(), e.getHora(), e.getPersonas(), e.getEstado());
    }
    @Override protected Reserva updateRow(Connection conn, Reserva e) throws SQLException {
        executeUpdate(conn, "UPDATE reservas SET cliente=?,telefono=?,fecha=?,hora=?,personas=?,estado=? WHERE id=?", e.getCliente(), e.getTelefono(), java.sql.Date.valueOf(e.getFecha()), e.getHora(), e.getPersonas(), e.getEstado(), e.getId());
        return e;
    }
    @Override public List<Reserva> findAll() { return executeQuery("SELECT * FROM reservas ORDER BY fecha DESC,hora"); }
    public void cancelar(long id) { cerrarReserva(id, "CANCELADA"); }
    public void finalizar(long id) { cerrarReserva(id, "FINALIZADO"); }

    private void cerrarReserva(long id, String estado) {
        int filas = executeUpdateStandalone(
                "UPDATE reservas SET estado=? WHERE id=? AND estado='CONFIRMADA'", estado, id);
        if (filas == 0) throw new IllegalStateException("Solo puedes cambiar una reserva confirmada. Actualiza la lista.");
    }
}
