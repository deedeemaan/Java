package com.example.map_toysocialnetwork_gui.controller;

import com.example.map_toysocialnetwork_gui.domain.Message;
import com.example.map_toysocialnetwork_gui.domain.Utilizator;
import com.example.map_toysocialnetwork_gui.service.DBService;
import com.example.map_toysocialnetwork_gui.service.Service;
import com.example.map_toysocialnetwork_gui.utils.events.ChangeEvent;
import com.example.map_toysocialnetwork_gui.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements Observer<ChangeEvent> {
    Integer pendingRequests;
    DBService service;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelFriends = FXCollections.observableArrayList();
    String masterUsername;
    @FXML
    Button loginButton;
    @FXML
    Button logoutButton;
    @FXML
    Label loginInfo;
    @FXML
    Label loginFirstName;
    @FXML
    Label loginLastName;
    @FXML
    Label pendingRequestsNumberTF;
    @FXML
    TextField textFieldUsername;
    @FXML
    PasswordField passwordField;
    @FXML
    GridPane gridPane;
    @FXML
    TableView<Utilizator> tableView;
    @FXML
    TableColumn<Utilizator,Long> tableColumnID;
    @FXML
    TableColumn<Utilizator,String> tableColumnFirstName;
    @FXML
    TableColumn<Utilizator,String> tableColumnLastName;
    @FXML
    Button btnAdd;
    @FXML
    Button btnUpdate;
    @FXML
    Button btnDelete;
    @FXML
    Button btnSendRequest;
    @FXML
    Button btnAnswerRequest;
    @FXML
    Tab adminTab;
    @FXML
    Tab chatTab;
    @FXML
    Label signUpLabel;
    @FXML
    Label signUpLabel0;
    Integer loginStatus; // 0 - not logged in
    // 1 - user
    // 2 - admin

    @FXML
    ListView<Utilizator> listView;
    @FXML
    ImageView sendButtonImageView;
    @FXML
    ImageView chatRefreshImageView;
    @FXML
    Label toUsersLabel;
    @FXML
    MenuButton userSettingsButton;
    @FXML
    MenuItem removeFriendMenuItem;
    @FXML
    ImageView refreshFriendsListImageView;
    @FXML
    TextArea chatTextArea;
    @FXML
    TextField inputBox;
    @FXML
    Button startChatButton;

    public void setUserService(DBService service) {
        this.service = service;
        this.loginStatus = 0;
        this.pendingRequests = 0;
        service.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize() {
        tableColumnID.setCellValueFactory(new PropertyValueFactory<Utilizator, Long>("id"));
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        tableView.setItems(model);

        initFriendsModel();
        listView.setItems(modelFriends);
        listView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);

        loginInfo.setText("Not logged in.");
        pendingRequestsNumberTF.setText(" ");
        logoutButton.setDisable(true);
        logoutButton.setVisible(false);
        btnAdd.setDisable(true);
        btnAdd.setVisible(false);
        btnUpdate.setDisable(true);
        btnUpdate.setVisible(false);
        btnDelete.setDisable(true);
        btnDelete.setVisible(false);
        btnSendRequest.setDisable(true);
        btnSendRequest.setVisible(false);
        btnAnswerRequest.setDisable(true);
        btnAnswerRequest.setVisible(false);
        adminTab.setDisable(true);
        chatTab.setDisable(true);
        chatTextArea.setEditable(false);
        userSettingsButton.setVisible(false);
        sendButtonImageView.setDisable(true);

        inputBox.setOnAction(event -> handleEnterKeyPress());
        sendButtonImageView.setOnMouseClicked(event -> handleEnterKeyPress());

    }

    private void handleEnterKeyPress() {
        String message = inputBox.getText().trim();
        if (!message.isEmpty()) {
            Optional<Utilizator> fromUserOptional = service.findByUsername(masterUsername);
            if (fromUserOptional.isPresent()) {
                List<Utilizator> toUsers = new ArrayList<>();

                Pattern pattern = Pattern.compile("\\(([^)]+)\\)");

                Matcher matcher = pattern.matcher(toUsersLabel.getText());

                while (matcher.find()) {
                    String username = matcher.group(1);
                    toUsers.add(service.findByUsername(username).get());
                }

                Message message1 = service.sendMessage(fromUserOptional.get(), toUsers, message);
                if (message1 != null) {
                    chatTextArea.appendText("You(" + masterUsername + "): " + message1.getDate().toString() + "\n");
                    chatTextArea.appendText(message + "\n\n");

                    inputBox.clear();
                } else {
                    Alert.showErrorMessage(null, "Failed to send message.");
                }
            }
        }
    }

    private void initModel() {
        Iterable<Utilizator> users = service.getAll();
        List<Utilizator> userList = StreamSupport.stream(users.spliterator(), false).collect(Collectors.toList());
        model.setAll(userList);

        initFriendsModel();

        initNumberOfFriendRequests(masterUsername);

        signUpLabel.setOnMouseClicked(this::handleSignUp);

        // Set mouse enter and exit event handlers
        signUpLabel.setOnMouseEntered(event -> {
            signUpLabel.setCursor(Cursor.HAND);
        });

        signUpLabel.setOnMouseExited(event -> {
            signUpLabel.setCursor(Cursor.DEFAULT);
        });
    }

    private void initFriendsModel() {
        Iterable<Utilizator> friends = new ArrayList<>();
        if (masterUsername != null && !masterUsername.equals("admin")) {
            friends = service.getUserFriends(service.findByUsername(masterUsername).get().getId());
            List<Utilizator> friendsList = StreamSupport.stream(friends.spliterator(), false).collect(Collectors.toList());
            modelFriends.setAll(friendsList);
        } else {
            modelFriends.setAll((Collection<? extends Utilizator>) friends);
        }
    }

    @FXML
    public void handleStartChat(ActionEvent event) {
        ObservableList<Utilizator> selectedUsers = listView.getSelectionModel().getSelectedItems();
        chatTextArea.clear();
        inputBox.clear();
        if (!(selectedUsers.isEmpty())) {
            sendButtonImageView.setDisable(true);
            if (selectedUsers.size() > 1) {
                StringBuilder usersInfo = new StringBuilder();
                for (int i = 0; i < selectedUsers.size(); i++) {
                    Utilizator user = selectedUsers.get(i);
                    usersInfo.append(user.toString()).append(" (").append(user.getUsername()).append(")");
                    if (i < selectedUsers.size() - 1) {
                        usersInfo.append(", ");
                    }
                }
                toUsersLabel.setText(usersInfo.toString());
                userSettingsButton.setVisible(false);
            } else {
                toUsersLabel.setText(selectedUsers.get(0).toString() + " (" + selectedUsers.get(0).getUsername() + ") ");
                userSettingsButton.setVisible(true);
                loadChat(selectedUsers.get(0));
            }
        } else {
            Alert.showErrorMessage(null, "No users selected!");
        }
    }

    public void loadChat(Utilizator otherUser) {
        chatTextArea.clear();
        List<Message> messages = service.getMessagesBetweenUsers(otherUser, service.findByUsername(masterUsername).get());
        messages.forEach(message -> {
            if (message.getFrom().getUsername().equals(masterUsername)) {
                chatTextArea.appendText("You(" + masterUsername + "): " +message.getDate().toString() + "\n");
                chatTextArea.appendText(message.getMessage() + "\n\n");
            } else {
                chatTextArea.appendText(message.getFrom().toString()+ " (" + message.getFrom().getUsername() + "): " +message.getDate().toString() + "\n");
                chatTextArea.appendText(message.getMessage() + "\n\n");
            }
        });
    }

    @FXML
    private void handleRefreshFriendsList(MouseEvent event) {
        initFriendsModel();
    }

    @FXML
    private void handleRefreshChat(MouseEvent event) {
        ObservableList<Utilizator> selectedUsers = listView.getSelectionModel().getSelectedItems();
        if (!(selectedUsers.isEmpty())) {
            loadChat(selectedUsers.get(0));
        } else {
            Alert.showErrorMessage(null, "No users selected!");
        }
    }

    public static String parseUsername(String input) {
        // Define the pattern for the username
        Pattern pattern = Pattern.compile("\\(([^)]+)\\)");

        // Create a matcher for the input string
        Matcher matcher = pattern.matcher(input);

        // Find the username using the regular expression
        if (matcher.find()) {
            return matcher.group(1); // Group 1 contains the captured username
        } else {
            return null; // Return null if no match is found
        }
    }


    @FXML
    private void handleRemoveFriend(ActionEvent event) {
        String username = parseUsername(toUsersLabel.toString());
        Optional<Utilizator> userOptional = service.findByUsername(username);
        Optional<Utilizator> masterUserOptional = service.findByUsername(masterUsername);
        if (userOptional.isPresent() && masterUserOptional.isPresent()) {
            service.removeFriendship(userOptional.get().getId(), masterUserOptional.get().getId());
            toUsersLabel.setText(" ");
            userSettingsButton.setVisible(false);
            chatTextArea.clear();
            inputBox.clear();
            initFriendsModel();
            sendButtonImageView.setDisable(true);
        }
    }
    private void handleSignUp(MouseEvent event) {
        showSignUpDialog();
    }

    private void initNumberOfFriendRequests(String masterUsername) {
        if (masterUsername != null && !(masterUsername.equals("admin")))
        {
            Integer nr = service.numberOfPendingRequests(service.findByUsername(masterUsername).get().getId());
            if (nr > 0)
                pendingRequestsNumberTF.setText("Pending friend requests!");
            else
                pendingRequestsNumberTF.setText(" ");
        }
    }

    public void handleLogin(ActionEvent actionEvent) {
        if (loginStatus == 0) {


            String username = textFieldUsername.getText();
            String password = passwordField.getText();

            try {
                service.login(username, password);
                loginInfo.setText("Logged in as:");
                Alert.showMessage(null, javafx.scene.control.Alert.AlertType.INFORMATION, null, "Succesfully logged in!");
                loginButton.setDisable(true);
                loginButton.setVisible(false);
                logoutButton.setDisable(false);
                logoutButton.setVisible(true);
                gridPane.setDisable(true);
                gridPane.setVisible(false);
                signUpLabel.setDisable(true);
                signUpLabel.setVisible(false);
                signUpLabel0.setDisable(true);
                signUpLabel0.setVisible(false);

                if (username.equals("admin")){
                    loginStatus = 2;
                    loginFirstName.setText("admin");
                    btnAdd.setDisable(false);
                    btnAdd.setVisible(true);
                    btnUpdate.setDisable(false);
                    btnUpdate.setVisible(true);
                    btnDelete.setDisable(false);
                    btnDelete.setVisible(true);
                    adminTab.setDisable(false);
                    masterUsername = username;
                } else {
                    loginStatus = 1;
                    loginFirstName.setText(service.findByUsername(username).get().getFirstName());
                    loginLastName.setText(service.findByUsername(username).get().getLastName());
                    btnSendRequest.setDisable(false);
                    btnSendRequest.setVisible(true);
                    btnAnswerRequest.setDisable(false);
                    btnAnswerRequest.setVisible(true);
                    initNumberOfFriendRequests(username);
                    adminTab.setDisable(true);
                    chatTab.setDisable(false);
                    masterUsername = username;
                    initFriendsModel();
                }
            } catch (Exception e) {
                Alert.showErrorMessage(null, e.getMessage());
            }

        }
        else
            Alert.showErrorMessage(null, "You are already logged in.");
    }

    public void handleSendRequest(ActionEvent actionEvent) {
        showSendRequestDialog(masterUsername);
    }

    public void handleAnswerRequest(ActionEvent actionEvent) {
        showPendingRequestsDialog(masterUsername);

    }

    public void handleLogout(ActionEvent actionEvent) {
        if(loginStatus != 0) {
            loginStatus = 0;
            logoutButton.setDisable(true);
            logoutButton.setVisible(false);
            loginInfo.setText("Not logged in.");
            loginFirstName.setText(" ");
            loginLastName.setText(" ");
            Alert.showMessage(null, javafx.scene.control.Alert.AlertType.INFORMATION, null, "Succesfully logged out!");
            signUpLabel.setDisable(false);
            signUpLabel.setVisible(true);
            signUpLabel0.setDisable(false);
            signUpLabel0.setVisible(true);
            loginButton.setDisable(false);
            loginButton.setVisible(true);
            gridPane.setDisable(false);
            gridPane.setVisible(true);
            textFieldUsername.clear();
            passwordField.clear();

            btnAdd.setDisable(true);
            btnAdd.setVisible(false);
            btnUpdate.setDisable(true);
            btnUpdate.setVisible(false);
            btnDelete.setDisable(true);
            btnDelete.setVisible(false);
            btnSendRequest.setDisable(true);
            btnSendRequest.setVisible(false);
            btnAnswerRequest.setDisable(true);
            btnAnswerRequest.setVisible(false);

            adminTab.setDisable(true);
            chatTab.setDisable(true);

            pendingRequestsNumberTF.setText(" ");

            chatTextArea.clear();
            inputBox.clear();
            userSettingsButton.setVisible(false);
            sendButtonImageView.setDisable(true);
        }
        else
            Alert.showErrorMessage(null, "You are not logged in.");
    }

    public void handleDeleteUser(ActionEvent actionEvent) {
        if (loginStatus == 2) {
            ObservableList<Utilizator> selected = tableView.getSelectionModel().getSelectedItems();
            if (!(selected.isEmpty())) {
                service.remove(selected.get(0).getId());
                Alert.showMessage(null, javafx.scene.control.Alert.AlertType.INFORMATION, "Delete", "User was successfully deleted!");

            } else {
                Alert.showErrorMessage(null, "No user selected!");
            }
        }
        else
            Alert.showErrorMessage(null, "You don't have the permission to do that!");
    }

    public void handleUpdateUser(ActionEvent actionEvent) {
        if (loginStatus == 2) {
            ObservableList<Utilizator> selected = tableView.getSelectionModel().getSelectedItems();
            if (!(selected.isEmpty())) {
                showUserEditDialog(selected.get(0));
            } else {
                Alert.showErrorMessage(null, "No user selected!");
            }
        } else
            Alert.showErrorMessage(null, "You don't have the permission to do that!");
    }

    public void handleAddUser(ActionEvent actionEvent) {
        if (loginStatus == 2) {
            showUserEditDialog(null);
        }
        else
            Alert.showErrorMessage(null, "You don't have the permission to do that!");
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

    private void showSendRequestDialog(String username) {
        try {


            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/friendrequest-view.fxml"));
            AnchorPane root =(AnchorPane)loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Send Friend Request");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            FriendRequestController friendRequestController = loader.getController();
            friendRequestController.setService(service, dialogStage, username);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showPendingRequestsDialog(String masterUsername) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/pendingfriendrequests-view.fxml"));
            AnchorPane root = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Pending Friend Requests");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            PendingFriendRequestsController pendingFriendRequestsController = loader.getController();
            pendingFriendRequestsController.setService(service, dialogStage, masterUsername);

            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showSignUpDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/signupdialog-view.fxml"));
            AnchorPane root = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Sign up");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            SignUpDialogController signUpDialogController = loader.getController();
            signUpDialogController.setService(service, dialogStage);

            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ChangeEvent changeEvent) {
        initModel();
        initFriendsModel();
    }
}
