package pe.edu.upeu.restaurante.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import pe.edu.upeu.restaurante.components.TableViewHelper;
import pe.edu.upeu.restaurante.model.Pedido;
import pe.edu.upeu.restaurante.service.IPedidoService;
import pe.edu.upeu.restaurante.service.IRestauranteService;

public class ResumenController {
    private final IRestauranteService restaurante;
    private final IPedidoService pedidos;

    @FXML private Label lblPlatos, lblPedidos, lblMesas, lblReservas;
    @FXML private TableView<String[]> tabla;

    public ResumenController(IRestauranteService restaurante, IPedidoService pedidos) {
        this.restaurante = restaurante;
        this.pedidos = pedidos;
    }

    @FXML public void initialize() {
        TableViewHelper.agregarColumna(tabla, "N.º pedido", 0);
        TableViewHelper.agregarColumna(tabla, "Cliente", 1);
        TableViewHelper.agregarColumna(tabla, "Total", 2);
        TableViewHelper.agregarColumna(tabla, "Estado", 3);
        actualizar();
    }

    // Los indicadores se vuelven a consultar a H2 al pulsar Actualizar.
    @FXML private void actualizar() {
        try {
            lblPlatos.setText(restaurante.contar("platos"));
            lblPedidos.setText(restaurante.contar("pedidos"));
            lblMesas.setText(restaurante.contar("mesas"));
            lblReservas.setText(restaurante.contar("reservas"));

            tabla.getItems().clear();
            for (Pedido p : pedidos.recientes()) {
                tabla.getItems().add(new String[] {
                    String.valueOf(p.getId()),
                    p.getCliente() == null ? "Cliente general" : p.getCliente(),
                    "S/ " + p.getTotal().setScale(2, java.math.RoundingMode.HALF_UP),
                    p.getEstado()
                });
            }
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "No se pudo actualizar el resumen: " +
                (ex.getMessage() == null ? "Revisa la conexión H2." : ex.getMessage())).showAndWait();
        }
    }
}
