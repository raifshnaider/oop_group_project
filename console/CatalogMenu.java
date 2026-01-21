package console;

import backend.entity.Product;
import backend.service.CatalogService;
import backend.service.OrderService;
import backend.dto.FullOrderDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CatalogMenu {
    private final CatalogService catalogService = new CatalogService();
    private final OrderService orderService = new OrderService();

    public void show(Scanner scanner) {
        List<Product> products = catalogService.getAllProducts();
        System.out.println("\n--- CATALOG ---");
        for (Product p : products) {
            System.out.println(p);
        }

        System.out.println("\nEnter Product ID to buy (or 0 to back):");
        String input = scanner.nextLine();
        try {
            Long prodId = Long.parseLong(input);
            if (prodId == 0) return;

            System.out.println("Enter quantity:");
            int qty = Integer.parseInt(scanner.nextLine());

            System.out.println("Enter delivery address:");
            String address = scanner.nextLine().trim();

            Map<Long, Integer> cart = new HashMap<>();
            cart.put(prodId, qty);

            FullOrderDTO result = orderService.placeOrder(cart, address);
            System.out.println("Order created successfully!");
            System.out.println(result);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
