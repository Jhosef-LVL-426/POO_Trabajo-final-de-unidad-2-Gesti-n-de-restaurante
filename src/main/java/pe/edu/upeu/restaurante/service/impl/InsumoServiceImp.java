package pe.edu.upeu.restaurante.service.impl;

import pe.edu.upeu.restaurante.repository.InsumoRepository;
import pe.edu.upeu.restaurante.repository.ICrudGenericoRepository;
import pe.edu.upeu.restaurante.model.Insumo;
import pe.edu.upeu.restaurante.service.IInsumoService;
import java.math.BigDecimal;

/** Inyección por constructor como en SysVentas. */
public class InsumoServiceImp extends CrudGenericoServiceImp<Insumo,Long> implements IInsumoService {
    private final InsumoRepository repository;
    public InsumoServiceImp(InsumoRepository repository) { this.repository=repository; }
    @Override public void actualizarCantidad(long id, BigDecimal anterior, BigDecimal nueva) {
        repository.actualizarCantidad(id, anterior, nueva);
    }
    @Override protected ICrudGenericoRepository<Insumo,Long> getRepo() { return repository; }
}
