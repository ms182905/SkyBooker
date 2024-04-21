module org.ms.skybooker {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires static lombok;
    requires com.jfoenix;

    opens org.ms.skybooker to javafx.fxml;
    exports org.ms.skybooker;
    exports org.ms.skybooker.controller;
    opens org.ms.skybooker.controller to javafx.fxml;
}