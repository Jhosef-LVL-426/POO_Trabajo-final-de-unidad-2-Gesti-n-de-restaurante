package pe.edu.upeu.restaurante.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.restaurante.RestauranteApp;
import pe.edu.upeu.restaurante.model.Perfil;
import pe.edu.upeu.restaurante.service.AutenticacionService;

import java.util.Arrays;
import java.util.Optional;

public class LoginController {
    private final AutenticacionService autenticacion;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnIngresar;

    public LoginController(AutenticacionService autenticacion) {
        this.autenticacion = autenticacion;
    }

    @FXML private void ingresar() {
        String usuario = txtUsuario.getText();
        char[] clave = txtPassword.getText().toCharArray();
        try {
            if (usuario.isBlank() || clave.length == 0) {
                new Alert(Alert.AlertType.INFORMATION, "Completa el usuario y la contraseña.").showAndWait();
                return;
            }
            Optional<Perfil> perfil = autenticacion.identificar(usuario, clave);
            txtPassword.clear();
            if (perfil.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "Usuario o contraseña incorrectos.").showAndWait();
                return;
            }
            RestauranteApp.mostrarPrincipal((Stage) btnIngresar.getScene().getWindow(), perfil.get());
        } catch (IllegalStateException e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo iniciar sesión.").showAndWait();
        } finally {
            Arrays.fill(clave, '\0');
        }
    }
}
