package backend.service;

import backend.config.SessionContext;
import backend.entity.User;
import backend.repository.UserRepository;
import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository = new UserRepository();

    public boolean login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // В реальном проекте тут проверка хеша
            if (user.getPassword().equals(password)) {
                SessionContext.getInstance().setCurrentUser(user);
                return true;
            }
        }
        return false;
    }
}
