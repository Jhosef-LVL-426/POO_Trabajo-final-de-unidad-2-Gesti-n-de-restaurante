package pe.edu.upeu.restaurante.service;

import pe.edu.upeu.restaurante.model.Plato;
import java.util.List;

public interface IPlatoService extends ICrudGenericoService<Plato, Long> {
    List<Plato> disponibles();
}
