package backend.config;

import backend.entity.User;

public class SessionContext {
    private static final SessionContext INSTANCE = new SessionContext();
    private User currentUser;

    private SessionContext() {}

    public static SessionContext getInstance() {
        return INSTANCE;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        this.currentUser = null;
    }
}
