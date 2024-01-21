package com.model;
import com.model.Comments;

import javax.persistence.*;

import org.hibernate.Hibernate;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tickets")
public class Ticket implements TicketObserver{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "priority_id", referencedColumnName = "id")
    private Priority priority;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User ticketOwner;

    @ManyToOne
    @JoinColumn(name = "assigned_user_id", referencedColumnName = "id")
    private User assignedUser;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<Comments> comments;

    public void addComment(Comments comment) {
        comments.add(comment);
        comment.setTicket(this);
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "closed_at")
    private Date closedAt;

    @Column(name="description")
    private String description;

    public Long getId() {
        return id;
    }

    @Transient
    private TicketSubject ticketSubject;

    public Ticket() {
        this.ticketSubject = new TicketSubject();
        this.ticketSubject.attach(this); // Register the ticket as an observer
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public User getTicketOwner() {
        return ticketOwner;
    }

    public void setTicketOwner(User ticketOwner) {
        this.ticketOwner = ticketOwner;
    }

    public User getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Date closedAt) {
        this.closedAt = closedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter for the TicketSubject (used for attaching and detaching observers)
    public TicketSubject getTicketSubject() {
        return ticketSubject;
    }

    public void update(Ticket ticket) {
        String message = "Notification: Ticket with ID " + ticket.getId() + " has been updated.";
        sendMessage(message);
    }

    public void sendMessage(String message) {
        // Assuming ticketOwner is the user to be notified
        if (assignedUser != null) {
            assignedUser.receiveMessage(message);
        }
    }

    // JPQL query to get all Tickets
    public static List<Ticket> getAllTickets(EntityManager em) {

        String jpql = "SELECT t FROM Ticket t";
        
        TypedQuery<Ticket> query = em.createQuery(jpql, Ticket.class);
        
        return query.getResultList();

    }

    public static List<Ticket> getTicketsByAssignee(EntityManager em, User assignee) {
        String jpql = "SELECT t FROM Ticket t WHERE t.assignedUser = :assignee";
        TypedQuery<Ticket> query = em.createQuery(jpql, Ticket.class)
                .setParameter("assignee", assignee);
        return query.getResultList();
    }

    public List<Comments> getComments() {
        // Ensure that comments are loaded from the database before returning
        Hibernate.initialize(comments);
        return comments;
    }

}
