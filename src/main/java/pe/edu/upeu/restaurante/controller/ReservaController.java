package pe.edu.upeu.restaurante.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import pe.edu.upeu.restaurante.components.TableViewHelper;
import pe.edu.upeu.restaurante.config.AppContext;
import pe.edu.upeu.restaurante.model.Reserva;
import pe.edu.upeu.restaurante.service.IReservaService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class ReservaController {
    private static final DateTimeFormatter HORA_RESERVA =
        DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);
    private final IReservaService service;
    @FXML private TextField txtCliente, txtTelefono, txtHora, txtPersonas;
    @FXML private DatePicker dpFecha;
    @FXML private TableView<String[]> tabla;

    public ReservaController(IReservaService service) { this.service = service; }

    @FXML public void initialize() {
        TableViewHelper.agregarColumna(tabla, "ID", 0);
        TableViewHelper.agregarColumna(tabla, "Cliente", 1);
        TableViewHelper.agregarColumna(tabla, "Teléfono", 2);
        TableViewHelper.agregarColumna(tabla, "Fecha", 3);
        TableViewHelper.agregarColumna(tabla, "Hora", 4);
        TableViewHelper.agregarColumna(tabla, "Personas", 5);
        TableViewHelper.agregarColumna(tabla, "Estado", 6);
        dpFecha.setValue(LocalDate.now());
        txtPersonas.setText("2");
        actualizarLista();
    }

    @FXML private void actualizarLista() {
        try {
            tabla.getItems().clear();
            for (Reserva r : service.findAll()) {
                tabla.getItems().add(new String[] {
                    String.valueOf(r.getId()), r.getCliente(),
                    r.getTelefono() == null || r.getTelefono().isBlank() ? "-" : r.getTelefono(),
                    r.getFecha().toString(), r.getHora(),
                    String.valueOf(r.getPersonas()), r.getEstado()
                });
            }
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    @FXML private void agregar() {
        String cliente = txtCliente.getText().trim();
        String telefono = txtTelefono.getText().trim();
        LocalDate fecha = dpFecha.getValue();
        if (cliente.isEmpty() || fecha == null || txtHora.getText().isBlank()) {
            error("Completa el nombre del cliente, la fecha y la hora.");
            return;
        }
        if (cliente.length() > 100 || telefono.length() > 20) {
            error("El nombre admite hasta 100 caracteres y el teléfono hasta 20.");
            return;
        }
        if (fecha.isBefore(LocalDate.now())) {
            error("La fecha de reserva no puede ser anterior a hoy.");
            return;
        }
        String hora = txtHora.getText().trim();
        // LocalTime comprueba horas reales; se guarda en formato HH:mm.
        try {
            hora = LocalTime.parse(hora, HORA_RESERVA).format(HORA_RESERVA);
        } catch (DateTimeParseException ex) {
            error("Escribe la hora en formato 24 horas, por ejemplo 19:30.");
            return;
        }
        int personas;
        try {
            personas = Integer.parseInt(txtPersonas.getText().trim());
        } catch (NumberFormatException ex) {
            error("Escribe la cantidad de personas con un número entero, por ejemplo 2.");
            return;
        }
        if (personas <= 0) {
            error("La cantidad de personas debe ser mayor que cero.");
            return;
        }
        try {
            service.save(new Reserva(0, cliente, telefono,
                    fecha, hora, personas, "CONFIRMADA"));
            txtCliente.clear();
            txtTelefono.clear();
            txtHora.clear();
            txtPersonas.setText("2");
            dpFecha.setValue(LocalDate.now());
            actualizarLista();
            AppContext.getInstance().getBean(MainGuiController.class).estado("Reserva registrada.");
            informacion("Reserva registrada correctamente.");
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    @FXML private void cancelar() { cerrarReserva(false); }
    @FXML private void finalizar() { cerrarReserva(true); }

    private void cerrarReserva(boolean finalizar) {
        String[] fila = tabla.getSelectionModel().getSelectedItem();
        if (fila == null) { informacion("Selecciona una reserva de la tabla."); return; }
        if (!"CONFIRMADA".equals(fila[6])) {
            informacion("Solo puedes cambiar una reserva confirmada. Estado actual: " + fila[6]);
            return;
        }
        String accion = finalizar ? "Finalizar" : "Cancelar";
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                "¿" + accion + " la reserva de " + fila[1] + "?", ButtonType.YES, ButtonType.NO);
        confirmar.setHeaderText(null);
        if (confirmar.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;
        try {
            long id = Long.parseLong(fila[0]);
            if (finalizar) service.finalizar(id);
            else service.cancelar(id);
            actualizarLista();
            AppContext.getInstance().getBean(MainGuiController.class)
                    .estado(finalizar ? "Reserva finalizada." : "Reserva cancelada.");
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void informacion(String texto) { new Alert(Alert.AlertType.INFORMATION, texto).showAndWait(); }
    private void error(String texto) {
        new Alert(Alert.AlertType.ERROR, texto == null ? "Revisa los datos de la reserva." : texto).showAndWait();
    }
}
