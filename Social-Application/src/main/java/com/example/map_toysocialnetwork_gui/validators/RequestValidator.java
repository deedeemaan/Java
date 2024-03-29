package com.example.map_toysocialnetwork_gui.validators;

import com.example.map_toysocialnetwork_gui.domain.FriendRequest;

import java.util.Objects;

public class RequestValidator implements Validator<FriendRequest>{
    @Override
    public void validate(FriendRequest entity) throws ValidationException {
        if (!Objects.equals(entity.getStatus(), "pending") && !Objects.equals(entity.getStatus(), "approved") && !Objects.equals(entity.getStatus(), "rejected")) {
            throw new ValidationException("Request must be either pending, approved or rejected!");
        }
    }

}