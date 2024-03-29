module com.example.map_toysocialnetwork_gui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
//    requires jdk.jshell;
    requires java.sql;

    opens com.example.map_toysocialnetwork_gui to javafx.fxml;
    exports com.example.map_toysocialnetwork_gui;

    opens com.example.map_toysocialnetwork_gui.controller to javafx.fxml;
    exports com.example.map_toysocialnetwork_gui.controller;
    opens com.example.map_toysocialnetwork_gui.domain;
    exports com.example.map_toysocialnetwork_gui.domain;
    opens com.example.map_toysocialnetwork_gui.service;
    exports com.example.map_toysocialnetwork_gui.service;

}