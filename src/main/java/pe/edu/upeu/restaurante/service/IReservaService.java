package pe.edu.upeu.restaurante.service;

import pe.edu.upeu.restaurante.model.Reserva;
import java.util.List;

public interface IReservaService extends ICrudGenericoService<Reserva, Long> {
    void cancelar(long id);
    void finalizar(long id);
}
