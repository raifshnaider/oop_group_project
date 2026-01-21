package console;

import backend.service.AuthService;
import java.util.Scanner;

public class AuthMenu {
    private final AuthService authService = new AuthService();

    public void show(Scanner scanner) {
        System.out.println("\n=== WELCOME ===");
        System.out.println("1. Login");
        System.out.println("2. Exit");
        System.out.print("> ");

        String choice = scanner.nextLine();
        if ("1".equals(choice)) {
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine(); // Если используешь PasswordHasher, в AuthService надо добавить хеширование

            if (authService.login(email, password)) {
                System.out.println("Success!");
            } else {
                System.out.println("Wrong credentials.");
            }
        } else if ("2".equals(choice)) {
            System.exit(0);
        }
    }
}
