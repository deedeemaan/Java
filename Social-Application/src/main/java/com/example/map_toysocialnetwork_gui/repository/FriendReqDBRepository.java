package com.example.map_toysocialnetwork_gui.repository;
import com.example.map_toysocialnetwork_gui.config.DatabaseConnectionConfig;
import com.example.map_toysocialnetwork_gui.domain.FriendRequest;
import com.example.map_toysocialnetwork_gui.domain.Tuple;
import com.example.map_toysocialnetwork_gui.domain.Utilizator;
import com.example.map_toysocialnetwork_gui.validators.RequestValidator;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FriendReqDBRepository implements Repository<Tuple<Long,Long>, FriendRequest>{

    private RequestValidator validator;

    public FriendReqDBRepository(RequestValidator validator) {
        this.validator = validator;
    }
    @Override
    public Optional<FriendRequest> findOne(Tuple<Long, Long> longLongTuple) {
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("select * from friendships " +
                    "where \"idfrom\" = ? and \"idto\" = ?");
        )
        {
            statement.setLong(1, Math.toIntExact(longLongTuple.getLeft()));
            statement.setLong(2, Math.toIntExact(longLongTuple.getRight()));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long id_from = resultSet.getLong("idFrom");
                Long id_To = resultSet.getLong("idTo");
                String stare = resultSet.getString("status");
                FriendRequest friendRequest = new FriendRequest(id_from, id_To, stare);
                return Optional.of(friendRequest);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();    }

    @Override
    public Iterable<FriendRequest> findAll() {
        Set<FriendRequest> friendRequests = new HashSet<FriendRequest>();
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("select * from FriendRequest");
        )
        {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id_from = resultSet.getLong("idFrom");
                Long id_To = resultSet.getLong("idTo");
                String stare = resultSet.getString("status");
                FriendRequest friendRequest = new FriendRequest(id_from, id_To, stare);
                friendRequests.add(friendRequest);
            }
            return friendRequests;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendRequest> save(FriendRequest entity) {
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
            PreparedStatement statement = connection.prepareStatement("insert into FriendRequest(\"idfrom\", \"idto\", \"status\") " +
                    "values (?,?,?)");
        )
        {
            statement.setLong(1,Math.toIntExact(entity.getId().getLeft()));
            statement.setInt(2,Math.toIntExact(entity.getId().getRight()));
            statement.setString(3, entity.getStatus());
            int rowsAffected = statement.executeUpdate();
            if(rowsAffected < 0) {
                System.err.println("Failed to add FriendshipRequest. Check DB.");
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
            PreparedStatement statement = connection.prepareStatement("DELETE FROM FriendRequest " +
                    "WHERE (\"idfrom\" = ? and \"idto\" = ?)")
        )
        {
            statement.setLong(1,longLongTuple.getLeft());
            statement.setLong(2, longLongTuple.getRight());
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
            PreparedStatement statement = connection.prepareStatement("UPDATE FriendRequest " +
                    "SET \"idfrom\" = ?, \"idto\" = ?, status = ? WHERE pk_friendrequest = ?")
        )
        {
            statement.setLong(1,Math.toIntExact(entity.getId().getLeft()));
            statement.setLong(2,Math.toIntExact(entity.getId().getRight()));
            statement.setString(3,entity.getStatus());
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
        }    }

    ///gaseste pt un id si o stare toate inregistrarile( getAll(idTo(adica eu), stare))
    /// ca la getAll + where idTo=? si stare=?



//    public Iterable<FriendRequest> findMyFriendReq(Long idTo){
//        Set<FriendRequest> friendRequests = new HashSet<FriendRequest>();
//        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
//                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
//            PreparedStatement statement = connection.prepareStatement("select * from friendships where idTo = ?");
//        )
//        {
//            statement.setLong(1,idTo);
//            ResultSet resultSet = statement.executeQuery();
//            while (resultSet.next()) {
//                Long id_from = resultSet.getLong("idFrom");
//                Long id_To = resultSet.getLong("idTo");
//                String stare = resultSet.getString("status");
//                FriendRequest friendRequest = new FriendRequest(id_from, id_To, stare);
//                friendRequests.add(friendRequest);
//            }
//            return friendRequests;
//        }
//        catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
