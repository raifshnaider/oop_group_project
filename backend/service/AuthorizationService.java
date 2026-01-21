package backend.service;

import backend.config.SessionContext;
import backend.entity.User;
import backend.enums.Role;

public class AuthorizationService {
    public boolean isAdmin() {
        User user = SessionContext.getInstance().getCurrentUser();
        return user != null && user.getRole() == Role.ADMIN;
    }

    public boolean isManager() {
        User user = SessionContext.getInstance().getCurrentUser();
        return user != null && (user.getRole() == Role.MANAGER || user.getRole() == Role.ADMIN);
    }
}
