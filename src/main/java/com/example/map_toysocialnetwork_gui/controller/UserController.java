package com.example.map_toysocialnetwork_gui.controller;

import com.example.map_toysocialnetwork_gui.SocialNetworkApplication;
import com.example.map_toysocialnetwork_gui.domain.Utilizator;
import com.example.map_toysocialnetwork_gui.repository.paging.Page;
import com.example.map_toysocialnetwork_gui.repository.paging.Pageable;
import com.example.map_toysocialnetwork_gui.service.DBService;
import com.example.map_toysocialnetwork_gui.utils.events.ChangeEvent;
import com.example.map_toysocialnetwork_gui.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements Observer<ChangeEvent> {
    DBService service;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();

    @FXML
    TableView<Utilizator> tableView;
    @FXML
    TableColumn<Utilizator,Long> tableColumnID;
    @FXML
    TableColumn<Utilizator,String> tableColumnFirstName;
    @FXML
    TableColumn<Utilizator,String> tableColumnLastName;
    @FXML
    TableColumn<Utilizator,String> tableColumnPassword;
    @FXML
    TextField nrOfElemOnPageTextField;

    @FXML
    private Button prevButton;

    @FXML
    private Button nextButton;

    private int nrElemPerPage = 4;
    private int currentPage = 0;
    private int totalNrOfElems = 0;

    public void setUserService(DBService service) {
        this.service = service;
        service.addObserver(this);
        nrOfElemOnPageTextField.setText(String.valueOf(nrElemPerPage));
        initModel();
    }

    @FXML
    public void initialize() {
        tableColumnID.setCellValueFactory(new PropertyValueFactory<Utilizator, Long>("id"));
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        tableColumnPassword.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("password"));
        tableView.setItems(model);
    }

    private void initModel() {
//        Iterable<Utilizator> users = service.getAll();
//        List<Utilizator> userList = StreamSupport.stream(users.spliterator(), false).collect(Collectors.toList());
//
//        model.setAll(userList);


        Page<Utilizator> page = service.findAllOnPage(new Pageable(currentPage, nrElemPerPage));
        int maxPage = (int) Math.ceil((double) page.getTotalNrOfElems() / nrElemPerPage) - 1;
        if (maxPage < 0)
            maxPage = 0;
        if (currentPage > maxPage){
            currentPage = maxPage;
            page = service.findAllOnPage(new Pageable(currentPage, nrElemPerPage));
        }

        model.setAll(StreamSupport.stream(page.getElemsOnPage().spliterator(), false).collect(Collectors.toList()));
        totalNrOfElems = page.getTotalNrOfElems();

        prevButton.setDisable(currentPage == 0);
        nextButton.setDisable((currentPage + 1) * nrElemPerPage >= totalNrOfElems);
    }

    public void handleDeleteUser(ActionEvent actionEvent) {
        ObservableList<Utilizator> selected = tableView.getSelectionModel().getSelectedItems();
        if (!(selected.isEmpty())) {
             service.remove(selected.get(0).getId());
                Alert.showMessage(null, javafx.scene.control.Alert.AlertType.INFORMATION, "Delete", "User was successfully deleted!");

        } else {
            Alert.showErrorMessage(null, "No user selected!");
        }
    }

    public void handleUpdateUser(ActionEvent actionEvent) {

        ObservableList<Utilizator> selected = tableView.getSelectionModel().getSelectedItems();
        if (!(selected.isEmpty())) {
            showUserEditDialog(selected.get(0));
        }
        else {
            Alert.showErrorMessage(null, "No user selected!");
        }
    }

    public void handleAddUser(ActionEvent actionEvent) {
        showUserEditDialog(null);
    }

    private void showUserEditDialog(Utilizator user) {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/edituser-view.fxml"));
            AnchorPane root =(AnchorPane)loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("User Edit Dialog");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            EditUserController editUserController = loader.getController();
            editUserController.setService(service, dialogStage, user);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showFriendRequests(Utilizator user){
        try{
            FXMLLoader loader = new FXMLLoader(SocialNetworkApplication.class.getResource("/friendrequest-view.fxml"));
//            loader.setLocation(getClass().getResource("/friendrequest.fxml"));
            AnchorPane root =(AnchorPane)loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friend Suggestions");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            FriendRequestController friendRequest = loader.getController();
//            System.out.println(dialogStage.toString());
//            System.out.println(user.getId());
            friendRequest.setService(service, dialogStage, user.getId());

            dialogStage.setWidth(800);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void handleFriendRequest(ActionEvent actionEvent) {

        ObservableList<Utilizator> selected = tableView.getSelectionModel().getSelectedItems();
        if (!(selected.isEmpty())) {
            System.out.println(selected.get(0));
            showFriendRequests(selected.get(0));
        }
        else {
            Alert.showErrorMessage(null, "No user selected!");
        }
    }

    private void showPendingRequests(Utilizator user){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/requests-view.fxml"));
            AnchorPane root =(AnchorPane)loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Pending Friend Requests");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            RequestsController pendingFriendRequest = loader.getController();
            pendingFriendRequest.setService(service, dialogStage, user.getId());

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void handlePendingFriendRequest(ActionEvent actionEvent) {

        ObservableList<Utilizator> selected = tableView.getSelectionModel().getSelectedItems();
        if (!(selected.isEmpty())) {
            showPendingRequests(selected.get(0));
        }
        else {
            Alert.showErrorMessage(null, "No user selected!");
        }
    }

    private void showFriends(Utilizator user) {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/friendslist.fxml"));
            AnchorPane root =(AnchorPane)loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friends List");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            FriendListController editUserController = loader.getController();
            editUserController.setService(service, dialogStage, user.getId());

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void handleFriendsList(ActionEvent actionEvent) {

        ObservableList<Utilizator> selected = tableView.getSelectionModel().getSelectedItems();
        if (!(selected.isEmpty())) {
            showFriends(selected.get(0));
        }
        else {
            Alert.showErrorMessage(null, "No user selected!");
        }
    }

    @Override
    public void update(ChangeEvent utilizatorChangeEvent) {
        initModel();
    }

    public void onPressPrevButton(ActionEvent actionEvent) {
        currentPage--;
        initModel();
    }

    public void onPressNextButton(ActionEvent actionEvent) {
        currentPage++;
        initModel();
    }

    public void textFieldChanged(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER){
            nrElemPerPage = Integer.parseInt(nrOfElemOnPageTextField.getText());
            initModel();
        }
    }
}
