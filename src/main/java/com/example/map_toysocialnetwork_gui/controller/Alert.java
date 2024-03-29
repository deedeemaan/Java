package com.example.map_toysocialnetwork_gui.controller;

import javafx.stage.Stage;

public class Alert {
    public static void showMessage(Stage owner, javafx.scene.control.Alert.AlertType type, String header, String text){
        javafx.scene.control.Alert message=new javafx.scene.control.Alert(type);
        message.setHeaderText(header);
        message.setContentText(text);
        message.initOwner(owner);
        message.showAndWait();
    }

    public static void showErrorMessage(Stage owner, String text){
        javafx.scene.control.Alert message=new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        message.initOwner(owner);
        message.setTitle("Mesaj eroare");
        message.setContentText(text);
        message.showAndWait();
    }
}
