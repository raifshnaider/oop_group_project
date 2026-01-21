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
                showCart(); // <-- Твоя новая функция
            } else if ("p".equals(input)) {
                checkout();
            } else {
                addToCart(input, scanner);
            }
        }
    }

    // 1. Функция добавления в корзину
    private void addToCart(String inputId, Scanner scanner) {
        try {
            Long prodId = Long.parseLong(inputId);
            Optional<Product> productOpt = catalogService.findById(prodId);

            if (productOpt.isEmpty()) {
                System.out.println("❌ Product not found!");
                return;
            }

            System.out.print("Enter quantity: ");
            int qty = Integer.parseInt(scanner.nextLine());

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

    // 2. Функция просмотра корзины (ТО, ЧТО ТЫ ПРОСИЛ)
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

            // Ищем товар, чтобы узнать его имя и цену
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

    // Вспомогательная: размер корзины
    private int getCartSize() {
        return cart.values().stream().mapToInt(Integer::intValue).sum();
    }

    // 3. Функция оформления заказа
    private void checkout() {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty!");
            return;
        }

        try {
            FullOrderDTO result = orderService.placeOrder(cart);
            System.out.println("🎉 ORDER PLACED SUCCESSFULLY!");
            System.out.println(result);
            cart.clear(); // Очищаем корзину после покупки
        } catch (Exception e) {
            System.out.println("❌ Error placing order: " + e.getMessage());
        }
    }
}
