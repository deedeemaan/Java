package com.example.map_toysocialnetwork_gui.repository;

import com.example.map_toysocialnetwork_gui.config.DatabaseConnectionConfig;
import com.example.map_toysocialnetwork_gui.domain.Message;
import com.example.map_toysocialnetwork_gui.domain.Utilizator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MessageRepository {
    UtilizatorDBRepository userRepo;

    public MessageRepository(UtilizatorDBRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void save(Message message) {
        String selectQuery = "SELECT * FROM messages ORDER BY date DESC LIMIT 1";


        try (Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO messages(\"from_user_id\", text, date, reply_id) VALUES " +
                     "(?, ?, ?, ?); ");
        )
        {
            statement.setLong(1, Math.toIntExact(message.getFrom().getId()));
            statement.setString(2, message.getMessage());
            statement.setTimestamp(3, Timestamp.valueOf(message.getDate()));
            if (message.getReply() == null) {
                statement.setObject(4, message.getReply());
            } else {
                statement.setInt(4, Math.toIntExact(message.getReply().getId()));
            }

            int affectedRows = statement.executeUpdate();
            if (affectedRows != 0) {

                try(PreparedStatement statement1 = connection.prepareStatement("SELECT * FROM messages ORDER BY date DESC LIMIT 1");
                    ResultSet resultSet = statement1.executeQuery()) {
                    if (resultSet.next()) {
                        Long messageId = resultSet.getLong("id");

                        try (PreparedStatement toStatement = connection.prepareStatement("INSERT INTO messages_to (message_id, to_user_id) VALUES (?, ?)")) {
                            message.getTo().forEach(u -> {
                                try {
                                    toStatement.setLong(1, Math.toIntExact(messageId));
                                    toStatement.setLong(2, Math.toIntExact(u.getId()));
                                    toStatement.addBatch();
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            });

                            toStatement.executeBatch();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                    } else {
                        System.err.println("No entries in the table.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }


            } else {
                throw new SQLException("Creating message failed, no ID obtained.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Message> findOne(Long id) {
        try (Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * " +
                     "FROM messages " +
                     "WHERE messages.id = ? ");
        )
        {
            statement.setLong(1, Math.toIntExact(id));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long fromUserId = resultSet.getLong("from_user_id");
                String text = resultSet.getString("text");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Long replyId = resultSet.getLong("reply_id");
                Message replyMessage = findOne(replyId).orElse(null);
                Utilizator fromUser = null;
                Optional<Utilizator> fromUserOptional = userRepo.findOne(fromUserId);
                if(fromUserOptional.isPresent()) {
                    fromUser = fromUserOptional.get();
                }
                ArrayList<Utilizator> toUsers = new ArrayList<>();

                try(PreparedStatement statement1 = connection.prepareStatement("SELECT messages_to.to_user_id " +
                        "FROM messages_to JOIN messages " +
                        "ON messages.id = messages_to.message_id WHERE messages.id = ? "))
                {
                    statement1.setLong(1, Math.toIntExact(id));
                    ResultSet resultSet1 = statement1.executeQuery();
                    while(resultSet1.next()) {
                        Long toUserId = resultSet1.getLong("to_user_id");
                        Optional<Utilizator> toUserOptional = userRepo.findOne(toUserId);
                        toUserOptional.ifPresent(toUsers::add);
                    }
                    if (fromUser != null) {
                        Message message = new Message(fromUser, toUsers, text, replyMessage);
                        message.setId(id);
                        return Optional.of(message);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Iterable<Message> findAll() {
        Set<Message> messageSet = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * " +
                     "FROM messages ");
        )
        {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long messageId = resultSet.getLong("id");
                Long fromUserId = resultSet.getLong("from_user_id");
                String text = resultSet.getString("text");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Long replyId = resultSet.getLong("reply_id");
                Message replyMessage = findOne(replyId).orElse(null);
                Utilizator fromUser = null;
                Optional<Utilizator> fromUserOptional = userRepo.findOne(fromUserId);
                if(fromUserOptional.isPresent()) {
                    fromUser = fromUserOptional.get();
                }
                ArrayList<Utilizator> toUsers = new ArrayList<>();

                try(PreparedStatement statement1 = connection.prepareStatement("SELECT messages_to.to_user_id " +
                        "FROM messages_to JOIN messages " +
                        "ON messages.id = messages_to.message_id WHERE messages.id = ? "))
                {
                    statement1.setLong(1, Math.toIntExact(messageId));
                    ResultSet resultSet1 = statement1.executeQuery();
                    while(resultSet1.next()) {
                        Long toUserId = resultSet1.getLong("to_user_id");
                        Optional<Utilizator> toUserOptional = userRepo.findOne(toUserId);
                        toUserOptional.ifPresent(toUsers::add);
                    }
                    if (fromUser != null) {
                        Message message = new Message(fromUser, toUsers, text, replyMessage);
                        message.setId(messageId);
                        message.setData(date);
                        messageSet.add(message);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return messageSet;
    }

    public Optional<Message> delete(Long messageID) {
        try (Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM messages " +
                     "WHERE messages.id = ? ");
        )
        {
            statement.setLong(1, Math.toIntExact(messageID));
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.err.println("Failed to delete message. Check DB.");
                return Optional.empty();
            } else {
                return findOne(messageID);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Message> update(Message message) {

        try (Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_CONNECTION_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("UPDATE messages " +
                     "SET text = ? WHERE messages.id = ? ");
        ) {
            statement.setLong(2, Math.toIntExact(message.getId()));
            statement.setString(1, message.getMessage());
            int rowsAffeced = statement.executeUpdate();
            if (rowsAffeced > 0) {
                return Optional.empty();
            } else {
                System.err.println("Failed to update message. Check DB.");
                return Optional.of(message);
            }
        } catch (SQLException e ){
            throw new RuntimeException(e);
        }
    }
}
