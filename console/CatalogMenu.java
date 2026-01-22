package console;

import backend.entity.Product;
import backend.service.CatalogService;
import backend.service.OrderService;
import backend.dto.FullOrderDTO;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class CatalogMenu {
    private final CatalogService catalogService = new CatalogService();
    private final OrderService orderService = new OrderService();

    private final Map<Long, Integer> cart = new HashMap<>();

    public void show(Scanner scanner) {
        while (true) {
            clearScreen(); // <--- 🔥 ТУТ ОЧИЩАЕТСЯ ЭКРАН

            System.out.println("\n--- CATALOG ---");
            List<Product> products = catalogService.getAllProducts();
            for (Product p : products) {
                System.out.printf("[%d] %s - $%.2f (Stock: %d)\n",
                        p.getId(), p.getName(), p.getPrice(), p.getStock());
            }

            System.out.println("\n--- ACTIONS ---");
            System.out.println("[ID] Enter product ID to add to cart");
            System.out.println("[c]  View Cart (" + getCartSize() + " items)");
            System.out.println("[p]  Pay / Checkout");
            System.out.println("[0]  Back to Main Menu");
            System.out.print("> ");

            String input = scanner.nextLine();

            if ("0".equals(input)) {
                return; // Выход назад
            } else if ("c".equals(input)) {
                showCart(scanner); // 🔥 Добавил scanner, чтобы сделать паузу при просмотре
            } else if ("p".equals(input)) {
                checkout(scanner); // 🔥 Добавил scanner, чтобы сделать паузу после чека
            } else {
                addToCart(input, scanner);
            }
        }
    }

    private void addToCart(String inputId, Scanner scanner) {
        try {
            Long prodId = Long.parseLong(inputId);
            Optional<Product> productOpt = catalogService.findById(prodId);

            if (productOpt.isEmpty()) {
                System.out.println("❌ Product not found!");
                pressEnterToContinue(scanner); // 🔥 Пауза
                return;
            }

            System.out.print("Enter quantity: ");
            String qtyStr = scanner.nextLine();
            int qty = Integer.parseInt(qtyStr);

            if (qty <= 0) {
                System.out.println("❌ Quantity must be positive.");
                pressEnterToContinue(scanner);
                return;
            }

            // Добавляем или обновляем количество
            cart.merge(prodId, qty, Integer::sum);
            System.out.println("✅ Added to cart!");
            // pressEnterToContinue(scanner); // Здесь можно не делать паузу, чтобы быстро добавлять

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid command.");
            pressEnterToContinue(scanner); // 🔥 Пауза при ошибке
        }
    }

    private void showCart(Scanner scanner) {
        if (cart.isEmpty()) {
            System.out.println("\n🛒 Cart is empty.");
            pressEnterToContinue(scanner);
            return;
        }

        System.out.println("\n🛒 --- YOUR CART ---");
        BigDecimal estimatedTotal = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Long prodId = entry.getKey();
            Integer qty = entry.getValue();

            Optional<Product> pOpt = catalogService.findById(prodId);
            if (pOpt.isPresent()) {
                Product p = pOpt.get();
                BigDecimal lineTotal = p.getPrice().multiply(BigDecimal.valueOf(qty));
                estimatedTotal = estimatedTotal.add(lineTotal);

                System.out.printf("- %s x %d = $%.2f\n", p.getName(), qty, lineTotal);
            }
        }
        System.out.println("---------------------");
        System.out.printf("Total: $%.2f\n", estimatedTotal);

        pressEnterToContinue(scanner); // 🔥 Пауза, чтобы успеть прочитать
    }

    private int getCartSize() {
        return cart.values().stream().mapToInt(Integer::intValue).sum();
    }

    private void checkout(Scanner scanner) {
        if (cart.isEmpty()) {
            System.out.println("⚠️ Cart is empty! Add items first.");
            pressEnterToContinue(scanner);
            return;
        }

        System.out.println("\n💳 Processing payment...");

        try {
            // Оформляем заказ
            FullOrderDTO receipt = orderService.placeOrder(cart);

            // Выводим красивый чек
            System.out.println("\n*********************************");
            System.out.println("          PAYMENT RECEIPT        ");
            System.out.println("*********************************");
            System.out.println("Order ID: " + receipt.orderId);
            System.out.println("Customer: " + receipt.buyerEmail);
            System.out.println("Status:   " + receipt.status);
            System.out.println("---------------------------------");

            for (FullOrderDTO.OrderItemInfo item : receipt.items) {
                BigDecimal lineSum = item.price.multiply(BigDecimal.valueOf(item.quantity));
                System.out.printf("%-15s x%d = $%.2f\n", item.productName, item.quantity, lineSum);
            }

            System.out.println("---------------------------------");
            System.out.printf("TOTAL PAID:           $%.2f\n", receipt.totalAmount);
            System.out.println("*********************************\n");
            System.out.println("✅ Thank you for your purchase!");

            cart.clear(); // Очищаем корзину

        } catch (Exception e) {
            System.out.println("❌ Transaction Failed: " + e.getMessage());
        }

        pressEnterToContinue(scanner); // 🔥 Пауза, чтобы успеть прочитать чек!
    }

    // --- Вспомогательные методы ---

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void pressEnterToContinue(Scanner scanner) {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
