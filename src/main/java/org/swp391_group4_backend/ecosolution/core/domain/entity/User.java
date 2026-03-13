package org.swp391_group4_backend.ecosolution.core.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    private UUID id;
    private String username;
    private String email;
    private String password;
    private String role;
    // Employer reference (UUID of boss enterprise) - optional
    private UUID employerId;

    // Gamification points
    private Integer points;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public UUID getEmployerId() { return employerId; }
    public void setEmployerId(UUID employerId) { this.employerId = employerId; }
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    // simple builder to match existing code usage
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final User u = new User();
        public Builder id(UUID id) { u.setId(id); return this; }
        public Builder username(String username) { u.setUsername(username); return this; }
        public Builder email(String email) { u.setEmail(email); return this; }
        public Builder password(String password) { u.setPassword(password); return this; }
        public Builder role(String role) { u.setRole(role); return this; }
        public Builder employerId(UUID employerId) { u.setEmployerId(employerId); return this; }
        public Builder points(Integer points) { u.setPoints(points); return this; }
        public User build() { return u; }
    }
}

