package com.model;

import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "priorities", uniqueConstraints = @UniqueConstraint(columnNames = "priority_name"))
public class Priority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "priority_name", unique = true)
    private String priorityName;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    // Method to get all categories
    public static List<Priority> getAllPriorities(EntityManager entityManager) {
        String jpql = "SELECT p FROM Priority p";
        TypedQuery<Priority> query = entityManager.createQuery(jpql, Priority.class);
        return query.getResultList();
    }
}
