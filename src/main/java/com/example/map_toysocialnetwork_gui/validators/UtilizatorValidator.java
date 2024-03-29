package com.example.map_toysocialnetwork_gui.validators;

import com.example.map_toysocialnetwork_gui.domain.Utilizator;
import com.example.map_toysocialnetwork_gui.validators.ValidationException;
import com.example.map_toysocialnetwork_gui.validators.Validator;

public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        if (entity == null)
            throw new ValidationException("entity must be not null");

        if (entity.getFirstName().isEmpty() || entity.getLastName().isEmpty())
            throw new ValidationException("Nume utilizator invalid!");
    }
}

