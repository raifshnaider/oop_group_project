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

    // Храним корзину в памяти: ID товара -> Количество
    private final Map<Long, Integer> cart = new HashMap<>();

    public void show(Scanner scanner) {
        while (true) {
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
                showCart();
            } else if ("p".equals(input)) {
                checkout();
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
                return;
            }

            System.out.print("Enter quantity: ");
            String qtyStr = scanner.nextLine();
            int qty = Integer.parseInt(qtyStr);

            if (qty <= 0) {
                System.out.println("❌ Quantity must be positive.");
                return;
            }

            // Добавляем или обновляем количество
            cart.merge(prodId, qty, Integer::sum);
            System.out.println("✅ Added to cart!");

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid command.");
        }
    }

    private void showCart() {
        if (cart.isEmpty()) {
            System.out.println("\n🛒 Cart is empty.");
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
    }

    private int getCartSize() {
        return cart.values().stream().mapToInt(Integer::intValue).sum();
    }

    // --- ВОТ ЭТОТ МЕТОД ОБНОВЛЕН ---
    private void checkout() {
        if (cart.isEmpty()) {
            System.out.println("⚠️ Cart is empty! Add items first.");
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
    }
}
