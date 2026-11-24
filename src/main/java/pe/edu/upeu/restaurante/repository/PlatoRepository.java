package pe.edu.upeu.restaurante.repository;

import pe.edu.upeu.restaurante.model.Plato;
import java.sql.*;
import java.util.List;

/** Repositorio JDBC de platos, con el patrón AbstractJpaRepository de SysVentas. */
public class PlatoRepository extends AbstractJpaRepository<Plato, Long> {
    @Override protected String getTableName() { return "platos"; }
    @Override protected String getPkColumn() { return "id"; }
    @Override protected Plato mapRow(ResultSet rs) throws SQLException { return new Plato(rs.getLong("id"),rs.getString("nombre"),rs.getString("categoria"),rs.getBigDecimal("precio"),rs.getBoolean("disponible")); }
    @Override protected Plato insert(Connection conn, Plato e) throws SQLException {
        long id = executeInsertGetKey(conn, "INSERT INTO platos(nombre,categoria,precio,disponible) VALUES(?,?,?,?)", e.getNombre(), e.getCategoria(), e.getPrecio(), e.isDisponible());
        return new Plato(id, e.getNombre(), e.getCategoria(), e.getPrecio(), e.isDisponible());
    }
    @Override protected Plato updateRow(Connection conn, Plato e) throws SQLException {
        executeUpdate(conn, "UPDATE platos SET nombre=?,categoria=?,precio=?,disponible=? WHERE id=?", e.getNombre(), e.getCategoria(), e.getPrecio(), e.isDisponible(), e.getId());
        return e;
    }
    public List<Plato> disponibles() { return executeQuery("SELECT * FROM platos WHERE disponible=TRUE ORDER BY nombre"); }
}
