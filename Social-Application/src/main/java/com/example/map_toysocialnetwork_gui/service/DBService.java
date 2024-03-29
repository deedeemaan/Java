package com.example.map_toysocialnetwork_gui.service;

import com.example.map_toysocialnetwork_gui.domain.*;
import com.example.map_toysocialnetwork_gui.repository.FriendReqDBRepository;
import com.example.map_toysocialnetwork_gui.repository.FriendshipsDBRepository;
import com.example.map_toysocialnetwork_gui.repository.MessageRepository;
import com.example.map_toysocialnetwork_gui.repository.UtilizatorDBRepository;
import com.example.map_toysocialnetwork_gui.utils.events.ChangeEvent;
import com.example.map_toysocialnetwork_gui.utils.events.ChangeEventType;
import com.example.map_toysocialnetwork_gui.utils.observer.Observable;
import com.example.map_toysocialnetwork_gui.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DBService implements Service<Long, Utilizator>, Observable<ChangeEvent> {
    UtilizatorDBRepository usersRepo;
    FriendshipsDBRepository friendshipsRepo;
    FriendReqDBRepository requestsRepo;
    MessageRepository messagesRepo;

    Long nextId;

    public DBService(UtilizatorDBRepository usersRepo, FriendshipsDBRepository friendshipsRepo,MessageRepository messagesRepo, FriendReqDBRepository requestsRepo) {
        this.usersRepo = usersRepo;
        this.friendshipsRepo = friendshipsRepo;
        this.messagesRepo = messagesRepo;
        this.requestsRepo = requestsRepo;

        Long max = -1L;
        for (Utilizator utilizator: usersRepo.findAll())
            if (utilizator.getId() > max)
                max = utilizator.getId();
        this.nextId = max + 1;
    }
    @Override
    public Optional<Utilizator> find(Long aLong) {
        return usersRepo.findOne(aLong);
    }

    @Override
    public Iterable<Utilizator> getAll() {
        return usersRepo.findAll();
    }

    @Override
    public void add(String firstName, String lastName,String username, String password) {
        Utilizator user  = new Utilizator(firstName, lastName,username, password);
        user.setId(nextId);
        if(usersRepo.save(user).isPresent()) {
            throw new IllegalArgumentException("User with given ID already exists");
        }
        notifyObsevers(new ChangeEvent(ChangeEventType.ADD,user));
    }

    @Override
    public void remove(Long aLong) {
        Utilizator deletedUser = usersRepo.findOne(aLong).get();
        Optional<Utilizator> user = usersRepo.delete(aLong);
        if(user.isPresent()){
            throw  new IllegalArgumentException("There is no user with given id.");
        }
        notifyObsevers(new ChangeEvent(ChangeEventType.DELETE,deletedUser));
    }

    public void update(Long userID, String newFirstName, String newLastName,String username, String password) {
        Optional<Utilizator> oldUser = usersRepo.findOne(userID);
        if(oldUser.isEmpty()) {
            throw new IllegalArgumentException("User with given ID doesn't exist.");
        }
        Utilizator updatedUser = new Utilizator(newFirstName, newLastName,username, password);
        updatedUser.setId(userID);
        if(usersRepo.update(updatedUser).isPresent()) {
            throw new IllegalArgumentException("Error updating user!");
        }
        notifyObsevers(new ChangeEvent(ChangeEventType.UPDATE,updatedUser,oldUser.get()));
    }

    public void removeFriendship(Long id_user1, Long id_user2) {
        if(friendshipsRepo.delete(new Tuple<>(id_user1, id_user2)).isPresent()) {
            throw new IllegalArgumentException("There is no friendship between these users or one of the users does not exist.");
        }
    }


    public void addFriendship(Long id_user1, Long id_user2, LocalDateTime date) {
        if(usersRepo.findOne(id_user1).isEmpty()) {
            throw new IllegalArgumentException("ID for User1 does not exist in DB.");
        }
        if(usersRepo.findOne(id_user2).isEmpty()) {
            throw new IllegalArgumentException("ID for User2 does not exist in DB.");
        }
        if(friendshipsRepo.save(new Prietenie(id_user1,id_user2,date)).isPresent()){
            throw new IllegalArgumentException("Users are already friends!");
        }
    }

    public Iterable<Prietenie> getFriendShipsByMonth(Long user_id, String month) {

        Iterable<Prietenie> allFriendshipsByMonth = friendshipsRepo.findAll();
        List<Prietenie> friendships = StreamSupport.stream(allFriendshipsByMonth.spliterator(), false)
                .filter(f -> Objects.equals(f.getId().getRight(), user_id) || Objects.equals(f.getId().getLeft(), user_id))
                .filter(f -> f.getDate().getMonth().toString().equals(month.toUpperCase()))
                .collect(Collectors.toList());

        return friendships;
    }

    private List<Observer<ChangeEvent>> observers=new ArrayList<>();

    public void addObserver(Observer<ChangeEvent> e) {
        observers.add(e);
    }


    public void removeObserver(Observer<ChangeEvent> e) {
        observers.remove(e);
    }


    public void notifyObsevers(ChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }

    public void saveRequest(String status, Long idFrom, Long idTo){
        FriendRequest request = new FriendRequest(idFrom, idTo, status);
        if(requestsRepo.findOne(new Tuple<>(idFrom,idTo)).isPresent()){
            updateRequest(status, idFrom, idTo);
        }
        else {
            if(requestsRepo.save(request).isEmpty()){
                throw new IllegalArgumentException("Service error: Failed while trying to save request.");
            } else {
                notifyObsevers(new ChangeEvent(ChangeEventType.ADD, request));
            }
        }
    }

    private void updateRequest(String status, Long idFrom, Long idTo) {
        Optional<FriendRequest> oldRequest = requestsRepo.findOne(new Tuple<>(idFrom,idFrom));
        if(oldRequest.isEmpty()){
            throw new IllegalArgumentException("Service error: couldn't find request!");
        }

        FriendRequest updatedRequest = new FriendRequest(idFrom, idTo, status);
        if (requestsRepo.update(updatedRequest).isPresent()) {
            throw new RuntimeException("Service error: failed when trying to update friendship!");
        }

        notifyObsevers(new ChangeEvent(ChangeEventType.UPDATE, updatedRequest, oldRequest));
    }

    public void deleteRequest(Long from, Long to) {
        FriendRequest deletedRequest = requestsRepo.findOne(new Tuple<>(from, to)).get();
        if (requestsRepo.delete(new Tuple<>(from, to)).isPresent()) {
            throw new IllegalArgumentException("Service error: Failed while trying to delete request.");
        } else {
            notifyObsevers(new ChangeEvent(ChangeEventType.DELETE, deletedRequest));
        }
    }
    public List<Utilizator> requestedOrApprovedUsers(Long userID) {
        // list of users userID requested and they approved / he approved
        ArrayList<Utilizator> users = new ArrayList<>();
        List<FriendRequest> userFriendRequests = StreamSupport.stream(requestsRepo.findAll().spliterator(), false)
                .filter(r -> r.getId().getLeft().equals(userID))
                .filter(r -> !(r.getStatus().equals("rejected")))
                .collect(Collectors.toList());
        userFriendRequests.forEach(r -> {
            users.add(usersRepo.findOne(r.getId().getRight()).get());
        });
        return users;
    }

    public void login(String username, String password) {
        if (!(username.equals("admin"))) {
            Optional<Utilizator> user = usersRepo.findByUsername(username);
            if (user.isEmpty()) {
                throw new IllegalArgumentException("Incorrect username!");
            } else {
                if (!(user.get().getPassword().equals(password))) {
                    throw new IllegalArgumentException("Incorrect password!");
                }
            }
        } else if (!(password.equals("admin"))){
            throw new IllegalArgumentException("Incorrect password!");
        }
    }

    public List<Utilizator> getPotentialFriends(Long userID) {

        List<Utilizator> users = new ArrayList<>();
        usersRepo.findAll().forEach(u->{
            if(!Objects.equals(u.getId(), userID) && potentialFriend(u.getId(), userID)) {
                users.add(u);
            }
        });
        return users;
    }

    public List<Utilizator> getUserFriends(Long userID)  {
        List<Utilizator> friends = new ArrayList<>();

        List<Prietenie> friendships = StreamSupport.stream(friendshipsRepo.findAll().spliterator(), false)
                .filter(f -> f.getId().getLeft().equals(userID) || f.getId().getRight().equals(userID))
                .collect(Collectors.toList());

        friendships.forEach(f -> {
            if (f.getId().getLeft().equals(userID)) {
                friends.add(usersRepo.findOne(f.getId().getRight()).get());
            } else {
                friends.add(usersRepo.findOne(f.getId().getLeft()).get());
            }
        });

        return friends;
    }

    public boolean potentialFriend(Long potentialFriendID, Long ourUserID) {
        Utilizator potentialFriend = usersRepo.findOne(potentialFriendID).get();
        if (getUserFriends(ourUserID).contains(potentialFriend))
            return false;
        if (requestedOrApprovedUsers(ourUserID).contains(potentialFriend))
            return false;
        if (getPendingFriendRequests(ourUserID).contains(potentialFriend))
            return false;
        return true;
    }

    public Optional<Utilizator> findByUsername(String username) {
        return usersRepo.findByUsername(username);
    }

    public List<Utilizator> getPendingFriendRequests(Long userID) {
        ArrayList<Utilizator> prieteni = new ArrayList<>();
        List<FriendRequest> pendingFriendRequests = StreamSupport.stream(requestsRepo.findAll().spliterator(), false)
                .filter(r -> Objects.equals(r.getId().getRight(), userID))
                .filter(r -> Objects.equals(r.getStatus(), "pending"))
                .collect(Collectors.toList());
        pendingFriendRequests.forEach(r->{
            prieteni.add(usersRepo.findOne(r.getId().getLeft()).get());
        });
        return prieteni;
    }
    public Integer numberOfPendingRequests(Long userID) {
        return getPendingFriendRequests(userID).size();
    }



    public Message sendMessage(Utilizator fromUser, List<Utilizator> toUsers, String messageText) {
        Message message = new Message(fromUser, toUsers, messageText, null);
        try {
            messagesRepo.save(message);
            return message;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Message> getMessagesBetweenUsers(Utilizator u1, Utilizator u2) {
        ArrayList<Message> messages = new ArrayList<>();
        messagesRepo.findAll().forEach(message -> {
            if (message.getFrom().equals(u1) && message.getTo().contains(u2) ||
                    message.getFrom().equals(u2) && message.getTo().contains(u1)) {
                messages.add(message);
            }
        });
        return messages;
    }
}
