package pe.edu.upeu.restaurante.components;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/** Ayuda reutilizable para mostrar filas de texto en las tablas del sistema. */
public final class TableViewHelper {
    private TableViewHelper() { }

    public static void agregarColumna(TableView<String[]> tabla, String titulo, int indice) {
        if (tabla.getColumns().isEmpty()) {
            tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        }
        TableColumn<String[], String> columna = new TableColumn<>(titulo);
        columna.setCellValueFactory(fila -> new SimpleStringProperty(
                indice < fila.getValue().length ? fila.getValue()[indice] : ""));
        tabla.getColumns().add(columna);
    }
}
