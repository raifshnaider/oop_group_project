package backend.service;

import backend.config.SessionContext;
import backend.entity.User;
import backend.enums.Role;
import backend.repository.UserRepository;
import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository = new UserRepository();

    public boolean login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) {
                SessionContext.getInstance().setCurrentUser(user);
                return true;
            }
        }
        return false;
    }

    public String register(String username, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            return "Error: Email already registered!";
        }

        User newUser = new User(username, email, password, Role.CUSTOMER);
        userRepository.save(newUser);

        return "Registration successful! Please login.";
    }
}
