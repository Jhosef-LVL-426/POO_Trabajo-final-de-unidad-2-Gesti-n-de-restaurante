package pe.edu.upeu.restaurante.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import pe.edu.upeu.restaurante.components.TableViewHelper;
import pe.edu.upeu.restaurante.config.AppContext;
import pe.edu.upeu.restaurante.dto.PedidoItemDTO;
import pe.edu.upeu.restaurante.service.IPedidoService;

public class HistorialController {
    private final IPedidoService service;
    @FXML private TableView<String[]> tabla;
    @FXML private ComboBox<String> cbxEstado;

    public HistorialController(IPedidoService service) { this.service = service; }

    @FXML public void initialize() {
        TableViewHelper.agregarColumna(tabla, "N.º", 0);
        TableViewHelper.agregarColumna(tabla, "Fecha", 1);
        TableViewHelper.agregarColumna(tabla, "Mesa", 2);
        TableViewHelper.agregarColumna(tabla, "Cliente", 3);
        TableViewHelper.agregarColumna(tabla, "Total", 4);
        TableViewHelper.agregarColumna(tabla, "Estado", 5);
        cbxEstado.getItems().setAll("PENDIENTE", "EN PREPARACION", "LISTO", "ENTREGADO", "CANCELADO");

        // Al seleccionar un pedido se muestra su estado actual en el ComboBox.
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado == null) cbxEstado.setValue(null);
            else cbxEstado.setValue(seleccionado[5]);
        });
        actualizarLista();
    }

    @FXML private void actualizarLista() {
        try {
            tabla.getItems().setAll(service.historial());
            tabla.getSelectionModel().clearSelection();
            cbxEstado.setValue(null);
        } catch (Exception ex) { error(ex); }
    }

    @FXML private void actualizarEstado() {
        String[] fila = tabla.getSelectionModel().getSelectedItem();
        String nuevoEstado = cbxEstado.getValue();
        if (fila == null || nuevoEstado == null) {
            informacion("Selecciona un pedido de la tabla y luego su nuevo estado.");
            return;
        }
        if (fila[5].equals(nuevoEstado)) {
            informacion("Ese pedido ya tiene el estado seleccionado.");
            return;
        }
        try {
            service.actualizarEstado(Long.parseLong(fila[0]), nuevoEstado);
            actualizarLista();
            AppContext.getInstance().getBean(MainGuiController.class).estado("Estado del pedido actualizado.");
            informacion("Estado actualizado correctamente.");
        } catch (Exception ex) { error(ex); }
    }

    @FXML private void verDetalle() {
        String[] fila = tabla.getSelectionModel().getSelectedItem();
        if (fila == null) {
            informacion("Primero selecciona un pedido de la tabla.");
            return;
        }
        try {
            StringBuilder texto = new StringBuilder("Pedido N.º " + fila[0] + "\nCliente: " + fila[3] + "\n\n");
            for (PedidoItemDTO item : service.detalle(Long.parseLong(fila[0]))) {
                texto.append(item.getPlato()).append("  x").append(item.getCantidad())
                     .append("  = S/ ").append(item.getSubtotal()).append("\n");
            }
            texto.append("\nTotal: S/ ").append(fila[4]);
            informacion(texto.toString());
        } catch (Exception ex) { error(ex); }
    }

    private void informacion(String texto) {
        new Alert(Alert.AlertType.INFORMATION, texto).showAndWait();
    }
    private void error(Exception ex) {
        new Alert(Alert.AlertType.ERROR,
            ex.getMessage() == null ? "Error al consultar el historial." : ex.getMessage()).showAndWait();
    }
}
