package backend.service;

import backend.config.SessionContext;
import backend.entity.User;
import backend.enums.Role;
import backend.repository.UserRepository;
import backend.util.PasswordHasher;

import java.util.List;
import java.util.Optional;

public class AuthService {

    private final UserRepository userRepository = new UserRepository();

    public boolean login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        if (!PasswordHasher.check(password, user.getPasswordHash())) {
            return false;
        }

        SessionContext.getInstance().setCurrentUser(user);
        return true;
    }

    public String register(String email, String password, String fullName, String phone) {
        if (email == null || email.isBlank() || !email.contains("@") || email.startsWith("@")) {
            return "Error: Invalid email format (must contain @).";
        }
        if (userRepository.findByEmail(email).isPresent()) {
            return "Error: Email already registered!";
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(PasswordHasher.hash(password));
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setRole(Role.CUSTOMER);
        user.setActive(true);

        userRepository.save(user);

        return "Registration successful! Please login.";
    }

    // 🔥 --- МЕТОДЫ ДЛЯ АДМИНКИ ---

    // 1. Получить список всех пользователей
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 2. Сменить роль пользователю
    public void changeUserRole(Long userId, String roleName) {
        try {
            // Приводим к верхнему регистру (admin -> ADMIN)
            Role role = Role.valueOf(roleName.toUpperCase());
            userRepository.updateRole(userId, role);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Ошибка: Такой роли нет! (Используйте: ADMIN, MANAGER, CUSTOMER)");
        }
    }
}
