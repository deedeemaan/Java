package com.example.map_toysocialnetwork_gui.utils.observer;

import com.example.map_toysocialnetwork_gui.utils.events.Event;

public interface Observer <E extends Event>{

    void update(E e);
}
