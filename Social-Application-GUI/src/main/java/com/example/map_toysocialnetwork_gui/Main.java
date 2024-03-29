package com.example.map_toysocialnetwork_gui;

import com.example.map_toysocialnetwork_gui.UI.UI;
import com.example.map_toysocialnetwork_gui.validators.LocalDateTimeValidator;
import com.example.map_toysocialnetwork_gui.validators.PrietenieValidator;
import com.example.map_toysocialnetwork_gui.validators.UtilizatorValidator;
import com.example.map_toysocialnetwork_gui.repository.FriendshipsDBRepository;
import com.example.map_toysocialnetwork_gui.repository.UtilizatorDBRepository;
import com.example.map_toysocialnetwork_gui.service.DBService;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

//    public static void main(String[] args) throws SQLException {
//        UtilizatorDBRepository usersRepo = new UtilizatorDBRepository(new UtilizatorValidator());
//        FriendshipsDBRepository friendshipsRepo = new FriendshipsDBRepository(new PrietenieValidator(), new LocalDateTimeValidator());
//        DBService DBserv = new DBService(usersRepo, friendshipsRepo);
//        UI ui = new UI(DBserv);
//    }
}
