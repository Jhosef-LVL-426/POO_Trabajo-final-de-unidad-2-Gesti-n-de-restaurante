package pe.edu.upeu.restaurante.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import pe.edu.upeu.restaurante.components.TableViewHelper;
import pe.edu.upeu.restaurante.config.AppContext;
import pe.edu.upeu.restaurante.model.Insumo;
import pe.edu.upeu.restaurante.service.IInsumoService;
import java.math.BigDecimal;

public class InsumoController {
    private final IInsumoService service;
    @FXML private TextField txtNombre, txtUnidad, txtStock, txtMinimo;
    @FXML private TableView<String[]> tabla;

    public InsumoController(IInsumoService service) { this.service = service; }

    @FXML public void initialize() {
        TableViewHelper.agregarColumna(tabla, "ID", 0);
        TableViewHelper.agregarColumna(tabla, "Insumo", 1);
        TableViewHelper.agregarColumna(tabla, "Unidad", 2);
        TableViewHelper.agregarColumna(tabla, "Cantidad actual", 3);
        TableViewHelper.agregarColumna(tabla, "Cantidad mínima", 4);
        TableViewHelper.agregarColumna(tabla, "Situación", 5);
        actualizarLista();
    }

    @FXML private void actualizarLista() {
        try {
            tabla.getItems().clear();
            for (Insumo insumo : service.findAll()) {
                // Si el stock llega al mínimo, hay que reponer el insumo.
                String situacion = insumo.getStock().compareTo(insumo.getMinimo()) <= 0
                        ? "REABASTECER" : "SUFICIENTE";
                tabla.getItems().add(new String[] {
                    String.valueOf(insumo.getId()), insumo.getNombre(), insumo.getUnidad(),
                    insumo.getStock().toPlainString(), insumo.getMinimo().toPlainString(), situacion
                });
            }
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    @FXML private void agregar() {
        String nombre = txtNombre.getText().trim();
        String unidad = txtUnidad.getText().trim();
        if (nombre.isEmpty() || unidad.isEmpty()) {
            error("Escribe el nombre del insumo y su unidad (kg, litros o unidades).");
            return;
        }
        if (nombre.length() > 90 || unidad.length() > 20) {
            error("El insumo admite hasta 90 caracteres y su unidad hasta 20.");
            return;
        }
        try {
            BigDecimal stock = leerCantidad(txtStock.getText());
            BigDecimal minimo = leerCantidad(txtMinimo.getText());
            service.save(new Insumo(0, nombre, unidad, stock, minimo));
            txtNombre.clear(); txtUnidad.clear(); txtStock.clear(); txtMinimo.clear();
            actualizarLista();
            AppContext.getInstance().getBean(MainGuiController.class).estado("Insumo guardado.");
            informacion("Insumo guardado. REABASTECER significa que queda poco stock.");
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private BigDecimal leerCantidad(String texto) {
        BigDecimal cantidad;
        try {
            cantidad = new BigDecimal(texto.trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Escribe una cantidad válida, por ejemplo 2,50.");
        }
        if (cantidad.signum() < 0) throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        if (cantidad.stripTrailingZeros().scale() > 2 || cantidad.compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new IllegalArgumentException("La cantidad admite hasta 8 enteros y 2 decimales.");
        }
        return cantidad;
    }

    @FXML private void editarCantidad() {
        String[] fila = tabla.getSelectionModel().getSelectedItem();
        if (fila == null) {
            informacion("Selecciona el insumo que quieres actualizar.");
            return;
        }
        TextInputDialog dialogo = new TextInputDialog(fila[3]);
        dialogo.setTitle("Editar cantidad actual");
        dialogo.setHeaderText(fila[1] + " · Cantidad actual: " + fila[3] + " " + fila[2]);
        dialogo.setContentText("Nueva cantidad total (" + fila[2] + "):");
        dialogo.showAndWait().ifPresent(texto -> {
            try {
                BigDecimal nueva = leerCantidad(texto);
                service.actualizarCantidad(Long.parseLong(fila[0]), new BigDecimal(fila[3]), nueva);
                actualizarLista();
                AppContext.getInstance().getBean(MainGuiController.class).estado("Cantidad actualizada: " + fila[1]);
            } catch (Exception ex) { error(ex.getMessage()); }
        });
    }

    @FXML private void eliminar() {
        String[] fila = tabla.getSelectionModel().getSelectedItem();
        if (fila == null) {
            informacion("Selecciona un insumo de la tabla para eliminarlo.");
            return;
        }
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar el insumo " + fila[1] + "?", ButtonType.YES, ButtonType.NO);
        confirmar.setHeaderText(null);
        if (confirmar.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;
        try {
            service.delete(Long.parseLong(fila[0]));
            actualizarLista();
            AppContext.getInstance().getBean(MainGuiController.class).estado("Insumo eliminado.");
            informacion("Insumo eliminado.");
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void informacion(String texto) { new Alert(Alert.AlertType.INFORMATION, texto).showAndWait(); }
    private void error(String texto) {
        new Alert(Alert.AlertType.ERROR, texto == null ? "No se pudo guardar el insumo." : texto).showAndWait();
    }
}
