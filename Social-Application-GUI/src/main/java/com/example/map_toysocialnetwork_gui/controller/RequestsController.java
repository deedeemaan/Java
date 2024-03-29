package com.example.map_toysocialnetwork_gui.controller;

import com.example.map_toysocialnetwork_gui.domain.Utilizator;
import com.example.map_toysocialnetwork_gui.service.DBService;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RequestsController implements Observer<ChangeEvent> {
    @FXML
    Button acceptButton;
    @FXML
    Button declineButton;
    @FXML
    TableView<Utilizator> tableView;
    @FXML
    TableColumn<Utilizator, Long> tableColumnID;
    @FXML
    TableColumn<Utilizator, String> tableColumnFirstName;
    @FXML
    TableColumn<Utilizator, String> tableColumnLastName;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();
    private DBService service;
    Stage dialogStage;
//    String username;
    Long id;
    @FXML
    public void initialize(){
        tableColumnID.setCellValueFactory(new PropertyValueFactory<Utilizator, Long>("id"));
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("lastName"));
        tableView.setItems(model);
    }

    public void setService(DBService service, Stage stage, Long id){
        this.service = service;
        this.dialogStage = stage;
        this.id = id;
        service.addObserver(this);
        initModel();
    }
    private void initModel(){
        List<Utilizator> userList = service.getPendingFriendRequests(id);
        System.out.println(userList);
        model.setAll(userList);
    }

    public void handleAcceptRequest(ActionEvent action){
        ObservableList<Utilizator> selected = tableView.getSelectionModel().getSelectedItems();
        if(!(selected.isEmpty())){
            try{
                Utilizator user = selected.get(0);
                service.updateRequest("approved", user.getId(), id);
                LocalDateTime currentDateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                String formattedDateTime = currentDateTime.format(formatter);
                LocalDateTime localDateTime = LocalDateTime.parse(formattedDateTime, formatter);
                service.addFriendship(user.getId(), id, localDateTime);
                initModel();
                Alert.showMessage(null, javafx.scene.control.Alert.AlertType.INFORMATION, null, "Friend request accepted!");
            } catch (Exception e){
                Alert.showErrorMessage(null, e.getMessage());
            }
        }
        else{
            Alert.showErrorMessage(null, "No user selected");
        }
    }

    public void handleRejectRequest(ActionEvent action){
        ObservableList<Utilizator> selected = tableView.getSelectionModel().getSelectedItems();
        if(!(selected.isEmpty())){
            try{
                Utilizator user = selected.get(0);
                service.saveRequest("rejected", user.getId(), id);
                initModel();
                Alert.showMessage(null, javafx.scene.control.Alert.AlertType.INFORMATION, null, "Friend request accepted!");
            } catch (Exception e){
                Alert.showErrorMessage(null, e.getMessage());
            }
        }
        else{
            Alert.showErrorMessage(null, "No user selected");
        }
    }

    @Override
    public void update(ChangeEvent changeEvent) {
        initModel();
    }
}
