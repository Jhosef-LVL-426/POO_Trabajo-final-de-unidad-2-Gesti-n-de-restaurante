package pe.edu.upeu.restaurante.service;

import pe.edu.upeu.restaurante.model.Insumo;
import java.math.BigDecimal;

public interface IInsumoService extends ICrudGenericoService<Insumo, Long> {
    void actualizarCantidad(long id, BigDecimal anterior, BigDecimal nueva);
}
