package com.model;

import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "status", uniqueConstraints = @UniqueConstraint(columnNames = "status_name"))
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "status_name", unique = true)
    private String statusName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public static List<Status> getAllStatusses(EntityManager entityManager) {
        String jpql = "SELECT s FROM Status s";
        TypedQuery<Status> query = entityManager.createQuery(jpql, Status.class);
        return query.getResultList();
    }
    
}
