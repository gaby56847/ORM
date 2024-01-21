package com.model;

import javax.persistence.*;

import java.util.List;

@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = "categorie_name"))
public class Category implements TicketCategory{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "categorie_name", unique = true)
    private String categorieName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
    this.id = id; 
    }

    public String getCategorieName() {
        return categorieName;
    }

    @Override
    public String getName() {
        return this.categorieName;
    }

    public void setCategorieName(String categorieName) {
        this.categorieName = categorieName;
    }

    // Method to get all categories
    public static List<Category> getAllCategories(EntityManager entityManager) {
        String jpql = "SELECT c FROM Category c";
        TypedQuery<Category> query = entityManager.createQuery(jpql, Category.class);
        return query.getResultList();
    }
}
