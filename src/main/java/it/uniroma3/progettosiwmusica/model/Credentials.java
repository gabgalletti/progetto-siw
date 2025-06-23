package it.uniroma3.progettosiwmusica.model;

import jakarta.persistence.*;

@Entity
public class Credentials {
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String DEFAULT_ROLE = "DEFAULT";
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;
    private String password;

    @Column(name = "user_role")
    private String role;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isAdmin() {
        return ADMIN_ROLE.equals(role);
    }

    public boolean isDefault() {
        return DEFAULT_ROLE.equals(role);
    }
}


