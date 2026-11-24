package pe.edu.upeu.restaurante;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import pe.edu.upeu.restaurante.config.AppContext;
import pe.edu.upeu.restaurante.config.DatabaseConfig;
import pe.edu.upeu.restaurante.controller.MainGuiController;
import pe.edu.upeu.restaurante.model.Perfil;

/** Entrada JavaFX: carga pantallas FXML igual que SysVentas. */
public class RestauranteApp extends Application {
    @Override public void start(Stage stage) {
        try {
            DatabaseConfig.init();
            mostrarLogin(stage);
        } catch(Exception ex) {
            Alert alert=new Alert(Alert.AlertType.ERROR,"No se pudo iniciar el restaurante: "+ex.getMessage());
            alert.showAndWait();
        }
    }
    public static void mostrarLogin(Stage stage) {
        stage.setMinWidth(450);
        stage.setMinHeight(300);
        cargar(stage, "/view/login.fxml", 460, 310, null);
        stage.setResizable(true);
        stage.centerOnScreen();
    }
    public static void mostrarPrincipal(Stage stage, Perfil perfil) {
        cargar(stage, "/view/maingui.fxml", 1050, 700, perfil);
        stage.setMinWidth(960);stage.setMinHeight(600);
        stage.centerOnScreen();
    }
    private static void cargar(Stage stage,String ruta,int ancho,int alto, Perfil perfil) {
        try {
            FXMLLoader loader=new FXMLLoader(RestauranteApp.class.getResource(ruta));
            // FXML recibe los controladores creados en AppContext (igual que SysVentas).
            loader.setControllerFactory(AppContext.getInstance()::getBean);
            Scene scene=new Scene(loader.load(),ancho,alto);
            if (perfil != null) {
                loader.<MainGuiController>getController().configurarPerfil(perfil);
            }
            scene.getStylesheets().add(RestauranteApp.class.getResource("/css/style.css").toExternalForm());
            stage.setTitle("Mesa | Gestión de Restaurante");
            stage.setScene(scene);
            stage.sizeToScene();
            stage.show();
        }catch(Exception ex){throw new IllegalStateException("Error cargando "+ruta,ex);}
    }
    @Override public void stop() {DatabaseConfig.shutdown();}
    public static void main(String[] args){launch(args);}
}
