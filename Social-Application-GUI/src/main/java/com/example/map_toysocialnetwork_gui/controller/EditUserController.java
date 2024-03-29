package com.example.map_toysocialnetwork_gui.controller;

import com.example.map_toysocialnetwork_gui.domain.Utilizator;
import com.example.map_toysocialnetwork_gui.service.DBService;
import com.example.map_toysocialnetwork_gui.validators.ValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditUserController {
    @FXML
    private TextField textFieldFN;
    @FXML
    private TextField textFieldLN;
    @FXML
    private TextField textFieldPW;
    private DBService service;
    Stage dialogStage;
    Utilizator user;


    public void setService(DBService service, Stage stage, Utilizator user) {
        this.service = service;
        this.dialogStage = stage;
        this.user = user;
        if (user != null) {
            setFields(user);
        }
    }

    @FXML
    public void handleSave() {
        String fn = textFieldFN.getText();
        String ln = textFieldLN.getText();
        String pw = textFieldPW.getText();
        if (!(fn.isEmpty()) && !(ln.isEmpty()) && !(pw.isEmpty())) {
            saveUser(fn, ln, pw);
        }
        else {
            Alert.showErrorMessage(null, "First or last name  or password cannot be empty.");
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
            String pw = textFieldPW.getText();
            if (!(fn.isEmpty()) && !(ln.isEmpty()) && !(pw.isEmpty())) {
                updateUser(fn, ln, pw);
            }
        }
    }

    private void saveUser(String fn, String ln, String pw) {
        try {
            try {
                service.add(fn, ln, pw);
                dialogStage.close();
                Alert.showMessage(null, javafx.scene.control.Alert.AlertType.INFORMATION,"Success","User added!");
            } catch (Exception e) {
                Alert.showErrorMessage(null, e.getMessage());
            }

        } catch (ValidationException e){
            Alert.showErrorMessage(null, e.getMessage());
        }
    }

    private void updateUser(String fn, String ln, String pw) {
        try {
            try {
                service.update(user.getId(), fn, ln, pw);
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
        textFieldPW.setText(user.getPassword());
    }

    @FXML
    public void handleCancel(){
        dialogStage.close();
    }
}
