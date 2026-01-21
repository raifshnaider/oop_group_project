package console;

import backend.entity.User;
import backend.service.AuthService;
import java.util.List;
import backend.enums.Role;
import java.util.Scanner;

public class AdminMenu {
    private final AuthService authService = new AuthService();

    public void show(Scanner scanner) {
        while (true) {
            System.out.println("\n=== 👮 ADMIN PANEL (USER MANAGEMENT) ===");
            System.out.println("1. List All Users");
            System.out.println("2. Change User Role");
            System.out.println("0. Back");
            System.out.print("> ");

            String choice = scanner.nextLine();

            if ("0".equals(choice)) return;

            if ("1".equals(choice)) {
                showAllUsers();
            } else if ("2".equals(choice)) {
                changeRole(scanner);
            }
        }
    }

    private void showAllUsers() {
        List<User> users = authService.getAllUsers();
        System.out.println("\n--- USER LIST ---");
        System.out.printf("%-5s %-20s %-30s %-10s\n", "ID", "Name", "Email", "Role");
        System.out.println("-----------------------------------------------------------------------");
        for (User u : users) {
            System.out.printf("%-5d %-20s %-30s %-10s\n",
                    u.getId(), u.getFullName(), u.getEmail(), u.getRole());
        }
    }

    private void changeRole(Scanner scanner) {
        System.out.print("\nEnter User ID: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());

            System.out.print("Enter New Role (ADMIN / MANAGER / CUSTOMER): ");
            String roleInput = scanner.nextLine().trim().toUpperCase();

            Role newRole;
            try {
                newRole = Role.valueOf(roleInput);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Invalid role. Use: ADMIN, MANAGER, CUSTOMER");
                return;
            }

            authService.changeUserRole(id, newRole); // ✅ ТЕПЕРЬ Role
            System.out.println("✅ Role updated successfully!");

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid user ID format");
        }
    }

}
