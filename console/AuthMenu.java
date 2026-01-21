package console;

import backend.service.AuthService;
import java.util.Scanner;

public class AuthMenu {
    private final AuthService authService = new AuthService();

    public void show(Scanner scanner) {
        System.out.println("\n=== WELCOME ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("0. Exit");
        System.out.print("> ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> login(scanner);
            case "2" -> register(scanner);
            case "0" -> System.exit(0);
            default -> System.out.println("Invalid option");
        }
    }

    private void login(Scanner scanner) {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (authService.login(email, password)) {
            System.out.println("Login Success!");
        } else {
            System.out.println("Wrong email or password.");
        }
    }

    private void register(Scanner scanner) {
        System.out.println("\n--- REGISTRATION ---");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        String result = authService.register(username, email, password);
        System.out.println(result);
    }
}
