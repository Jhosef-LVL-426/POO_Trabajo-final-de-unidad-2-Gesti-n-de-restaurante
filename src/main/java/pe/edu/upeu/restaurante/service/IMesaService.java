package pe.edu.upeu.restaurante.service;

import pe.edu.upeu.restaurante.model.Mesa;
import java.util.List;

public interface IMesaService extends ICrudGenericoService<Mesa, Long> {
    List<Mesa> libres();
    boolean eliminarSiLibre(long id);
}
