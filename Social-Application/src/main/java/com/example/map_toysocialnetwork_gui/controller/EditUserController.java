package com.example.map_toysocialnetwork_gui.controller;

import com.example.map_toysocialnetwork_gui.domain.Utilizator;
import com.example.map_toysocialnetwork_gui.service.DBService;
import com.example.map_toysocialnetwork_gui.service.Service;
import com.example.map_toysocialnetwork_gui.validators.ValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditUserController {
    @FXML
    private TextField textFieldFN;
    @FXML
    private TextField textFieldLN;
    @FXML
    private TextField textFieldUsername;
    @FXML
    private TextField textFieldPassword;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    private DBService service;
    Stage dialogStage;
    Utilizator user;

    @FXML
    public void initialize() {
    }

    public void setService(DBService service, Stage stage, Utilizator user) {
        this.service = service;
        this.dialogStage = stage;
        this.user = user;
        if (user != null) {
            setFields(user);
            addButton.setDisable(true);
        }
        else {
            updateButton.setDisable(true);
        }
    }

    @FXML
    public void handleSave() {
        String fn = textFieldFN.getText();
        String ln = textFieldLN.getText();
        String username = textFieldUsername.getText();
        String password = textFieldPassword.getText();
        if (!(fn.isEmpty()) && !(ln.isEmpty()) && !(username.isEmpty()) && !(password.isEmpty())) {
            saveUser(fn, ln, username, password);
        }
        else {
            Alert.showErrorMessage(null, "Cannot be empty!");
        }
    }

    @FXML
    void handleUpdate() {
        if (user == null) {
            Alert.showErrorMessage(null, "No user was selected.");
        }
        else {
            String fn = textFieldFN.getText();
            String ln = textFieldLN.getText();
            String username = textFieldUsername.getText();
            String password = textFieldPassword.getText();
            if (!(fn.isEmpty()) && !(ln.isEmpty())) {
                updateUser(fn, ln, username, password);
            }
        }
    }

    private void saveUser(String fn, String ln, String username, String password) {
        try {
            try {
                service.add(fn, ln, username, password);
                dialogStage.close();
                Alert.showMessage(null, javafx.scene.control.Alert.AlertType.INFORMATION,"Success","User added!");
            } catch (Exception e) {
                Alert.showErrorMessage(null, e.getMessage());
            }

        } catch (ValidationException e){
            Alert.showErrorMessage(null, e.getMessage());
        }
    }

    private void updateUser(String fn, String ln, String username, String password) {
        try {
            try {
                service.update(user.getId(), fn, ln, username, password);
                dialogStage.close();
                Alert.showMessage(null, javafx.scene.control.Alert.AlertType.INFORMATION,"Success","User updated!");
            } catch (Exception e) {
                Alert.showErrorMessage(null, e.getMessage());
            }

        } catch (ValidationException e){
            Alert.showErrorMessage(null, e.getMessage());
        }
    }

    private void setFields(Utilizator user) {
        textFieldFN.setText(user.getFirstName());
        textFieldLN.setText(user.getLastName());
        textFieldUsername.setText(user.getUsername());
        textFieldPassword.setText(user.getPassword());
    }

    @FXML
    public void handleCancel(){
        dialogStage.close();
    }
}
