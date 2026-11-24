package pe.edu.upeu.restaurante.service.impl;

import pe.edu.upeu.restaurante.repository.PlatoRepository;
import pe.edu.upeu.restaurante.repository.ICrudGenericoRepository;
import pe.edu.upeu.restaurante.model.Plato;
import pe.edu.upeu.restaurante.service.IPlatoService;
import java.util.List;

/** Inyección por constructor como en SysVentas. */
public class PlatoServiceImp extends CrudGenericoServiceImp<Plato,Long> implements IPlatoService {
    private final PlatoRepository repository;
    public PlatoServiceImp(PlatoRepository repository) { this.repository=repository; }
    @Override protected ICrudGenericoRepository<Plato,Long> getRepo() { return repository; }
    @Override public List<Plato> disponibles() { return repository.disponibles(); }
}
