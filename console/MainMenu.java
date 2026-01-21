package console;

import backend.config.SessionContext;
import backend.enums.Role;
import java.util.Scanner;

public class MainMenu {
    private final AuthMenu authMenu = new AuthMenu();
    private final CatalogMenu catalogMenu = new CatalogMenu();
    private final OrderMenu orderMenu = new OrderMenu();
    private final AdminMenu adminMenu = new AdminMenu();
    private final ManagerMenu managerMenu = new ManagerMenu(); // 🔥 ДОБАВЛЕНО!

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (SessionContext.getInstance().getCurrentUser() == null) {
                authMenu.show(scanner);
            } else {
                Role role = SessionContext.getInstance().getCurrentUser().getRole();

                System.out.println("\n=== MAIN MENU ===");
                System.out.println("1. Catalog");
                System.out.println("2. My Orders");

                // 🔥 НОВАЯ ЛОГИКА:
                if (role == Role.ADMIN) {
                    System.out.println("3. 👮 Admin Panel");
                } else if (role == Role.MANAGER) {
                    System.out.println("3. 📦 Manager Panel");
                }

                System.out.println("0. Logout");
                System.out.print("> ");

                String choice = scanner.nextLine();

                switch (choice) {
                    case "1" -> catalogMenu.show(scanner);
                    case "2" -> orderMenu.showMyOrders();
                    case "3" -> {
                        if (role == Role.ADMIN) {
                            adminMenu.show(scanner);
                        } else if (role == Role.MANAGER) {
                            managerMenu.show(scanner);
                        } else {
                            System.out.println("❌ Access Denied");
                        }
                    }
                    case "0" -> SessionContext.getInstance().logout();
                    default -> System.out.println("Invalid option");
                }
            }
        }
    }
}
