package com.example.map_toysocialnetwork_gui;

import com.example.map_toysocialnetwork_gui.UI.UI;
import com.example.map_toysocialnetwork_gui.domain.FriendRequest;
import com.example.map_toysocialnetwork_gui.domain.Message;
import com.example.map_toysocialnetwork_gui.domain.Utilizator;
import com.example.map_toysocialnetwork_gui.repository.FriendReqDBRepository;
import com.example.map_toysocialnetwork_gui.repository.MessageRepository;
import com.example.map_toysocialnetwork_gui.validators.LocalDateTimeValidator;
import com.example.map_toysocialnetwork_gui.validators.PrietenieValidator;
import com.example.map_toysocialnetwork_gui.validators.RequestValidator;
import com.example.map_toysocialnetwork_gui.validators.UtilizatorValidator;
import com.example.map_toysocialnetwork_gui.repository.FriendshipsDBRepository;
import com.example.map_toysocialnetwork_gui.repository.UtilizatorDBRepository;
import com.example.map_toysocialnetwork_gui.service.DBService;

import java.sql.SQLException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws SQLException {
        /*
        UtilizatorDBRepository usersRepo = new UtilizatorDBRepository(new UtilizatorValidator());
        FriendshipsDBRepository friendshipsRepo = new FriendshipsDBRepository(new PrietenieValidator(), new LocalDateTimeValidator());
        DBService DBserv = new DBService(usersRepo, friendshipsRepo);
        UI ui = new UI(DBserv);

         */
        MessageRepository msgRepo = new MessageRepository(new UtilizatorDBRepository(new UtilizatorValidator()));
        Utilizator u1 = new Utilizator("hjkfkh", "jkdfkgnfd", "dfgjkdfg", "jgkkdg");
        u1.setId(11l);
        Utilizator u2 = new Utilizator("hjkfkh", "jkdfkgnfd", "dfgjkdfg", "jgkkdg");
        u2.setId(13l);
        Utilizator u3 = new Utilizator("hjkfkh", "jkdfkgnfd", "dfgjkdfg", "jgkkdg");
        u3.setId(14l);
        ArrayList<Utilizator> l = new ArrayList<>();
        l.add(u2);
        l.add(u3);
        Message message = new Message(u1, l, "test", null);
        msgRepo.save(message);
        // Optional<Message> newMessageOptional = msgRepo.findOne(2l);
        Iterable<Message> messages = msgRepo.findAll();
    }
}
