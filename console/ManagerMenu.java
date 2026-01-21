package console;

import backend.entity.Product;
import backend.service.CatalogService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ManagerMenu {
    private final CatalogService catalogService = new CatalogService();

    public void show(Scanner scanner) {
        while (true) {
            System.out.println("\n=== 📦 MANAGER PANEL ===");
            System.out.println("1. View All Products");
            System.out.println("2. Add New Product");
            System.out.println("3. Update Price");
            System.out.println("4. Add Stock");
            System.out.println("5. Delete Product"); // 🔥 Новая кнопка
            System.out.println("0. Back");
            System.out.print("> ");

            String choice = scanner.nextLine();

            if ("0".equals(choice)) return;

            try {
                switch (choice) {
                    case "1" -> viewProducts();
                    case "2" -> addProduct(scanner);
                    case "3" -> updatePrice(scanner);
                    case "4" -> updateStock(scanner);
                    case "5" -> deleteProduct(scanner); // 🔥 Вызов метода
                    default -> System.out.println("Invalid option");
                }
            } catch (Exception e) {
                // Здесь мы ловим ошибки из CatalogService (цена < 0, ID не найден и т.д.)
                System.out.println("❌ ERROR: " + e.getMessage());
            }
        }
    }

    private void viewProducts() {
        List<Product> products = catalogService.getAllProducts();
        System.out.println("\n--- PRODUCT LIST ---");
        System.out.printf("%-5s %-20s %-10s %-10s\n", "ID", "Name", "Price", "Stock");
        System.out.println("-------------------------------------------------------");
        for (Product p : products) {
            System.out.printf("%-5d %-20s $%-9.2f %-10d\n",
                    p.getId(), p.getName(), p.getPrice(), p.getStock());
        }
    }

    private void addProduct(Scanner scanner) {
        System.out.print("Product Name: ");
        String name = scanner.nextLine();

        System.out.print("Price: ");
        BigDecimal price = new BigDecimal(scanner.nextLine());

        System.out.print("Stock: ");
        int stock = Integer.parseInt(scanner.nextLine());

        System.out.print("Category ID: ");
        Long catId = Long.parseLong(scanner.nextLine());

        catalogService.addNewProduct(name, price, stock, catId);
        System.out.println("✅ Product added successfully!");
    }

    private void updatePrice(Scanner scanner) {
        System.out.print("Product ID: ");
        Long id = Long.parseLong(scanner.nextLine());
        System.out.print("New Price: ");
        BigDecimal price = new BigDecimal(scanner.nextLine());

        catalogService.changePrice(id, price);
        System.out.println("✅ Price updated!");
    }

    private void updateStock(Scanner scanner) {
        System.out.print("Product ID: ");
        Long id = Long.parseLong(scanner.nextLine());
        System.out.print("Add to Stock: ");
        int qty = Integer.parseInt(scanner.nextLine());

        catalogService.addStock(id, qty);
        System.out.println("✅ Stock updated!");
    }

    // 🔥 НОВЫЙ МЕТОД: Удаление
    private void deleteProduct(Scanner scanner) {
        System.out.print("Enter Product ID to DELETE: ");
        Long id = Long.parseLong(scanner.nextLine());

        System.out.print("⚠️ Are you sure? (type 'yes' to confirm): ");
        String confirm = scanner.nextLine();

        if ("yes".equalsIgnoreCase(confirm)) {
            catalogService.deleteProduct(id);
            System.out.println("🗑️ Product deleted successfully!");
        } else {
            System.out.println("Operation cancelled.");
        }
    }
}
