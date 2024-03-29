package com.example.map_toysocialnetwork_gui.service;

import com.example.map_toysocialnetwork_gui.domain.Entity;
import com.example.map_toysocialnetwork_gui.validators.ValidationException;

import java.util.Optional;

public interface Service<ID, E extends Entity<ID>>{
    /**
     *
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     *          or null - if there is no entity with the given id
     * @throws IllegalArgumentException
     *                  if id is null.
     */
    Optional<E> find(ID id);

    /**
     *
     * @return all entities
     */
    Iterable<E> getAll();

    /**
     *
     * @param firstName
     *         firstName must be not empty
     * @param lastName
     *         lastName must be not empty
     * @throws ValidationException
     *            if the entity is not valid
     * @throws IllegalArgumentException
     *             if the given entity is null.     *
     */
    void add(String firstName, String lastName, String password);

    /**
     * removes the entity with the specified id
     *
     * @param id id must be not null
     * @throws IllegalArgumentException if the given id is null.
     */
    void remove(ID id);
}
