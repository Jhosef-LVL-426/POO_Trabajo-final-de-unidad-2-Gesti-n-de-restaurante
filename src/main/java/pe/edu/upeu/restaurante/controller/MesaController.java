package pe.edu.upeu.restaurante.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import pe.edu.upeu.restaurante.components.TableViewHelper;
import pe.edu.upeu.restaurante.model.Mesa;
import pe.edu.upeu.restaurante.config.AppContext;
import pe.edu.upeu.restaurante.service.IMesaService;

public class MesaController {
    private final IMesaService service;
    @FXML private TextField txtNumero,txtCapacidad;
    @FXML private TableView<String[]> tabla;
    public MesaController(IMesaService service){this.service=service;}
    @FXML public void initialize(){
        TableViewHelper.agregarColumna(tabla,"ID",0);
        TableViewHelper.agregarColumna(tabla,"Mesa",1);
        TableViewHelper.agregarColumna(tabla,"Capacidad",2);
        TableViewHelper.agregarColumna(tabla,"Estado",3);
        listar();
    }
    private void listar(){
        tabla.getItems().clear();
        for(Mesa m:service.findAll())tabla.getItems().add(new String[]{String.valueOf(m.getId()),String.valueOf(m.getNumero()),String.valueOf(m.getCapacidad()),m.getEstado()});
    }
    @FXML private void agregar(){
        try{
            int numero=Integer.parseInt(txtNumero.getText().trim()),capacidad=Integer.parseInt(txtCapacidad.getText().trim());
            if(numero<1||capacidad<1)throw new IllegalArgumentException("Número y capacidad deben ser mayores que cero.");
            if (service.findAll().stream().anyMatch(mesa -> mesa.getNumero() == numero))
                throw new IllegalArgumentException("Ya existe una mesa con ese número.");
            service.save(new Mesa(0, numero, capacidad, "LIBRE"));
            txtNumero.clear();
            txtCapacidad.clear();
            listar();
            estado("Mesa registrada.");
        } catch (NumberFormatException ex) {
            error(new IllegalArgumentException("Escribe el número y la capacidad en números enteros."));
        } catch (Exception ex) { error(ex); }
    }
    @FXML private void cambiar(){
        String[] row = tabla.getSelectionModel().getSelectedItem();
        if (row == null) {
            new Alert(Alert.AlertType.INFORMATION, "Selecciona una mesa de la tabla.").showAndWait();
            return;
        }
        try {
            long id=Long.parseLong(row[0]);Mesa m=service.findById(id);
            String estado=m.getEstado().equals("LIBRE")?"OCUPADA":"LIBRE";
            service.update(id,new Mesa(m.getId(),m.getNumero(),m.getCapacidad(),estado));
            listar();
            estado("Estado de la mesa actualizado.");
        }catch(Exception ex){error(ex);}
    }
    @FXML private void eliminar(){
        String[] row = tabla.getSelectionModel().getSelectedItem();
        if (row == null) {
            new Alert(Alert.AlertType.INFORMATION, "Selecciona una mesa de la tabla.").showAndWait();
            return;
        }
        Alert a=new Alert(Alert.AlertType.CONFIRMATION,"¿Eliminar mesa "+row[1]+"?",ButtonType.YES,ButtonType.NO);
        a.setHeaderText(null);
        if(a.showAndWait().orElse(ButtonType.NO)!=ButtonType.YES)return;
        try {
            if (service.eliminarSiLibre(Long.parseLong(row[0]))) {
                listar();
                estado("Mesa eliminada.");
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Solo se pueden eliminar mesas libres.").showAndWait();
            }
        }catch(Exception ex){error(ex);}
    }
    private void estado(String mensaje) {
        AppContext.getInstance().getBean(MainGuiController.class).estado(mensaje);
    }
    private void error(Exception ex){new Alert(Alert.AlertType.ERROR,ex.getMessage()==null?"Error de mesa.":ex.getMessage()).showAndWait();}
}
