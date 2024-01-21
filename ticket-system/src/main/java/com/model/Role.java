package com.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "role", uniqueConstraints = @UniqueConstraint(columnNames = "role_name"))
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy="role")
    private List<User> users;
    
    @Column(name = "role_name", unique = true)
    private String roleName;
    
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    // Utility method to add a user to the role
    public void addUser(User user) {
        users.add(user);
        user.setRole(this);
    }

    public static List<Role> getAllRoles(EntityManager entityManager) {
        String jpql = "SELECT r FROM Role r";
        TypedQuery<Role> query = entityManager.createQuery(jpql, Role.class);
        return query.getResultList();
    }
}
