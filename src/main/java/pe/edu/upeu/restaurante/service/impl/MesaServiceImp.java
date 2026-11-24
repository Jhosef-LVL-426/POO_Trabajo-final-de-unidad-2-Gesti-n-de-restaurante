package pe.edu.upeu.restaurante.service.impl;

import pe.edu.upeu.restaurante.repository.MesaRepository;
import pe.edu.upeu.restaurante.repository.ICrudGenericoRepository;
import pe.edu.upeu.restaurante.model.Mesa;
import pe.edu.upeu.restaurante.service.IMesaService;
import java.util.List;

/** Inyección por constructor como en SysVentas. */
public class MesaServiceImp extends CrudGenericoServiceImp<Mesa,Long> implements IMesaService {
    private final MesaRepository repository;
    public MesaServiceImp(MesaRepository repository) { this.repository=repository; }
    @Override protected ICrudGenericoRepository<Mesa,Long> getRepo() { return repository; }
    @Override public List<Mesa> libres() { return repository.libres(); }
    @Override public boolean eliminarSiLibre(long id) { return repository.eliminarSiLibre(id)>0; }
}
