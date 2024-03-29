package com.example.map_toysocialnetwork_gui.domain;

import java.util.Objects;

public class FriendRequest extends Entity<Tuple<Long, Long>> {
    private String status;

    public FriendRequest(String status, Long idFrom, Long idTo) {
        this.status = status;
        setId(new Tuple<Long, Long>(idFrom, idTo));
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
