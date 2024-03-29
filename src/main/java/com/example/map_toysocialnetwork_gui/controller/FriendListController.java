package com.example.map_toysocialnetwork_gui.controller;

import com.example.map_toysocialnetwork_gui.domain.Prietenie;
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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendListController implements Observer<ChangeEvent> {
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
    TextField nrOfElemOnPageTextField;

    @FXML
    private Button prevButton;

    @FXML
    private Button nextButton;

    private int nrElemPerPage = 4;
    private int currentPage = 0;
    private int totalNrOfElems = 0;

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
        nrOfElemOnPageTextField.setText(String.valueOf(nrElemPerPage));
        service.addObserver(this);
        initModel();
    }
    private void initModel(){
        Page<Prietenie> page = service.findAllOnPageFriendships(new Pageable(currentPage, nrElemPerPage));
        int maxPage = (int) Math.ceil((double) page.getTotalNrOfElems() / nrElemPerPage) - 1;
        if (maxPage < 0)
            maxPage = 0;
        if (currentPage > maxPage){
            currentPage = maxPage;
            page = service.findAllOnPageFriendships(new Pageable(currentPage, nrElemPerPage));
        }

//        model.setAll(StreamSupport.stream(page.getElemsOnPage().spliterator(), false).collect(Collectors.toList()));
        totalNrOfElems = page.getTotalNrOfElems();

        prevButton.setDisable(currentPage == 0);
        nextButton.setDisable((currentPage + 1) * nrElemPerPage >= totalNrOfElems);
    }

    @Override
    public void update(ChangeEvent changeEvent) {
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
