package backend;

import console.MainMenu;

public class App {
    public static void main(String[] args) {
        System.out.println("Запуск приложения...");

        // Создаем и запускаем главное меню
        MainMenu menu = new MainMenu();
        menu.start();
    }
}
