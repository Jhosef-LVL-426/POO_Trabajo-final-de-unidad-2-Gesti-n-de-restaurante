module pe.edu.upeu.restaurante {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.h2database;
    requires org.slf4j;
    requires com.zaxxer.hikari;
    opens pe.edu.upeu.restaurante to javafx.fxml;
    opens pe.edu.upeu.restaurante.controller to javafx.fxml;
    opens pe.edu.upeu.restaurante.model;
    exports pe.edu.upeu.restaurante;
}
