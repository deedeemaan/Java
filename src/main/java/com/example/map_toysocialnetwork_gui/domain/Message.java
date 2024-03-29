package com.example.map_toysocialnetwork_gui.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long>{
    Utilizator from;
    List<Utilizator> to;
    String message;
    LocalDateTime date;
    Message reply;

    public Message(Utilizator from, List<Utilizator> to, String message, Message reply) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = LocalDateTime.now();
        reply = null;
        this.reply = reply;
    }

    public Utilizator getFrom() {
        return from;
    }

    public List<Utilizator> getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setTo(List<Utilizator> to) {
        this.to = to;
    }

    public void setFrom(Utilizator from) {
        this.from = from;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(LocalDateTime date) {
        this.date = date;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }
}
