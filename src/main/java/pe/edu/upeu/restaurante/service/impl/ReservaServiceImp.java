package pe.edu.upeu.restaurante.service.impl;

import pe.edu.upeu.restaurante.repository.ReservaRepository;
import pe.edu.upeu.restaurante.repository.ICrudGenericoRepository;
import pe.edu.upeu.restaurante.model.Reserva;
import pe.edu.upeu.restaurante.service.IReservaService;
import java.util.List;

/** Inyección por constructor como en SysVentas. */
public class ReservaServiceImp extends CrudGenericoServiceImp<Reserva,Long> implements IReservaService {
    private final ReservaRepository repository;
    public ReservaServiceImp(ReservaRepository repository) { this.repository=repository; }
    @Override protected ICrudGenericoRepository<Reserva,Long> getRepo() { return repository; }
    @Override public void finalizar(long id) { repository.finalizar(id); }
    @Override public void cancelar(long id) { repository.cancelar(id); }
}
