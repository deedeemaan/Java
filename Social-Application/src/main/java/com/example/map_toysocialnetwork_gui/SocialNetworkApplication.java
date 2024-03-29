package com.example.map_toysocialnetwork_gui;

import com.example.map_toysocialnetwork_gui.controller.UserController;
import com.example.map_toysocialnetwork_gui.repository.FriendReqDBRepository;
import com.example.map_toysocialnetwork_gui.repository.FriendshipsDBRepository;
import com.example.map_toysocialnetwork_gui.repository.MessageRepository;
import com.example.map_toysocialnetwork_gui.repository.UtilizatorDBRepository;
import com.example.map_toysocialnetwork_gui.service.DBService;
import com.example.map_toysocialnetwork_gui.service.Service;
import com.example.map_toysocialnetwork_gui.validators.LocalDateTimeValidator;
import com.example.map_toysocialnetwork_gui.validators.PrietenieValidator;
import com.example.map_toysocialnetwork_gui.validators.RequestValidator;
import com.example.map_toysocialnetwork_gui.validators.UtilizatorValidator;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SocialNetworkApplication extends Application {

    UtilizatorDBRepository userRepo;
    FriendshipsDBRepository friendshipsDBRepository;
    FriendReqDBRepository requestsRepo;

    MessageRepository messagesRepo;
    DBService service;


    public static void main(String[] args) {
        launch();
    }

    public void start(Stage stage) throws IOException {
        userRepo = new UtilizatorDBRepository(new UtilizatorValidator());
        friendshipsDBRepository = new FriendshipsDBRepository(new PrietenieValidator(), new LocalDateTimeValidator());
        requestsRepo = new FriendReqDBRepository(new RequestValidator());
        messagesRepo = new MessageRepository(userRepo);
        service = new DBService(userRepo, friendshipsDBRepository, messagesRepo, requestsRepo);

        initView(stage);
        stage.setWidth(800);
        stage.show();
    }

    private void initView(Stage stage) throws IOException {
        FXMLLoader loader =new FXMLLoader(getClass().getResource("/user-view2.fxml"));
        // loader.setLocation(getClass().getResource("com/example/map_toysocialnetwork_gui/views/user-view.fxml"));
        AnchorPane userLayout = loader.load();
        stage.setScene(new Scene(userLayout));
        stage.setTitle("SocialNetworkApplication");

        UserController userController = loader.getController();
        userController.setUserService(service);
    }

}