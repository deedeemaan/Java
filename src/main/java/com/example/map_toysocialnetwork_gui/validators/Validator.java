package com.example.map_toysocialnetwork_gui.validators;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}