package pe.edu.upeu.restaurante.service.impl;
import pe.edu.upeu.restaurante.dto.PedidoItemDTO;
import pe.edu.upeu.restaurante.model.Pedido;
import pe.edu.upeu.restaurante.repository.PedidoRepository;
import pe.edu.upeu.restaurante.service.IPedidoService;
import java.util.List;
public class PedidoServiceImp extends CrudGenericoServiceImp<Pedido,Long> implements IPedidoService {
    private final PedidoRepository repository;
    public PedidoServiceImp(PedidoRepository repository){this.repository=repository;}
    @Override protected pe.edu.upeu.restaurante.repository.ICrudGenericoRepository<Pedido,Long> getRepo(){return repository;}
    @Override public long registrar(Long mesaId,String cliente,List<PedidoItemDTO> items){return repository.registrar(mesaId,cliente,items);}
    @Override public List<Pedido> recientes(){return repository.recientes();}
    @Override public List<String[]> historial(){return repository.historial();}
    @Override public List<PedidoItemDTO> detalle(long id){return repository.detalle(id);}
    @Override public void actualizarEstado(long id,String estado){repository.actualizarEstado(id,estado);}
}
