package com.example.map_toysocialnetwork_gui.controller;

import com.example.map_toysocialnetwork_gui.domain.Utilizator;
import com.example.map_toysocialnetwork_gui.service.DBService;
import com.example.map_toysocialnetwork_gui.service.Service;
import com.example.map_toysocialnetwork_gui.utils.events.ChangeEvent;
import com.example.map_toysocialnetwork_gui.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PendingFriendRequestsController implements Observer<ChangeEvent>{
    ObservableList<Utilizator> model = FXCollections.observableArrayList();
    private DBService service;
    Stage dialogStage;
    String username;
    @FXML
    TableView<Utilizator> tableView;
    @FXML
    TableColumn<Utilizator,Long> tableColumnID;
    @FXML
    TableColumn<Utilizator,String> tableColumnFirstName;
    @FXML
    TableColumn<Utilizator,String> tableColumnLastName;

    @FXML
    public void initialize() {
        tableColumnID.setCellValueFactory(new PropertyValueFactory<Utilizator, Long>("id"));
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        tableView.setItems(model);
    }

    public void setService(DBService service, Stage stage, String username) {
        this.service = service;
        this.dialogStage = stage;
        this.username = username;
        service.addObserver(this);
        initModel();
    }

    private void initModel() {
        // model.clear();
        List<Utilizator> userList = service.getPendingFriendRequests(service.findByUsername(username).get().getId());
        model.setAll(userList);
    }


    public void handleAcceptRequest(ActionEvent actionEvent) {
        ObservableList<Utilizator> selected = tableView.getSelectionModel().getSelectedItems();
        if (!(selected.isEmpty())) {
            try {
                Utilizator user = selected.get(0);
                service.saveRequest("approved", user.getId(), service.findByUsername(username).get().getId());
                LocalDateTime currentDateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                String formattedDateTime = currentDateTime.format(formatter);
                LocalDateTime localDateTime = LocalDateTime.parse(formattedDateTime, formatter);
                service.addFriendship(user.getId(), service.findByUsername(username).get().getId(), localDateTime);
                initModel();
                Alert.showMessage(null, javafx.scene.control.Alert.AlertType.INFORMATION, null, "Friend request accepted!");
            } catch (Exception e) {
                Alert.showErrorMessage(null, e.getMessage());
            }

        } else {
            Alert.showErrorMessage(null, "No user selected!");
        }
    }

    public void handleRejectRequest(ActionEvent actionEvent) {
        ObservableList<Utilizator> selected = tableView.getSelectionModel().getSelectedItems();
        if (!(selected.isEmpty())) {
            try {
                service.saveRequest("rejected", selected.get(0).getId(), service.findByUsername(username).get().getId());
                initModel();
                Alert.showMessage(null, javafx.scene.control.Alert.AlertType.INFORMATION, null, "Friend request rejected!");
            } catch (Exception e) {
                Alert.showErrorMessage(null, e.getMessage());
            }

        } else {
            Alert.showErrorMessage(null, "No user selected!");
        }
    }

    @Override
    public void update(ChangeEvent changeEvent) {
        initModel();
    }
}
