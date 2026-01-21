package console;

import backend.config.SessionContext;
import backend.enums.Role; // <-- Добавлен импорт!
import java.util.Scanner;

public class MainMenu {
    private final AuthMenu authMenu = new AuthMenu();
    private final CatalogMenu catalogMenu = new CatalogMenu();
    private final OrderMenu orderMenu = new OrderMenu();
    private final AdminMenu adminMenu = new AdminMenu(); // <-- Добавлен админ-класс

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (SessionContext.getInstance().getCurrentUser() == null) {
                authMenu.show(scanner);
            } else {
                System.out.println("\n=== MAIN MENU ===");
                System.out.println("1. Catalog");
                System.out.println("2. My Orders");

                // Проверяем роль: Если Админ, показываем пункт 3
                if (SessionContext.getInstance().getCurrentUser().getRole() == Role.ADMIN) {
                    System.out.println("3. 👮 Admin Panel");
                }

                System.out.println("0. Logout");
                System.out.print("> ");

                String choice = scanner.nextLine();
                switch (choice) {
                    case "1" -> catalogMenu.show(scanner);
                    case "2" -> orderMenu.showMyOrders();
                    case "3" -> {
                        // Защита: запускаем только если реально Админ
                        if (SessionContext.getInstance().getCurrentUser().getRole() == Role.ADMIN) {
                            adminMenu.show(scanner);
                        } else {
                            System.out.println("Invalid option");
                        }
                    }
                    case "0" -> SessionContext.getInstance().logout();
                    default -> System.out.println("Invalid option");
                }
            }
        }
    }
}
