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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendRequestController implements Observer<ChangeEvent> {

    @FXML
    private Button addButton;
    @FXML
    TableView<Utilizator> tableView2;
    @FXML
    TableColumn<Utilizator, Long> tableColumnID;
    @FXML
    TableColumn<Utilizator, String> tableColumnFirstName;
    @FXML
    TableColumn<Utilizator, String> tableColumnLastName;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();
    private DBService service;
    Stage dialogStage;
    Long id;
    @FXML
    public void initialize(){
        tableColumnID.setCellValueFactory(new PropertyValueFactory<Utilizator, Long>("id"));
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("lastName"));
        tableView2.setItems(model);
    }

    public void setService(DBService service, Stage stage, Long id){
        this.service = service;
        this.dialogStage = stage;
        this.id = id;
        service.addObserver(this);
        //initialize();
        initModel();

//        System.out.println(model.get(0).toString());
//        System.out.println("YELLOOOOWW");
    }
    private void initModel(){
        Iterable<Utilizator> users = service.getPotentialFriends(id);
        List<Utilizator> userList = StreamSupport.stream(users.spliterator(), false).collect(Collectors.toList());
//        List<Utilizator> userList = service.getPotentialFriends(id);
//        System.out.println("AJDKAEIHEAWHOAWHOAHO!!!!!!111");
//        System.out.println(userList);
        model.setAll(userList);
//        System.out.println(model.toString());
    }

    public void handleAddFriend(ActionEvent action){
        ObservableList<Utilizator> selected = tableView2.getSelectionModel().getSelectedItems();
        if(!(selected.isEmpty())){
            try{
                service.saveRequest("pending", id, selected.get(0).getId());
                initModel();
                Alert.showMessage(null, javafx.scene.control.Alert.AlertType.INFORMATION, null, "Friend request sent!");
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
