module org.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires com.google.gson;
    requires com.sothawo.mapjfx;
    requires com.fasterxml.jackson.databind;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens org.example.demo to javafx.fxml, com.google.gson;
    exports org.example.demo;
    exports DB;
    opens DB to com.google.gson, javafx.fxml;
    exports Components;
    opens Components to com.google.gson, javafx.fxml;
    exports Serwer;
}