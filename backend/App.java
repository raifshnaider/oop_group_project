package backend;

import backend.config.DatabaseConfig;
import console.MainMenu;

import java.sql.Connection;

public class App {
    public static void main(String[] args) {
        System.out.println("Запуск приложения...");
        System.out.println(backend.util.PasswordHasher.hash("test123"));


        // Проверяем подключение к БД перед запуском меню
        try (Connection conn = DatabaseConfig.getConnection()) {
            System.out.println("✅ Подключение к БД успешно: " + conn.getMetaData().getURL());
        } catch (Exception e) {
            System.out.println("❌ Ошибка подключения к БД. Приложение остановлено.");
            e.printStackTrace();
            return; // не запускаем меню, если БД недоступна
        }

        // Запускаем главное меню
        MainMenu menu = new MainMenu();
        menu.start();
    }
}
