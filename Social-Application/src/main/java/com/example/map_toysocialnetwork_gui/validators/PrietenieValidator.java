package com.example.map_toysocialnetwork_gui.validators;

import com.example.map_toysocialnetwork_gui.domain.Prietenie;

import java.util.Objects;

public class PrietenieValidator implements Validator<Prietenie> {

    @Override
    public void validate(Prietenie entity) throws ValidationException {
        if (entity == null) {
            throw new ValidationException("Entity must be not null!");
        }
        else if (Objects.equals(entity.getId().getLeft(), entity.getId().getRight()))
            throw new ValidationException("Invalid friendship. User cannot be friends with himself.");
    }
}
