package com.example.map_toysocialnetwork_gui.repository;

import com.example.map_toysocialnetwork_gui.config.DatabaseConnectionConfig;
import com.example.map_toysocialnetwork_gui.domain.Utilizator;
import com.example.map_toysocialnetwork_gui.validators.UtilizatorValidator;
import com.example.map_toysocialnetwork_gui.validators.ValidationException;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UtilizatorDBRepository implements Repository<Long, Utilizator>{

    private UtilizatorValidator validator;

    public UtilizatorDBRepository(UtilizatorValidator validator) {
        this.validator = validator;
    }
    @Override
    public Optional<Utilizator> findOne(Long aLong) {
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("select * from users "+
                    "where id = ?");

        )
        {
            statement.setInt(1, Math.toIntExact(aLong));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                Utilizator u = new Utilizator(firstName,lastName,username,password);
                u.setId(aLong);
                return Optional.ofNullable(u);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Utilizator> findAll() {
        Set<Utilizator> utilizatori = new HashSet<Utilizator>();

        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("select * from users ");
        ) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                Utilizator user = new Utilizator(firstName, lastName,username, password);
                user.setId(id);
                utilizatori.add(user);
            }
            return  utilizatori;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Utilizator> save(Utilizator entity) {
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO users(id, firstname, lastname,username, password) " +
                    "VALUES (?,?,?,?,?)")
        )
        {
            validator.validate(entity);
            statement.setInt(1,Math.toIntExact(entity.getId()));
            statement.setString(2,entity.getFirstName());
            statement.setString(3,entity.getLastName());
            statement.setString(4,entity.getUsername());
            statement.setString(5,entity.getPassword());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected < 0) {
                System.err.println("Failed to add user. Maybe ID already exists. Check DB.");
                return Optional.of(entity);
            }
            else {
                return Optional.empty();
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Utilizator> delete(Long aLong) {
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("DELETE FROM users " +
                    "WHERE id = ?")
        )
        {
            statement.setInt(1,Math.toIntExact(aLong));
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected < 0) {
                System.err.println("Failed to delete user. Check DB.");
                return Optional.empty();
            }
            else {
                return findOne(aLong);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Utilizator> update(Utilizator entity) {

        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("UPDATE users " +
                    "SET firstname = ?, lastname = ?,username = ?, password = ? WHERE id = ?")
        )
        {
            statement.setString(1,entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getUsername());
            statement.setString(4, entity.getPassword());
            statement.setInt(5, Math.toIntExact(entity.getId()));
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected < 0) {
                System.err.println("Failed to update user. Check DB.");
                return Optional.of(entity);
            }
            else {
                return Optional.empty();
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Optional<Utilizator> findByUsername(String username) {
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("select * from users "+
                    "where username = ?");

        )
        {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long userID = resultSet.getLong("id");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String password = resultSet.getString("password");
                Utilizator u = new Utilizator(firstName,lastName,username,password);
                u.setId(userID);
                return Optional.ofNullable(u);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }
}
