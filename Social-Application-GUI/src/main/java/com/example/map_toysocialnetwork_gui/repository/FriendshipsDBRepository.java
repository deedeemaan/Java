package com.example.map_toysocialnetwork_gui.repository;

import com.example.map_toysocialnetwork_gui.config.DatabaseConnectionConfig;
import com.example.map_toysocialnetwork_gui.domain.Prietenie;
import com.example.map_toysocialnetwork_gui.domain.Tuple;
import com.example.map_toysocialnetwork_gui.domain.Utilizator;
import com.example.map_toysocialnetwork_gui.repository.paging.Page;
import com.example.map_toysocialnetwork_gui.repository.paging.Pageable;
import com.example.map_toysocialnetwork_gui.repository.paging.PagingRepository;
import com.example.map_toysocialnetwork_gui.validators.LocalDateTimeValidator;
import com.example.map_toysocialnetwork_gui.validators.PrietenieValidator;
import com.example.map_toysocialnetwork_gui.validators.UtilizatorValidator;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

public class FriendshipsDBRepository implements PagingRepository<Tuple<Long,Long>, Prietenie> {
    private PrietenieValidator validator;
    private LocalDateTimeValidator validatorLocalDateTime;

    public FriendshipsDBRepository(PrietenieValidator validator,LocalDateTimeValidator validatorLocalDateTime) {
        this.validator = validator;
        this.validatorLocalDateTime = validatorLocalDateTime;
    }

    @Override
    public Optional<Prietenie> findOne(Tuple<Long, Long> longLongTuple) {
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("select * from friendships " +
                    "where id_user1 = ? and id_user2 = ?");

        )
        {
            statement.setInt(1, Math.toIntExact(longLongTuple.getLeft()));
            statement.setInt(2, Math.toIntExact(longLongTuple.getRight()));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long id_user1 = resultSet.getLong("id_user1");
                Long id_user2 = resultSet.getLong("id_user2");
                Timestamp timestamp = resultSet.getTimestamp("friendsFrom");
                Prietenie p = new Prietenie(id_user1, id_user2, timestamp.toLocalDateTime());
                return Optional.of(p);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Prietenie> findAll() {
        Set<Prietenie> friendships = new HashSet<Prietenie>();
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("select * from friendships ");
        )
        {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id_user1 = resultSet.getLong("id_user1");
                Long id_user2 = resultSet.getLong("id_user2");
                Timestamp timestamp = resultSet.getTimestamp("friendsFrom");
                Prietenie p = new Prietenie(id_user1, id_user2, timestamp.toLocalDateTime());
                friendships.add(p);
            }
            return friendships;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Prietenie> save(Prietenie entity) {
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("insert into friendships(id_user1, id_user2, friendsFrom) " +
                    "values (?,?,?)");
        )
        {
            statement.setInt(1,Math.toIntExact(entity.getId().getLeft()));
            statement.setInt(2,Math.toIntExact(entity.getId().getRight()));
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            validator.validate(entity);
            validatorLocalDateTime.validate(entity.getDate());
            int rowsAffected = statement.executeUpdate();
            if(rowsAffected < 0) {
                System.err.println("Failed to add friendship. Check DB.");
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
    public Optional<Prietenie> delete(Tuple<Long, Long> longLongTuple) {
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("DELETE FROM friendships " +
                    "WHERE (id_user1 = ? and id_user2 = ?) or (id_user1 = ? and id_user2 = ?)")
        )
        {
            statement.setInt(1,Math.toIntExact(longLongTuple.getLeft()));
            statement.setInt(2, Math.toIntExact(longLongTuple.getRight()));
            statement.setInt(3,Math.toIntExact(longLongTuple.getRight()));
            statement.setInt(4, Math.toIntExact(longLongTuple.getLeft()));
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected < 0) {
                System.err.println("Failed to delete user. Check DB.");
                return Optional.empty();
            }
            else {
                return findOne(longLongTuple);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Prietenie> update(Prietenie entity) {
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("UPDATE friendships " +
                    "SET firstname = ?, lastname = ? WHERE id = ?")
        )
        {
            statement.setInt(1,Math.toIntExact(entity.getId().getLeft()));
            statement.setInt(2,Math.toIntExact(entity.getId().getRight()));
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            validator.validate(entity);
            validatorLocalDateTime.validate(entity.getDate());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected < 0) {
                System.err.println("Failed to update friendship. Check DB.");
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
    public Page<Prietenie> findAllOnPage(Pageable pageable) {
        Set<Prietenie> friendships = new HashSet<Prietenie>();
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("select * from friendships limit ? offset ?");
            PreparedStatement statement1 = connection.prepareStatement("select count (*) from friendships");
            )
        {
            statement.setInt(1,pageable.getPageSize());
            statement.setInt(2,pageable.getPageSize()*pageable.getPageNr());

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id_user1 = resultSet.getLong("id_user1");
                Long id_user2 = resultSet.getLong("id_user2");
                Timestamp timestamp = resultSet.getTimestamp("friendsFrom");
                Prietenie p = new Prietenie(id_user1, id_user2, timestamp.toLocalDateTime());
                friendships.add(p);
            }
            Long contor=0L;
            ResultSet resultSet1 = statement1.executeQuery();
            if (resultSet1.next()){
                contor=resultSet1.getLong(1);
            }
            return new Page<>(friendships, Math.toIntExact(contor));
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
