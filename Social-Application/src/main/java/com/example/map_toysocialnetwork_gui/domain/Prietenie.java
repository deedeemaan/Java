package com.example.map_toysocialnetwork_gui.domain;

import java.time.LocalDateTime;


public class Prietenie extends Entity<Tuple<Long,Long>> {

    LocalDateTime date;

    public Prietenie(Long user1,Long user2,LocalDateTime friendsFrom) {
        super.setId(new Tuple<>(user1, user2));
        this.date = friendsFrom;
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }
}
