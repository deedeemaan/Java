package com.example.map_toysocialnetwork_gui;

import com.example.map_toysocialnetwork_gui.controller.FriendRequestController;
import com.example.map_toysocialnetwork_gui.controller.UserController;
import com.example.map_toysocialnetwork_gui.repository.FriendRequestDBRepository;
import com.example.map_toysocialnetwork_gui.repository.FriendshipsDBRepository;
import com.example.map_toysocialnetwork_gui.repository.MessageDBRepository;
import com.example.map_toysocialnetwork_gui.repository.UtilizatorDBRepository;
import com.example.map_toysocialnetwork_gui.service.DBService;
import com.example.map_toysocialnetwork_gui.validators.FriendRequestValidator;
import com.example.map_toysocialnetwork_gui.validators.LocalDateTimeValidator;
import com.example.map_toysocialnetwork_gui.validators.PrietenieValidator;
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
    FriendRequestDBRepository friendRequestDBRepository;
    MessageDBRepository messagesRepo;
    DBService service;


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        userRepo = new UtilizatorDBRepository(new UtilizatorValidator());
        friendshipsDBRepository = new FriendshipsDBRepository(new PrietenieValidator(), new LocalDateTimeValidator());
        friendRequestDBRepository = new FriendRequestDBRepository(new FriendRequestValidator());
        messagesRepo = new MessageDBRepository(userRepo);
        service = new DBService(userRepo, friendshipsDBRepository, friendRequestDBRepository, messagesRepo);

        initView(stage);
        stage.setWidth(800);
        stage.show();
    }

    private void initView(Stage stage) throws IOException {
        FXMLLoader loader =new FXMLLoader(getClass().getResource("/user-view.fxml"));
//         loader.setLocation(getClass().getResource("com/example/map_toysocialnetwork_gui/views/user-view.fxml"));
        AnchorPane userLayout = loader.load();
        stage.setScene(new Scene(userLayout));
        stage.setTitle("SocialNetworkApplication");

        UserController userController = loader.getController();
        userController.setUserService(service);
    }

}