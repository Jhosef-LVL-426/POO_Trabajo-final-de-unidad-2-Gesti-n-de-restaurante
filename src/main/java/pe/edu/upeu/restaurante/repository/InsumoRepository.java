package pe.edu.upeu.restaurante.repository;

import pe.edu.upeu.restaurante.model.Insumo;
import java.sql.*;
import java.math.BigDecimal;
import java.util.List;

/** Repositorio JDBC de insumos, con el patrón AbstractJpaRepository de SysVentas. */
public class InsumoRepository extends AbstractJpaRepository<Insumo, Long> {
    @Override protected String getTableName() { return "insumos"; }
    @Override protected String getPkColumn() { return "id"; }
    @Override protected Insumo mapRow(ResultSet rs) throws SQLException { return new Insumo(rs.getLong("id"),rs.getString("nombre"),rs.getString("unidad"),rs.getBigDecimal("stock"),rs.getBigDecimal("minimo")); }
    @Override protected Insumo insert(Connection conn, Insumo e) throws SQLException {
        long id = executeInsertGetKey(conn, "INSERT INTO insumos(nombre,unidad,stock,minimo) VALUES(?,?,?,?)", e.getNombre(), e.getUnidad(), e.getStock(), e.getMinimo());
        return new Insumo(id, e.getNombre(), e.getUnidad(), e.getStock(), e.getMinimo());
    }
    @Override protected Insumo updateRow(Connection conn, Insumo e) throws SQLException {
        executeUpdate(conn, "UPDATE insumos SET nombre=?,unidad=?,stock=?,minimo=? WHERE id=?", e.getNombre(), e.getUnidad(), e.getStock(), e.getMinimo(), e.getId());
        return e;
    }
    public void actualizarCantidad(long id, BigDecimal anterior, BigDecimal nueva) {
        int filas = executeUpdateStandalone("UPDATE insumos SET stock=? WHERE id=? AND stock=?", nueva, id, anterior);
        if (filas == 0) throw new IllegalStateException("El insumo cambió. Actualiza la lista y vuelve a seleccionarlo.");
    }

    @Override public List<Insumo> findAll() { return executeQuery("SELECT * FROM insumos ORDER BY nombre"); }
}
