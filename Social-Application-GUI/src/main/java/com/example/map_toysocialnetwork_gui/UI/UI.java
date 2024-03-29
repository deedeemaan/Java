package com.example.map_toysocialnetwork_gui.UI;

import com.example.map_toysocialnetwork_gui.domain.Prietenie;
import com.example.map_toysocialnetwork_gui.domain.Utilizator;
import com.example.map_toysocialnetwork_gui.service.DBService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

public class UI {
    DBService serv;
    Scanner scanner;

    public UI(DBService serv){
        this.serv = serv;
        scanner = new Scanner(System.in);
        run();
    }

    public void run(){
        printMenu();
        while(true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            switch(input){
                case "/":
                    printMenu();
                    break;
                case "1":
                    addUserUI();
                    break;
                case "2":
                    updateUserUI();
                case "3":
                    removeUserUI();
                    break;
                case "4":
                    addFriendshipUI();
                    break;
                case "5":
                    removeFriendshipUI();
                    break;
                case "6":
                    getFriendShipsMonthUI();
                    break;
                case "x":
                    System.out.println("Bye.");
                    return;
                default:
                    System.out.println("Invalid command. Check menu. (Hint: use '/')");
                    break;
            }
        }
    }

    public void addUserUI() {
        System.out.println("Enter first name: ");
        String firstName = scanner.nextLine();
        if(firstName.equals("/")) {
            printMenu();
            return;
        }
        System.out.println("Enter last name: ");
        String lastName = scanner.nextLine();
        if(lastName.equals("/")) {
            printMenu();
            return;
        }
        System.out.println("Enter password: ");
        String password = scanner.nextLine();
        if(password.equals("/")) {
            printMenu();
            return;
        }
        serv.add(firstName, lastName, password);
        System.out.println("Success!");
    }

    public void updateUserUI() {

        System.out.println("Enter user ID: ");
        String userID = scanner.nextLine();
        if (userID.equals("/")) {
            printMenu();
            return;
        }
        System.out.println("Enter first name: ");
        String firstName = scanner.nextLine();
        if (firstName.equals("/")) {
            printMenu();
            return;
        }
        System.out.println("Enter last name: ");
        String lastName = scanner.nextLine();
        if (lastName.equals("/")) {
            printMenu();
            return;
        }
        System.out.println("Enter a password: ");
        String password = scanner.nextLine();
        if (password.equals("/")) {
            printMenu();
            return;
        }
        serv.update(Long.valueOf(userID), firstName, lastName, password);
        System.out.println("Success!");
    }

    public void removeUserUI() {
        // TODO
        System.out.println("Enter user's ID: ");
        String userID = scanner.nextLine();
        if(userID.equals("/")) {
            printMenu();
            return;
        }
        serv.remove(Long.valueOf(userID));
        System.out.println("Success!");
    }

    public void addFriendshipUI() {
        System.out.println("Enter first user's ID: ");
        String idUser1= scanner.nextLine();
        if(idUser1.equals("/")) {
            printMenu();
            return;
        }
        System.out.println("Enter first user's ID: ");
        String idUser2= scanner.nextLine();
        if(idUser2.equals("/")) {
            printMenu();
            return;
        }
        System.out.println("Enter date and time (yyyy-MM-dd HH:mm:ss): ");
        String dateString = scanner.nextLine();
        if(dateString.equals("/")) {
            printMenu();
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
        serv.addFriendship(Long.parseLong(idUser1), Long.parseLong(idUser2), localDateTime);
        System.out.println("Success!");
    }

    void removeFriendshipUI() {
        System.out.println("Enter first user's ID:");
        String userID = scanner.nextLine();
        while(userID.isEmpty()){
            System.out.println("ID cannot be empty. Please input ID.");
            userID = scanner.nextLine();
            if (!(userID.isEmpty()))
                break;
        }
        if(userID.equals("/")) {
            printMenu();
            return;
        }
        if(serv.find(Long.valueOf(userID)).isEmpty()) {
            System.err.println("No user with specified id.");
            return;
        }
        System.out.println("Enter second user's ID: ");
        String friendID = scanner.nextLine();
        while(friendID.isEmpty()){
            System.out.println("ID cannot be empty. Please input ID.");
            friendID = scanner.nextLine();
            if (!(friendID.isEmpty()))
                break;
        }
        if (friendID.equals("/")) {
            printMenu();
            return;
        }
        serv.removeFriendship(Long.parseLong(userID), Long.parseLong(friendID));
        System.out.println("Success!");
    }

    void getFriendShipsMonthUI(){
        System.out.println("Enter user's ID:");
        String idUser= scanner.nextLine();
        if(idUser.equals("/")) {
            printMenu();
            return;
        }
        System.out.println("Enter month");
        String monthStr = scanner.nextLine();
        if(monthStr.equals("/")) {
            printMenu();
            return;
        }

        monthStr = validateMonth(monthStr);
        while(monthStr.equals("Invalid month")) {
            System.out.println("Please input a valid month");
            monthStr = scanner.nextLine();
            monthStr = validateMonth(monthStr);
        }

        serv.getFriendShipsByMonth(Long.parseLong(idUser),monthStr).forEach(f -> {
            Optional<Utilizator> user1 = serv.find(f.getId().getLeft());
            Optional<Utilizator> user2 = serv.find(f.getId().getLeft());
            String str = user1.get().getFirstName() + " " + user1.get().getLastName() + " | " + user2.get().getFirstName()
                    + " " + user2.get().getLastName() + " | " + f.getDate().toString();
            System.out.println(str);
        });

    }

    private static boolean isValidMonthName(String monthStr) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};
        for (String monthName : monthNames) {
            if (monthName.equalsIgnoreCase(monthStr)) {
                return true;
            }
        }
        return false;
    }

    private String validateMonth(String monthStr) {
        if(isValidMonthName(monthStr)){
            return monthStr;
        }
        switch (monthStr){
            case "1":
                return "January";
            case "2":
                return "February";
            case "3":
                return "March";
            case "4":
                return "April";
            case "5":
                return "May";
            case "6":
                return "June";
            case "7":
                return "July";
            case "8":
                return "August";
            case "9":
                return "September";
            case "10":
                return "October";
            case "11":
                return "November";
            case "12":
                return "December";
            default:
                return "Invalid month";

        }

    }

    public void printMenu(){
        System.out.println("MENU");
        System.out.println("[/] Show menu/go back.");
        System.out.println("[1] Adauga utilizator nou.");
        System.out.println("[2] Modifica utilizator.");
        System.out.println("[3] Sterge utilizator existent.");
        System.out.println("[4] Adauga prietenie.");
        System.out.println("[5] Sterge prietenie.");
        System.out.println("[6] Afiseaza prietenii unui utilizator dintr-o anumita luna.");
        System.out.println("[REMOVED] Afiseaza numar de comunitati.");
        System.out.println("[REMOVED] Afiseaza cea mai sociabila comunitate.");
        System.out.println("[x] Inchide aplicatia.");
    }



    /*
    public void removeFriendUI() {
        System.out.println("Enter user's ID:");
        String userID = scanner.nextLine();
        while(userID.isEmpty()){
            System.out.println("ID cannot be empty. Please input ID.");
            userID = scanner.nextLine();
            if (!(userID.isEmpty()))
                break;
        }
        if(userID.equals("/")) {
            printMenu();
            return;
        }
        if(serv.find(Long.valueOf(userID)).isEmpty()) {
            System.err.println("No user with specified id.");
            return;
        }
        System.out.println("Enter friend's ID: ");
        String friendID = scanner.nextLine();
        while(friendID.isEmpty()){
            System.out.println("ID cannot be empty. Please input ID.");
            friendID = scanner.nextLine();
            if (!(friendID.isEmpty()))
                break;
        }

        if (friendID.equals("/")) {
            printMenu();
            return;
        }
        if (serv.find(Long.valueOf(friendID)).isEmpty()) {
            System.err.println("No user with specified id.");
            return;
        }
        Long userIDLong = Long.valueOf(userID);
        Long friendIDLong = Long.valueOf(friendID);
        if (serv.removeFriend(userIDLong, friendIDLong) == 0)
            System.out.println("Success!");
    }

    public void printNumberOfCommunities() {
        Integer nr = serv.getNumberOfCommunities();
        System.out.println("There are "+nr+" communities.");
    }

    public void printMostPopularCommunity() {
        System.out.println("[1] For list containing users' names.");
        System.out.println("[2] For list containing only users' IDs");
        System.out.print("> ");
        String input = scanner.nextLine();
        if(input.equals("/"))
            return;
        if (input.equals("1")) {
            for(Utilizator user: serv.getMostPopularCommunityUsers()) {
                System.out.println(user);
            }
        } else if (input.equals("2")) {
            System.out.println(serv.getMostPopularCommunityIDs());
        } else {
            System.err.println("Invalid input!");
        }
    }

     */
}
