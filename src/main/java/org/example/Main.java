package org.example;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        String argIp = "";
        int argPort;
        String url = "";
        boolean input = false;
        while(input == false) {
            if (args.length > 0) {
                try {
                    argIp = args[0];
                    argPort = Integer.parseInt(args[1]);
                    System.out.printf("You're client app is connecting to server at %s:%s %n", argIp, argPort);
                    url = "http://" + argIp + ":" + argPort;
                    input = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("The correct use of Client App is java Main [Server Ip] [Server Port]");
            }
        }

        Utils utils = new Utils();
        // Getting listening port from server
        int listeningPort = utils.getListeningPort(url);
        // Connect to listening port / socket
        try {
            Socket socket = new Socket(argIp, listeningPort);
            Client clientSocket = new Client(socket);
            clientSocket.listeningToServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // prompt menu to user
        boolean choosing = true;
        boolean initialStepCompleted = false;
        boolean secondMenu = false;
//        String name = "";
//        String username = "";
//        String password = "";
        Scanner scanner = new Scanner(System.in);
        while (choosing) {
            if (!initialStepCompleted) {
                System.out.println("Choose one option:");
                System.out.println("1 - Create account");
                System.out.println("2 - Login");
                System.out.println("3 - See available shows");
                int choice = scanner.nextInt();
                scanner.nextLine(); // to consume the newline character

                switch (choice) {
                    case 1:
                        System.out.print("Enter name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter username: ");
                        String username = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String password = scanner.nextLine();
                        utils.createAccount(name, username, password);
                        initialStepCompleted = true; // Set to true once the initial steps are completed
                        secondMenu = true;
                        break;
                    case 2:
                        System.out.print("Enter username: ");
                        username = scanner.nextLine();
                        System.out.print("Enter password: ");
                        password = scanner.nextLine();
                        utils.loginAccount(username, password);
                        initialStepCompleted = true; // Set to true once the initial steps are completed
                        secondMenu = true;
                        break;
                    case 3:
                        utils.getAvailableShows();
                        initialStepCompleted = false;
                        secondMenu = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Please select again.");
                }
            } else if (secondMenu){
                System.out.println("Choose one option:");
                System.out.println("1 - Edit my account");
                System.out.println("2 - Search shows");
                System.out.println("3 - Select show");
                System.out.println("4 - My unpaid reservations");
                System.out.println("5 - My paid reservations");
                System.out.println("6 - Make reservation");
                System.out.println("7 - Delete reservation");
                System.out.println("8 - Pay Reservation");
                System.out.println("9 - Logout");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        System.out.println("Enter old username: ");
                        String username = scanner.nextLine();
                        System.out.print("Enter new name: ");
                        String newName = scanner.nextLine();
                        System.out.print("Enter new username: ");
                        String newUsername = scanner.nextLine();
                        System.out.print("Enter new password: ");
                        String newPassword = scanner.nextLine();
                        utils.updateAccount(username, newName, newUsername, newPassword);
                        break;
                    case 2:
                        System.out.println("Search shows:");
                        System.out.print("Enter pais (or press Enter to skip): ");
                        String pais = scanner.nextLine();
                        System.out.print("Enter local (or press Enter to skip): ");
                        String local = scanner.nextLine();
                        System.out.print("Enter tipo (or press Enter to skip): ");
                        String tipo = scanner.nextLine();
                        System.out.print("Enter data_hora (or press Enter to skip): ");
                        String data_hora = scanner.nextLine();

                        utils.searchShows(pais.isEmpty() ? null : pais,
                                local.isEmpty() ? null : local,
                                tipo.isEmpty() ? null : tipo,
                                data_hora.isEmpty() ? null : data_hora);
                        break;
                    case 3:
                        System.out.print("Enter show ID: ");
                        int showId = scanner.nextInt();
                        boolean showExists = utils.checkShowExists(showId);

                        if (showExists) {
                            String seats = utils.getSeatsForShow(showId);
                            System.out.println("Seats for the show: " + seats);
                        } else {
                            System.out.println("Show does not exist.");
                        }
                        break;
                    case 4:
                        utils.getUnpayedReservations(); //TODO (nao esta 100% correto porque para ja a função no servidor está a ir buscar todas as reservas da BD e não aquelas pertencentes ao user - é necessario implementar o JWT)
                        break;
                    case 5:
                        utils.getPayedReservations(); //TODO (nao esta 100% correto porque para ja a função no servidor está a ir buscar todas as reservas da BD e não aquelas pertencentes ao user - é necessario implementar o JWT)
                        break;
                    case 6:
                        System.out.println("Make Reservation:");
                        System.out.print("Insert Espetaculo Id: ");
                        int idEspetaculo = Integer.parseInt(scanner.nextLine());
                        System.out.print("Insert Seat Id: ");
                        int idSeat = Integer.parseInt(scanner.nextLine());
                        break;
                    case 7:
                        System.out.println("Delete Reservation:");
                        System.out.print("Insert Espetaculo Id: ");
                        idEspetaculo = Integer.parseInt(scanner.nextLine());
                        System.out.print("Insert Seat Id: ");
                        idSeat = Integer.parseInt(scanner.nextLine());
                        break;
                    case 8:
                        System.out.println("Pay Reservation:");
                    case 9:
                        initialStepCompleted = false; // Set to false to go back to the initial step
                        break;
                    // ... other cases ...
                }
            }
        }
        // etc.

    }
}