package pe.edu.upeu.restaurante.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import pe.edu.upeu.restaurante.components.TableViewHelper;
import pe.edu.upeu.restaurante.config.AppContext;
import pe.edu.upeu.restaurante.dto.PedidoItemDTO;
import pe.edu.upeu.restaurante.model.Mesa;
import pe.edu.upeu.restaurante.model.Plato;
import pe.edu.upeu.restaurante.service.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class PedidoController {
    private final IPedidoService pedidos;
    private final IPlatoService platos;
    private final IMesaService mesas;
    @FXML private ComboBox<Mesa> cbxMesa;
    @FXML private ComboBox<Plato> cbxPlato;
    @FXML private TextField txtCliente, txtCantidad;
    @FXML private TableView<String[]> tabla;
    @FXML private Label lblTotal;
    private final List<PedidoItemDTO> items = new ArrayList<>();

    public PedidoController(IPedidoService pedidos, IPlatoService platos, IMesaService mesas) {
        this.pedidos = pedidos;
        this.platos = platos;
        this.mesas = mesas;
    }

    @FXML public void initialize() {
        TableViewHelper.agregarColumna(tabla, "Plato", 0);
        TableViewHelper.agregarColumna(tabla, "Cantidad", 1);
        TableViewHelper.agregarColumna(tabla, "Precio S/", 2);
        TableViewHelper.agregarColumna(tabla, "Subtotal S/", 3);
        tabla.getColumns().forEach(columna -> columna.setSortable(false));

        // StringConverter muestra el nombre del objeto y no su referencia de Java.
        cbxMesa.setConverter(new StringConverter<>() {
            @Override public String toString(Mesa mesa) {
                return mesa == null ? "" : "Mesa " + mesa.getNumero() + " (" + mesa.getCapacidad() + " personas)";
            }
            @Override public Mesa fromString(String texto) { return null; }
        });
        cbxPlato.setConverter(new StringConverter<>() {
            @Override public String toString(Plato plato) {
                return plato == null ? "" : plato.getNombre() + " - S/ " + plato.getPrecio();
            }
            @Override public Plato fromString(String texto) { return null; }
        });
        items.clear();
        tabla.getItems().clear();
        txtCliente.clear();
        txtCantidad.setText("1");
        actualizarTabla();
        actualizarOpciones();
    }

    @FXML private void actualizarOpciones() {
        // Conserva la selección si sigue disponible, y recarga datos desde H2.
        Mesa seleccionMesa = cbxMesa.getValue();
        Plato seleccionPlato = cbxPlato.getValue();
        try {
            cbxMesa.getItems().setAll(mesas.libres());
            cbxPlato.getItems().setAll(platos.disponibles());
            cbxMesa.setValue(null);
            cbxPlato.setValue(null);
            if (seleccionMesa != null) for (Mesa m : cbxMesa.getItems()) {
                if (m.getId() == seleccionMesa.getId()) { cbxMesa.setValue(m); break; }
            }
            if (seleccionPlato != null) for (Plato p : cbxPlato.getItems()) {
                if (p.getId() == seleccionPlato.getId()) { cbxPlato.setValue(p); break; }
            }
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    @FXML private void agregar() {
        Plato plato = cbxPlato.getValue();
        if (plato == null) { error("Selecciona un plato para el pedido."); return; }
        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException ex) {
            error("Escribe una cantidad válida, por ejemplo 1 o 2.");
            return;
        }
        if (cantidad <= 0) { error("La cantidad debe ser mayor que cero."); return; }

        BigDecimal subtotal = plato.getPrecio().multiply(BigDecimal.valueOf(cantidad));
        items.add(new PedidoItemDTO(plato.getId(), plato.getNombre(), cantidad,
            plato.getPrecio(), subtotal));
        actualizarTabla();
        txtCantidad.setText("1");
    }

    @FXML private void quitar() {
        int indice = tabla.getSelectionModel().getSelectedIndex();
        if (indice < 0) {
            informacion("Selecciona un plato de la tabla para quitarlo.");
            return;
        }
        items.remove(indice);
        actualizarTabla();
    }

    private void actualizarTabla() {
        tabla.getItems().clear();
        for (PedidoItemDTO item : items) {
            tabla.getItems().add(new String[] {
                item.getPlato(), String.valueOf(item.getCantidad()),
                item.getPrecio().setScale(2, RoundingMode.HALF_UP).toPlainString(),
                item.getSubtotal().setScale(2, RoundingMode.HALF_UP).toPlainString()
            });
        }
        BigDecimal total = BigDecimal.ZERO;
        for (PedidoItemDTO item : items) total = total.add(item.getSubtotal());
        lblTotal.setText("Total: S/ " + total.setScale(2, RoundingMode.HALF_UP).toPlainString());
    }

    @FXML private void registrar() {
        if (items.isEmpty()) {
            informacion("Primero selecciona un plato y presiona Agregar plato.");
            return;
        }
        String cliente = txtCliente.getText().trim();
        if (cliente.length() > 100) {
            error("El nombre del cliente admite hasta 100 caracteres.");
            return;
        }
        BigDecimal total = items.stream().map(PedidoItemDTO::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (total.compareTo(new BigDecimal("99999999.99")) > 0) {
            error("El importe total del pedido es demasiado alto. Revisa las cantidades.");
            return;
        }
        try {
            Mesa mesa = cbxMesa.getValue();
            long id = pedidos.registrar(mesa == null ? null : mesa.getId(),
                    cliente, List.copyOf(items));
            // Vaciar el formulario solo cuando el pedido sí se guardó.
            items.clear();
            actualizarTabla();
            txtCliente.clear();
            txtCantidad.setText("1");
            cbxMesa.setValue(null);
            cbxPlato.setValue(null);
            actualizarOpciones();
            AppContext.getInstance().getBean(MainGuiController.class).estado("Pedido N.º " + id + " registrado.");
            informacion("Pedido N.º " + id + " registrado correctamente. Puedes verlo en Historial.");
        } catch (Exception ex) {
            error(ex.getMessage() == null ? "No se pudo registrar el pedido." : ex.getMessage());
            actualizarOpciones();
        }
    }

    private void informacion(String texto) {
        new Alert(Alert.AlertType.INFORMATION, texto).showAndWait();
    }
    private void error(String texto) {
        new Alert(Alert.AlertType.ERROR, texto == null ? "Revisa los datos del pedido." : texto).showAndWait();
    }
}
