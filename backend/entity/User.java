package backend.entity;

import backend.enums.Role;

import java.time.OffsetDateTime;

public class User {
    private Long id;
    private String email;
    private String passwordHash;
    private Role role;
    private String fullName;
    private String phone;
    private boolean isActive;
    private OffsetDateTime createdAt;

    public User() {}

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public boolean isActive() { return isActive; }
    public OffsetDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(Role role) { this.role = role; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setActive(boolean active) { isActive = active; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
