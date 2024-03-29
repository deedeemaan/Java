package com.example.map_toysocialnetwork_gui.validators;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeValidator implements Validator{
    @Override
    public void validate(Object entity) throws ValidationException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        try {
            LocalDateTime dateTime = LocalDateTime.parse(entity.toString(), formatter);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid input format. Please use the format: yyyy-MM-dd'T'HH:mm:ss");
        }
    }
}