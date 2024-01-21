package com.model;

import javax.persistence.*;

import java.util.List;

@Entity 
@Table(name="user", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Profile profile;

    @ManyToOne
    @JoinColumn(name="role_id")  
    private Role role;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;
    
    @Column(name="email", unique = true)
    private String email;

    @Column(name="password")
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void receiveMessage(String message) {
        UserNotificationManager.addNotification(id, "Received Message: " + message) ;
    }

    // JPQL query to get users by role
    public static List<User> getUsersByRole(EntityManager em, String roleName) {
        String jpql = "SELECT u FROM User u WHERE u.role.roleName = :roleName";

        TypedQuery<User> query = em.createQuery(jpql, User.class);
        query.setParameter("roleName", roleName);

        return query.getResultList();
    }
}
