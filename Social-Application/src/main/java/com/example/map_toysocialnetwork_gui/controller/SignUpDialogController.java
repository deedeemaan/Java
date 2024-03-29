package com.example.map_toysocialnetwork_gui.controller;

import com.example.map_toysocialnetwork_gui.service.Service;
import com.example.map_toysocialnetwork_gui.validators.ValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignUpDialogController {
    @FXML
    private TextField textFieldFN;
    @FXML
    private TextField textFieldLN;
    @FXML
    private TextField textFieldUsername;
    @FXML
    private PasswordField passwordField;
    private Service service;
    Stage dialogStage;

    @FXML
    public void initialize() {
    }

    public void setService(Service service, Stage dialogStage) {
        this.service = service;
        this.dialogStage = dialogStage;
    }

    public void handleSave() {
        String fn = textFieldFN.getText();
        String ln = textFieldLN.getText();
        String username = textFieldUsername.getText();
        String password = passwordField.getText();
        if (!(fn.isEmpty()) && !(ln.isEmpty()) && !(username.isEmpty()) && !(password.isEmpty())) {
            saveUser(fn, ln, username, password);
        }
        else {
            Alert.showErrorMessage(null, "Cannot be empty!");
        }
    }

    private void saveUser(String fn, String ln, String username, String password) {
        try {
            try {
                service.add(fn, ln, username, password);
                dialogStage.close();
                Alert.showMessage(null, javafx.scene.control.Alert.AlertType.INFORMATION,"Success","Account successfully created!");
            } catch (Exception e) {
                Alert.showErrorMessage(null, "Username already used!");
            }

        } catch (ValidationException e){
            Alert.showErrorMessage(null, e.getMessage());
        }
    }

    @FXML
    public void handleCancel(){
        dialogStage.close();
    }
}
