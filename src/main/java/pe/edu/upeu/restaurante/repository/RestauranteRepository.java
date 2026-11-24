package pe.edu.upeu.restaurante.repository;
import pe.edu.upeu.restaurante.config.DatabaseConfig;
import java.sql.*;
import java.util.Set;
/** Consulta de indicadores, en la capa repositorio de SysVentas. */
public class RestauranteRepository {
    public long contar(String tabla) {
        if(!Set.of("platos","pedidos","mesas","reservas","insumos").contains(tabla))
            throw new IllegalArgumentException("Tabla no permitida.");
        try(Connection c=DatabaseConfig.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT COUNT(*) FROM "+tabla)) {
            r.next();return r.getLong(1);
        }catch(SQLException ex){throw new IllegalStateException(ex.getMessage(),ex);}
    }
}
