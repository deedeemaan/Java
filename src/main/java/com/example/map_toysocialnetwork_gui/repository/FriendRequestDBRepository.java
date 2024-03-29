package com.example.map_toysocialnetwork_gui.repository;

import com.example.map_toysocialnetwork_gui.config.DatabaseConnectionConfig;
import com.example.map_toysocialnetwork_gui.domain.FriendRequest;
import com.example.map_toysocialnetwork_gui.domain.Prietenie;
import com.example.map_toysocialnetwork_gui.domain.Tuple;
import com.example.map_toysocialnetwork_gui.repository.paging.Page;
import com.example.map_toysocialnetwork_gui.repository.paging.Pageable;
import com.example.map_toysocialnetwork_gui.validators.FriendRequestValidator;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendRequestDBRepository implements Repository<Tuple<Long,Long>, FriendRequest> {

    private FriendRequestValidator validator;

    public FriendRequestDBRepository(FriendRequestValidator validator) {
        this.validator = validator;
    }

    @Override
    public Optional<FriendRequest> findOne(Tuple<Long, Long> longLongTuple) {
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("select * from friendrequest " +
                    "where idfrom = ? and idto = ?");

        )
        {
            statement.setInt(1, Math.toIntExact(longLongTuple.getLeft()));
            statement.setInt(2, Math.toIntExact(longLongTuple.getRight()));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long idFrom = resultSet.getLong("idFrom");
                Long idTo = resultSet.getLong("idTo");
                String status = resultSet.getString("status");
                FriendRequest friendRequest = new FriendRequest(status, idFrom, idTo);
                return Optional.of(friendRequest);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<FriendRequest> findAll() {
        Set<FriendRequest> friendships = new HashSet<FriendRequest>();
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("select * from friendrequest ");
        )
        {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
//                System.out.println("Yellow !!!!!!");
////                System.out.println(resultSet.next());

                Long idFrom = resultSet.getLong("idFrom");
                Long idTo = resultSet.getLong("idTo");
                String status = resultSet.getString("status");
                FriendRequest friendRequest = new FriendRequest(status, idFrom, idTo);
                friendships.add(friendRequest);
            }
            return friendships;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<FriendRequest> save(FriendRequest entity) {
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("insert into friendrequest(idfrom, idto, status) " +
                    "values (?,?,?)");
        )
        {
            statement.setInt(1,Math.toIntExact(entity.getId().getLeft()));
            statement.setInt(2,Math.toIntExact(entity.getId().getRight()));
            statement.setString(3, entity.getStatus());
            validator.validate(entity);
            int rowsAffected = statement.executeUpdate();
            if(rowsAffected < 0) {
                System.err.println("Failed to add friendrequest. Check DB.");
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
    public Optional<FriendRequest> delete(Tuple<Long, Long> longLongTuple) {
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("DELETE FROM friendrequest " +
                    "WHERE (idfrom = ? and idto = ?) or (idto = ? and idfrom = ?)")
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
    public Optional<FriendRequest> update(FriendRequest entity) {
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("UPDATE friendrequest " +
                    "SET status = ? WHERE (idfrom = ? and idto = ?) or (idto = ? and idfrom = ?)")
        )
        {
            statement.setString(1, entity.getStatus());
            statement.setInt(2,Math.toIntExact(entity.getId().getLeft()));
            statement.setInt(3,Math.toIntExact(entity.getId().getRight()));
            statement.setInt(4,Math.toIntExact(entity.getId().getLeft()));
            statement.setInt(5,Math.toIntExact(entity.getId().getRight()));
            validator.validate(entity);
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

    public Page<FriendRequest> findAllOnPage(Pageable pageable) {
        Set<FriendRequest> friendships = new HashSet<FriendRequest>();
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("select * from friendrequest limit ? offset ? ");
            PreparedStatement statement1 = connection.prepareStatement("select count (*) from friendrequest");
        )
        {
            statement.setInt(1,pageable.getPageSize());
            statement.setInt(2,pageable.getPageSize()*pageable.getPageNr());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
//                System.out.println("Yellow !!!!!!");
////                System.out.println(resultSet.next());

                Long idFrom = resultSet.getLong("idFrom");
                Long idTo = resultSet.getLong("idTo");
                String status = resultSet.getString("status");
                FriendRequest friendRequest = new FriendRequest(status, idFrom, idTo);
                friendships.add(friendRequest);
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
