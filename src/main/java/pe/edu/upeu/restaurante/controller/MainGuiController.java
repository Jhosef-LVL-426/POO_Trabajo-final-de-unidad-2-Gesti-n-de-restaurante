package pe.edu.upeu.restaurante.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pe.edu.upeu.restaurante.RestauranteApp;
import pe.edu.upeu.restaurante.config.AppContext;
import pe.edu.upeu.restaurante.model.Perfil;

public class MainGuiController {
    @FXML private BorderPane bp;
    @FXML private TabPane tabPane;
    @FXML private Tab tabPrincipal;
    @FXML private Label lblEstado, lblPerfil;
    @FXML private Menu menuGestion;
    @FXML private MenuItem menuCarta, menuMesas, menuInventario;
    private Perfil perfil;

    public void configurarPerfil(Perfil perfil) {
        this.perfil = perfil;
        lblPerfil.setText("Perfil: " + perfil.getNombre());
        boolean administra = perfil == Perfil.ADMINISTRACION;
        menuCarta.setVisible(administra);
        menuMesas.setVisible(administra);
        menuInventario.setVisible(administra);
        menuGestion.setText(administra ? "Administración" : "Reservas");
    }

    @FXML public void initialize() {
        resumen();
    }

    // FXML: carga la vista del módulo dentro de una pestaña, como en SysVentas.
    private void abrir(String archivo, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + archivo));
            // Inyección de dependencias: AppContext proporciona el controlador y sus servicios.
            loader.setControllerFactory(AppContext.getInstance()::getBean);
            tabPrincipal.setContent(loader.load());
            tabPrincipal.setText(titulo);
            tabPane.getSelectionModel().select(tabPrincipal);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR,
                    "No se pudo abrir la pantalla: " + ex.getMessage()).showAndWait();
        }
    }

    public void estado(String texto) {
        if (lblEstado != null) lblEstado.setText(texto);
    }

    @FXML private void resumen() { abrir("resumen.fxml", "Resumen"); }
    @FXML private void platos() { abrirAdministracion("main_plato.fxml", "Platos"); }
    @FXML private void pedidos() { abrir("main_pedido.fxml", "Registrar pedido"); }
    @FXML private void historial() { abrir("main_historial.fxml", "Historial"); }
    @FXML private void mesas() { abrirAdministracion("main_mesa.fxml", "Mesas"); }
    @FXML private void reservas() { abrir("main_reserva.fxml", "Reservas"); }
    @FXML private void insumos() { abrirAdministracion("main_insumo.fxml", "Inventario"); }

    private void abrirAdministracion(String archivo, String titulo) {
        if (perfil == Perfil.ADMINISTRACION) abrir(archivo, titulo);
    }

    @FXML private void cerrarSesion() {
        perfil = null;
        RestauranteApp.mostrarLogin((Stage) bp.getScene().getWindow());
    }
}
