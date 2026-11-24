package pe.edu.upeu.restaurante.controller;

import javafx.fxml.FXML;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import pe.edu.upeu.restaurante.model.Plato;
import pe.edu.upeu.restaurante.components.TableViewHelper;
import pe.edu.upeu.restaurante.service.IPlatoService;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

public class PlatoController {
    private final IPlatoService service;
    private List<Plato> platos = List.of();
    @FXML private TextField txtBuscar;
    private long editando=-1L;
    @FXML private TextField txtNombre,txtCategoria,txtPrecio;
    @FXML private CheckBox chkDisponible;
    @FXML private TableView<String[]> tabla;

    public PlatoController(IPlatoService service){this.service=service;}
    @FXML public void initialize(){
        TableViewHelper.agregarColumna(tabla,"ID",0);
        TableViewHelper.agregarColumna(tabla,"Nombre",1);
        TableViewHelper.agregarColumna(tabla,"Categoría",2);
        // Personaliza el color del precio mostrado sin cambiar el valor guardado en H2.
        TableColumn<String[], String> columnaPrecio = new TableColumn<>("Precio");
        columnaPrecio.setCellValueFactory(datos ->
                new SimpleStringProperty(datos.getValue()[3]));
        columnaPrecio.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(String precio, boolean vacia) {
                super.updateItem(precio, vacia);
                if (vacia || precio == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(precio);
                BigDecimal valor = new BigDecimal(precio);
                if (valor.compareTo(new BigDecimal("21.00")) >= 0) {
                    setStyle("-fx-text-fill: red;");
                } else if (valor.compareTo(new BigDecimal("20.00")) <= 0) {
                    setStyle("-fx-text-fill: #B8860B;");
                } else {
                    setStyle("");
                }
            }
        });
        tabla.getColumns().add(columnaPrecio);
        TableViewHelper.agregarColumna(tabla,"Disponibilidad",4);
        tabla.setRowFactory(tv->{
            TableRow<String[]> row=new TableRow<>();
            row.setOnMouseClicked(e->{if(e.getClickCount()==2&&!row.isEmpty())editar(row.getItem());});
            return row;
        });
        txtBuscar.textProperty().addListener((campo, anterior, actual) -> filtrar());
        limpiar();listar();
    }
    private void listar(){
        platos = service.findAll();
        filtrar();
    }

    private void filtrar() {
        String busqueda = normalizar(txtBuscar.getText());
        tabla.getItems().clear();
        for (Plato plato : platos) {
            if (normalizar(plato.getNombre()).contains(busqueda)) {
                tabla.getItems().add(new String[] {
                    String.valueOf(plato.getId()), plato.getNombre(), plato.getCategoria(),
                    plato.getPrecio().toPlainString(), plato.isDisponible() ? "Disponible" : "No disponible"
                });
            }
        }
        tabla.sort();
    }

    private static String normalizar(String texto) {
        // NFD separa cada vocal de su tilde para comparar una copia del nombre.
        String separado = Normalizer.normalize(texto.strip(), Normalizer.Form.NFD);
        return separado.replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT);
    }
    private void editar(String[] row){
        editando=Long.parseLong(row[0]);txtNombre.setText(row[1]);txtCategoria.setText(row[2]);
        txtPrecio.setText(row[3]);chkDisponible.setSelected(row[4].equals("Disponible"));
    }
    @FXML private void limpiar(){
        editando=-1;txtNombre.clear();txtCategoria.clear();txtPrecio.clear();chkDisponible.setSelected(true);
    }
    @FXML private void guardar(){
        try {
            String nombre = txtNombre.getText().trim();
            String categoria = txtCategoria.getText().trim();
            if (nombre.isBlank() || categoria.isBlank())
                throw new IllegalArgumentException("Completa nombre y categoría.");
            if (nombre.length() > 90 || categoria.length() > 40)
                throw new IllegalArgumentException("El nombre admite hasta 90 caracteres y la categoría hasta 40.");
            BigDecimal precio=new BigDecimal(txtPrecio.getText().trim().replace(',','.'));
            if(precio.signum()<0)throw new IllegalArgumentException("El precio no puede ser negativo.");
            if (precio.stripTrailingZeros().scale() > 2 || precio.compareTo(new BigDecimal("99999999.99")) > 0)
                throw new IllegalArgumentException("Escribe un precio de hasta 8 enteros y 2 decimales.");
            Plato p = new Plato(editando, nombre, categoria, precio, chkDisponible.isSelected());
            if(editando<0)service.save(p);else service.update(editando,p);
            limpiar();listar();estado("Plato guardado.");
        } catch (NumberFormatException ex) {
            error(new IllegalArgumentException("Escribe un precio válido, por ejemplo 18.50."));
        } catch (Exception ex) { error(ex); }
    }
    @FXML private void eliminar(){
        String[] row = tabla.getSelectionModel().getSelectedItem();
        if (row == null) {
            new Alert(Alert.AlertType.INFORMATION, "Selecciona un plato de la tabla.").showAndWait();
            return;
        }
        Alert a=new Alert(Alert.AlertType.CONFIRMATION,"¿Eliminar el plato "+row[1]+"?",ButtonType.YES,ButtonType.NO);
        a.setHeaderText(null);
        if(a.showAndWait().orElse(ButtonType.NO)!=ButtonType.YES)return;
        try {
            long id = Long.parseLong(row[0]);
            service.delete(id);
            if (id == editando) limpiar();
            listar();
            estado("Plato eliminado.");
        }
        catch(Exception ex){error(ex);}
    }
    private void estado(String texto){pe.edu.upeu.restaurante.config.AppContext.getInstance().getBean(MainGuiController.class).estado(texto);}
    private void error(Exception ex){new Alert(Alert.AlertType.ERROR,ex.getMessage()==null?"No se pudo completar la operación.":ex.getMessage()).showAndWait();}
}
