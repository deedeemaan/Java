package com.example.map_toysocialnetwork_gui.utils.observer;

import com.example.map_toysocialnetwork_gui.utils.events.Event;

public interface Observable<E extends Event>{

    void addObserver(Observer<E> e);

    void removeObserver(Observer<E> e);

    void notifyObsevers(E t);

}
