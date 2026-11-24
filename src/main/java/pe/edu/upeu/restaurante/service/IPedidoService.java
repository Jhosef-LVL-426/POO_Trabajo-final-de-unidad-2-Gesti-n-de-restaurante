package pe.edu.upeu.restaurante.service;
import pe.edu.upeu.restaurante.dto.PedidoItemDTO;
import pe.edu.upeu.restaurante.model.Pedido;
import java.util.List;
public interface IPedidoService extends ICrudGenericoService<Pedido, Long> {
    long registrar(Long mesaId,String cliente,List<PedidoItemDTO> items);
    List<Pedido> recientes();
    List<String[]> historial();
    List<PedidoItemDTO> detalle(long id);
    void actualizarEstado(long id,String estado);
}
