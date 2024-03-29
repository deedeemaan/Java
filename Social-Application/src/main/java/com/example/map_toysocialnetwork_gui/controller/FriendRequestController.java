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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

public class FriendRequestController implements Observer<ChangeEvent> {

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
        List<Utilizator> userList = service.getPotentialFriends(service.findByUsername(username).get().getId());
        model.setAll(userList);
    }

    public void handleCancel(){
        dialogStage.close();
    }

    public void handleAddFriend(ActionEvent actionEvent) {
        ObservableList<Utilizator> selected = tableView.getSelectionModel().getSelectedItems();
        if (!(selected.isEmpty())) {
            try {
                service.saveRequest("pending", service.findByUsername(username).get().getId(), selected.get(0).getId());
//                 notifyAll();
                initModel();
                Alert.showMessage(null, javafx.scene.control.Alert.AlertType.INFORMATION, null, "Friend request sent!");
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
