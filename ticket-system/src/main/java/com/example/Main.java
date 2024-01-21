package com.example;

import java.util.List;
import java.util.Scanner;
import java.util.Date;

import javax.persistence.NoResultException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;

import com.model.Category;
import com.model.CategoryFactory;
import com.model.Comments;
import com.model.Priority;
import com.model.Profile;
import com.model.Role;
import com.model.Status;
import com.model.Ticket;
import com.model.User;
import com.model.UserNotificationManager;

public class Main {

    private static SessionFactory sessionFactory;
    private static Session session;

    public static void main(String[] args) {

        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        
        // Build SessionFactory
        sessionFactory = configuration.buildSessionFactory();

        // Create Session 
        session = sessionFactory.openSession();

        // Create and save roles, status, priorities on startup
        createAndSaveRoles();
        createAndSaveStatus();
        createAndSavePriority();

        User loggedInUser = null;
        
        // Use session to perform operations
        Scanner scanner = new Scanner(System.in);

        // Execute queries on startup
        executeQueriesOnStartup(session);

        while(session != null) {

            int choice = welcomeMenu(scanner);
      
            if(choice == 1) {
                loggedInUser = login(scanner);
                // Inside your main method after successful login
                if (loggedInUser != null) {
                    List<Ticket> assignedTickets = Ticket.getTicketsByAssignee(session, loggedInUser);
                    System.out.println("Tickets assigned to you:");
                    
                    for (Ticket ticket : assignedTickets) {
                        System.out.println("Ticket ID: " + ticket.getId());
                        System.out.println("Description: " + ticket.getDescription());
                        
                        // Display comments for the ticket
                        List<Comments> comments = ticket.getComments();
                        if (!comments.isEmpty()) {
                            System.out.println("Comments:");
                            for (Comments comment : comments) {
                                System.out.println(" - " + comment.getDescription());
                            }
                        } else {
                            System.out.println("No comments for this ticket.");
                        }
                        
                        System.out.println();
                    }
                
                    List<String> notifications = UserNotificationManager.getNotifications(loggedInUser.getId());
                    System.out.println(notifications);
                }

                if (loggedInUser == null) {
                    System.out.println("Invalid login credentials");
                    continue; 
                }
            } else if (choice == 2) {
              register(scanner);
              continue;
            }
      
            // Login or register done, show CRUD menu
            // Display menu based on user's role or privileges
            if (loggedInUser != null) {
                tableMenu(loggedInUser, scanner);
            }
      
        }
    }

    //menu's
    public static int welcomeMenu(Scanner scanner) {
        System.out.println("Welcome!");
        System.out.println("1. Login");
        System.out.println("2. Register");
        
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();

        // Consume the newline character
        scanner.nextLine();
    
        return choice;
    }
    
    public static void tableMenu(User user, Scanner scanner) {
        System.out.println("Welcome, " + user.getFirstName() + "!");
        System.out.println("1. Profile");
        System.out.println("2. Users");
        System.out.println("3. Roles");
        System.out.println("4. Categories");
        System.out.println("5. Comments");
        System.out.println("6. Statusses");
        System.out.println("7. Tickets");
        System.out.println("8. Priorities");
        System.out.println("9. Logout");

        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); 

        switch (choice) {
            case 1:
                ProfileMenu(user, scanner);
                break;
            case 2:
                userMenu(user, scanner);
                break;
            case 3:
                roleMenu(user, scanner);
                break;
            case 4:
                categoryMenu(user, scanner);
                break;
            case 5:
                commentMenu(user, scanner);
                break;
            case 6:
                statusMenu(user, scanner);
                break;
            case 7:
                ticketMenu(user, scanner);
                break;
            case 8:
                priorityMenu(user, scanner);
                break;
            case 9:
                System.out.println("Logout successful!");
                session.close();
                System.exit(0);
            default:
                System.out.println("Invalid choice. Please try again.");
        }

    }

    public static void ProfileMenu(User user, Scanner scanner) {
        System.out.println("User Profile:");
        System.out.println("Name: " + user.getFirstName() + " " + user.getLastName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Gender: " + user.getProfile().getGender());
        System.out.println("Mobile: " + user.getProfile().getMobile());
        System.out.println();

        tableMenu(user, scanner);

    }

    public static void userMenu(User user, Scanner scanner) {
        System.out.println("Welcome, " + user.getFirstName() + "!");
        System.out.println("1. Profile");
        System.out.println("2. Users");
        System.out.println("3. Roles");
        System.out.println("4. Categories");
        System.out.println("5. Comments");
        System.out.println("6. Statusses");
        System.out.println("7. Tickets");
        System.out.println("8. Priorities");
        System.out.println("9. Logout");

        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); 

        // switch (choice) {
        //     case 1:
        //         viewProfile(user);
        //         break;
        //     case 2:
        //         updateProfile(user, scanner);
        //         break;
        //     case 3:
        //         deleteProfile(user);
        //         break;
        //     case 4:
        //         System.out.println("Logout successful!");
        //         session.close();
        //         System.exit(0);
        //     default:
        //         System.out.println("Invalid choice. Please try again.");
        // }

    }

    public static void roleMenu(User user, Scanner scanner) {
        System.out.println("Roles");

        // Get all roles
        List<Role> allRoles = Role.getAllRoles(session);
        System.out.println("All Roles:");
        for (Role role : allRoles) {
            System.out.println(role.getRoleName());
        }

        // Display the table menu
        tableMenu(user, scanner);

    }

    public static void categoryMenu(User user, Scanner scanner) {
        System.out.println("1. Categories");
        System.out.println("2. Create Category");
        System.out.println("3. Edit Category");
        System.out.println("4. Delete Category");
        System.out.println("5. Logout");

        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); 

        switch (choice) {
            case 1:
                viewCategories(user, scanner);
                break;
            case 2:
                createCategory(user, scanner);
                break;
            case 3:
                updateCategory(user, scanner);
                break;
            case 4:
                deleteCategorie(user, scanner);
            case 5:
                System.out.println("Logout successful!");
                session.close();
                System.exit(0);
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    public static void commentMenu(User user, Scanner scanner) {
        System.out.println("1. Comments");
        System.out.println("2. Create comment");
        System.out.println("5. Logout");

        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); 

        switch (choice) {
            case 1:
                viewComments(user, scanner);
                break;
            case 2:
                createComment(user, scanner);
                break;
            case 3:
                System.out.println("Logout successful!");
                session.close();
                System.exit(0);
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    public static void statusMenu(User user, Scanner scanner) {
        System.out.println("Statusses");

        // Get all roles
        List<Status> allStatusses = Status.getAllStatusses(session);
        System.out.println("All Statusses:");
        for (Status status : allStatusses) {
            System.out.println(status.getStatusName());
        }

        System.out.println();

        // Display the table menu
        tableMenu(user, scanner);

    }

    public static void ticketMenu(User user, Scanner scanner) {
        System.out.println("1. Tickets");
        System.out.println("2. Create Ticket");
        System.out.println("3. Filter Ticket");
        System.out.println("4. Edit Ticket");
        System.out.println("5. Logout");

        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); 

        switch (choice) {
            case 1:
                viewTickets(user, scanner);
                break;
            case 2:
                createTicket(user, scanner);
                break;
            case 3:
                filterTicket(user, scanner);
                break;
            case 4:
                updateTicket(user, scanner);
                break;
            case 5:
                System.out.println("Logout successful!");
                session.close();
                System.exit(0);
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    public static void priorityMenu(User user, Scanner scanner) {
        System.out.println("Priorities");

        // Get all priorities
        List<Priority> allPriorities = Priority.getAllPriorities(session);
        System.out.println("All Priorities:");
        for (Priority priority : allPriorities) {
            System.out.println(priority.getPriorityName());
        }

        System.out.println();

        // Display the table menu
        tableMenu(user, scanner);
    }


    //user
    public static User login(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        User user = validateCredentials(username, password);

        if (user != null) {
            System.out.println("Login successful!");
            return user;
        } else {
            System.out.println("Invalid login credentials");
            return null;
        }
    }
    
    public static void register(Scanner scanner) {
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.println("Select gender:");
        System.out.println("1. Male");
        System.out.println("2. Female");
        System.out.println("3. Other");

        int genderChoice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        String gender;
        switch (genderChoice) {
            case 1:
                gender = "Male";
                break;
            case 2:
                gender = "Female";
                break;
            case 3:
                gender = "Other";
                break;
            default:
                System.out.println("Invalid choice. Defaulting to 'Other'.");
                gender = "Other";
        }

        System.out.print("Enter mobile: ");
        String mobile = scanner.nextLine();

        // Create User entity
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);

        // Create Profile entity
        Profile profile = new Profile();
        profile.setGender(gender);
        profile.setMobile(mobile);

        // Set the relationship between User and Profile
        user.setProfile(profile);
        profile.setUser(user);

        // Set roles for the user
        Role role;

        // Display the available roles for the user to choose
        List<Role> allRoles = Role.getAllRoles(session);

        System.out.println("Select role:");
        for (int i = 0; i < allRoles.size(); i++) {
            System.out.println((i + 1) + ". " + allRoles.get(i).getRoleName());
        }

        int roleChoice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        // Check if the selected roleChoice is valid
        if (roleChoice >= 1 && roleChoice <= allRoles.size()) {
            role = allRoles.get(roleChoice - 1);

            // Set the user's role
            user.setRole(role);

            // Begin transaction
            session.beginTransaction();

            try {
                // Persist User, Profile, and Role
                session.save(user);
                session.save(profile);
                // No need to save the role separately, as it's already managed

                // Commit the transaction
                session.getTransaction().commit();

                System.out.println("Registration successful!");
            } catch (Exception e) {
                // Rollback the transaction in case of any error
                session.getTransaction().rollback();
                e.printStackTrace();
                System.out.println("Registration failed. Please try again.");
            }
        } else {
            System.out.println("Invalid role choice. Registration failed.");
        }

    }
    
    public static User validateCredentials(String email, String password) {
        try {
            User user = session.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();
    
            String storedPassword = user.getPassword();
    
            if (storedPassword.equals(password)) {
                return user; // Return the User object upon successful login
            } else {
                return null; // Return null if the password doesn't match
            }
        } catch (NoResultException e) {
            return null; // Return null if the user is not found
        }
    }
    
    //category
    public static void viewCategories(User user, Scanner scanner){
        System.out.println("Categories");
  
        // Get all categories
        List<Category> categories = Category.getAllCategories(session);
      
        // Print header
        System.out.println("ID \t Name");
        
        // Print each category with ID 
        for(Category c : categories) {
          System.out.println(c.getId() + " \t " + c.getCategorieName()); 
        }
        System.out.println();

        // Display the table menu
        tableMenu(user, scanner);
    }
    
    public static void createCategory(User user, Scanner scanner){
        // Get category name input
        System.out.print("Enter category name: ");
        String name = scanner.nextLine();

        // Create category using factory
        CategoryFactory factory = new CategoryFactory();
        Category newCategory = factory.createCategory(name);

        // Persist category
        session.beginTransaction();
        session.save(newCategory);
        session.getTransaction().commit();

        System.out.println("New category created successfully!");
    }
    
    public static void updateCategory(User user, Scanner scanner) {
        // Get category id to update
        Long id = getCategoryIdInput(scanner); 
        
        // Find category
        Category category = session.find(Category.class, id);

        // Get updated name
        String updatedName = getCategoryNameInput(scanner, category);

        // Update category 
        category.setCategorieName(updatedName);
        
        session.update(category);

        System.out.println("Category updated successfully!");
    }
    
    public static void deleteCategorie(User user, Scanner scanner) {
        // Get category id
        Long categoryId = getCategoryIdInput(scanner);

        // Begin transaction
        session.beginTransaction();

        // Create delete query
        int rowsAffected = session.createQuery("DELETE FROM Category c WHERE c.id = :id")
                                    .setParameter("id", categoryId)
                                    .executeUpdate();

        // Check if category was deleted
        if(rowsAffected > 0) {
            System.out.println("Category deleted successfully");
        }

        // Commit transaction
        session.getTransaction().commit();
        
        // Clear session to force refresh
        session.clear();

        // Refresh associated tickets
        session.createQuery("UPDATE Ticket t SET t.category = null WHERE t.category.id = :id")
                .setParameter("id", categoryId)
                .executeUpdate();

        // Refresh session 
        session.refresh(session.get(Category.class, categoryId));

        // Print confirmation
        System.out.println("Category " + categoryId + " deleted");
    }

    public static Long getCategoryIdInput(Scanner scanner) {

        System.out.print("Enter category ID: ");
       
        // Read id from user input 
        Long categoryId = null;  
        while(categoryId == null) {
          String input = scanner.nextLine();
          try {
            categoryId = Long.parseLong(input);  
          } catch (NumberFormatException e) {
            System.out.print("Invalid ID, please enter a valid number: ");
          }
        }
    
        return categoryId;
    }

    public static String getCategoryNameInput(Scanner scanner, Category category) {

        String name;
        
        do {
          System.out.print("Enter new name (max 30 chars): ");
          name = scanner.nextLine();
        } while(name.trim().isEmpty());
      
        System.out.print("Confirm changing "+category.getName()+" to "+name+"? (Y/N): ");
        String confirm = scanner.nextLine();
      
        if(!confirm.equalsIgnoreCase("Y")) {
          return category.getName(); // no change
        }
      
        return name;
      
    }


    //ticket
    public static void viewTickets(User user, Scanner scanner){
        System.out.println("Tickets");
  
        // Get all tickets
        List<Ticket> tickets = Ticket.getAllTickets(session);
      
        // Print header
        System.out.println("ID \t Owner \t Assignee \t Description ");
        
        // Print each ticket with ID 
        for(Ticket c : tickets) {
          System.out.println(c.getId() + " \t " + c.getTicketOwner()+ " \t " + c.getAssignedUser() + " \t " + c.getDescription()); 
        }
        System.out.println();

        // Display the table menu
        tableMenu(user, scanner);
    }
    
    public static void createTicket(User user, Scanner scanner) {
        // Display available categories
        System.out.println("Available Categories:");
        List<Category> categories = Category.getAllCategories(session);
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i).getCategorieName());
        }
    
        // Choose a category
        System.out.print("Choose a category (enter the number): ");
        int categoryChoice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character
        Category selectedCategory = categories.get(categoryChoice - 1);
    
        // Display available users for assignment
        System.out.println("Available Users for Assignment:");
        List<User> usersForAssignment = User.getUsersByRole(session, "DEVELOPER"); // Assuming you want to assign developers
        for (int i = 0; i < usersForAssignment.size(); i++) {
            System.out.println((i + 1) + ". " + usersForAssignment.get(i).getFirstName() + " " +
                    usersForAssignment.get(i).getLastName());
        }
    
        // Choose an assignee
        System.out.print("Choose an assignee (enter the number): ");
        int assigneeChoice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character
        User selectedAssignee = usersForAssignment.get(assigneeChoice - 1);
    
        // Enter ticket description
        System.out.print("Enter ticket description: ");
        String description = scanner.nextLine();
    
        // Display available statuses
        System.out.println("Available Statuses:");
        List<Status> statuses = Status.getAllStatusses(session);
        for (int i = 0; i < statuses.size(); i++) {
            System.out.println((i + 1) + ". " + statuses.get(i).getStatusName());
        }
    
        // Choose a status
        System.out.print("Choose a status (enter the number): ");
        int statusChoice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character
        Status selectedStatus = statuses.get(statusChoice - 1);
    
        // Display available priorities
        System.out.println("Available Priorities:");
        List<Priority> priorities = Priority.getAllPriorities(session);
        for (int i = 0; i < priorities.size(); i++) {
            System.out.println((i + 1) + ". " + priorities.get(i).getPriorityName());
        }
    
        // Choose a priority
        System.out.print("Choose a priority (enter the number): ");
        int priorityChoice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character
        Priority selectedPriority = priorities.get(priorityChoice - 1);
    
        // Create ticket
        Ticket newTicket = new Ticket();
        newTicket.setCategory(selectedCategory);
        newTicket.setAssignedUser(selectedAssignee);
        newTicket.setDescription(description);
        newTicket.setStatus(selectedStatus);
        newTicket.setPriority(selectedPriority);
        newTicket.setTicketOwner(user);
        newTicket.setCreatedAt(new Date());
    
        // Persist ticket
        session.beginTransaction();
        session.save(newTicket);
        session.getTransaction().commit();
    
        System.out.println("New ticket created successfully!");
    }
    
    public static void updateTicket(User user, Scanner scanner) {
        // Display available tickets
        System.out.println("Available Tickets:");
        List<Ticket> tickets = Ticket.getAllTickets(session);
        for (Ticket ticket : tickets) {
            System.out.println(ticket.getId() + ". " + ticket.getDescription());
        }

        // Prompt user to select a ticket for update
        System.out.print("Enter the ID of the ticket you want to update: ");
        long ticketId = scanner.nextLong();
        scanner.nextLine(); // Consume the newline character

        // Find the selected ticket
        Ticket selectedTicket = session.find(Ticket.class, ticketId);

        if (selectedTicket == null) {
            System.out.println("Ticket not found with ID: " + ticketId);
            return;
        }

        // Display current details of the selected ticket
        System.out.println("Current Details:");
        System.out.println("Description: " + selectedTicket.getDescription());
        System.out.println("Priority: " + selectedTicket.getPriority().getPriorityName());
        System.out.println("Status: " + selectedTicket.getStatus().getStatusName());

        // Prompt user for updates
        System.out.print("Enter new description (or press Enter to keep current): ");
        String newDescription = scanner.nextLine();

        // Display available priorities
        List<Priority> priorities = Priority.getAllPriorities(session);
        System.out.println("Available Priorities:");
        for (Priority priority : priorities) {
            System.out.println(priority.getId() + ". " + priority.getPriorityName());
        }

        // Prompt user to select a new priority
        System.out.print("Enter the ID of the new priority (or press Enter to keep current): ");
        long newPriorityId;
        try {
            newPriorityId = scanner.nextLong();
        } catch (Exception e) {
            newPriorityId = selectedTicket.getPriority().getId();
        }
        scanner.nextLine(); // Consume the newline character

        // Find the new priority
        Priority newPriority = session.find(Priority.class, newPriorityId);
        if (newPriority == null) {
            System.out.println("Invalid priority ID. Keeping current priority.");
            newPriority = selectedTicket.getPriority();
        }

        // Display available statuses
        List<Status> statuses = Status.getAllStatusses(session);
        System.out.println("Available Statuses:");
        for (Status status : statuses) {
            System.out.println(status.getId() + ". " + status.getStatusName());
        }

        // Prompt user to select a new status
        System.out.print("Enter the ID of the new status (or press Enter to keep current): ");
        long newStatusId;
        try {
            newStatusId = scanner.nextLong();
        } catch (Exception e) {
            newStatusId = selectedTicket.getStatus().getId();
        }
        scanner.nextLine(); // Consume the newline character

        // Find the new status
        Status newStatus = session.find(Status.class, newStatusId);
        if (newStatus == null) {
            System.out.println("Invalid status ID. Keeping current status.");
            newStatus = selectedTicket.getStatus();
        }

        // Update the ticket with the new details
        session.beginTransaction();

        try {
            selectedTicket.setDescription(newDescription.isEmpty() ? selectedTicket.getDescription() : newDescription);
            selectedTicket.setPriority(newPriority);
            selectedTicket.setStatus(newStatus);

            session.update(selectedTicket);

            session.getTransaction().commit();

            // Notify observers (e.g., trigger notification logic in the Ticket class)
            selectedTicket.getTicketSubject().notifyObservers(selectedTicket);

            System.out.println("Ticket updated successfully!");
        } catch (Exception e) {
            session.getTransaction().rollback();
            e.printStackTrace();
            System.out.println("Error updating ticket. Please try again.");
        }
    }

    public static void filterTicket(User user, Scanner scanner) {
        System.out.println("1. By category");
        System.out.println("2. By Priority");
        System.out.println("3. Logout");

        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); 

        switch (choice) {
            case 1:
                System.out.print("Enter category to filter by: ");
                String categoryName = scanner.nextLine();
                Category category = session.createQuery("SELECT c FROM Category c WHERE c.categorieName = :name", Category.class)
                                            .setParameter("name", categoryName)
                                            .getSingleResult();
                List<Ticket> tickets = Ticket.getTicketsByCategory(session, category);
                // Print header
                System.out.println("ID \t Owner \t Assignee \t Description ");
                
                // Print each ticket with ID 
                for(Ticket c : tickets) {
                System.out.println(c.getId() + " \t " + c.getTicketOwner()+ " \t " + c.getAssignedUser() + " \t " + c.getDescription()); 
                }
                System.out.println();

                // Display the table menu
                tableMenu(user, scanner);
                break;
            case 2:
                System.out.print("Enter priority to filter by: ");
                String priorityName = scanner.nextLine();
                Priority priority = session.createQuery("SELECT p FROM Priority p WHERE p.priorityName = :name", Priority.class)
                                            .setParameter("name", priorityName)
                                            .getSingleResult();
                List<Ticket> ptickets = Ticket.getTicketsByPriority(session, priority);
                // Print header
                System.out.println("ID \t Owner \t Assignee \t Description ");
                
                // Print each ticket with ID 
                for(Ticket c : ptickets) {
                System.out.println(c.getId() + " \t " + c.getTicketOwner()+ " \t " + c.getAssignedUser() + " \t " + c.getDescription()); 
                }
                System.out.println();

                // Display the table menu
                tableMenu(user, scanner);
                break;
            case 3:
                System.out.println("Logout successful!");
                session.close();
                System.exit(0);
            default:
                System.out.println("Invalid choice. Please try again.");
        }
        
    }

    public static Long getTicketIdInput(Scanner scanner) {

        System.out.print("Enter category ID: ");
       
        // Read id from user input 
        Long ticketId = null;  
        while(ticketId == null) {
          String input = scanner.nextLine();
          try {
            ticketId = Long.parseLong(input);  
          } catch (NumberFormatException e) {
            System.out.print("Invalid ID, please enter a valid number: ");
          }
        }
    
        return ticketId;
    }

    public static String getTicketNameInput(Scanner scanner, Ticket ticket) {

        String name;
        
        do {
          System.out.print("Enter new name (max 30 chars): ");
          name = scanner.nextLine();
        } while(name.trim().isEmpty());
      
        System.out.print("Confirm changing "+ticket.getDescription()+" to "+name+"? (Y/N): ");
        String confirm = scanner.nextLine();
      
        if(!confirm.equalsIgnoreCase("Y")) {
          return ticket.getDescription(); // no change
        }
      
        return name;
      
    }


    //category
    public static void viewComments(User user, Scanner scanner){
        System.out.println("Comments");
  
        // Get all comments
        List<Comments> comments = Comments.getAllComments(session);
      
        // Print header
        System.out.println("ID \t Ticket \t User \t Description ");
        
        // Print each ticket with ID 
        for(Comments c : comments) {
          System.out.println(c.getId() + " \t " + c.getDescription()); 
        }
        System.out.println();

        // Display the table menu
        tableMenu(user, scanner);
    }
    
    public static void createComment(User user, Scanner scanner) {
        Long ticketId = getTicketIdInput(scanner);
        Ticket ticket = session.find(Ticket.class, ticketId);

        // Check if the ticket exists
        if (ticket == null) {
            System.out.println("Ticket not found.");
            return;
        }

        // Get comment text
        System.out.print("Enter comment text: ");
        String commentText = scanner.nextLine();

        // Create a new comment
        Comments comment = new Comments();
        comment.setUser(user);
        comment.setTicket(ticket);
        comment.setCreatedAt(new Date());
        comment.setDescription(commentText);

        // Add the comment to the ticket
        ticket.addComment(comment);

        // Begin transaction
        session.beginTransaction();

        try {
            // Persist the comment and update the ticket
            session.save(comment);
            session.update(ticket);

            // Commit the transaction
            session.getTransaction().commit();

            System.out.println("Comment added successfully!");
        } catch (Exception e) {
            // Rollback the transaction in case of any error
            session.getTransaction().rollback();
            e.printStackTrace();
            System.out.println("Error adding comment. Please try again.");
        }
    }

    //default data
    private static void createAndSaveRoles() {
        session.beginTransaction();
    
        try {
            Role role1 = new Role();
            role1.setRoleName("ADMIN");
            session.save(role1);
    
            Role role2 = new Role();
            role2.setRoleName("USER");
            session.save(role2);
    
            Role role3 = new Role();
            role3.setRoleName("DEVELOPER");
            session.save(role3);
    
            session.getTransaction().commit();
            System.out.println("Roles created and saved successfully!");
        } catch (ConstraintViolationException e) {
            // Catch exception for unique constraint violation
            session.getTransaction().rollback();
            System.out.println("Roles already exist. Skipping creation.");
        } catch (Exception e) {
            // Catch other exceptions
            session.getTransaction().rollback();
            e.printStackTrace();
            System.out.println("Error creating and saving roles. Please check the logs.");
        }
    }
    
    private static void createAndSaveStatus() {
        session.beginTransaction();
    
        try {
            Status status1 = new Status();
            status1.setStatusName("NEW");
            session.save(status1);
    
            Status status2 = new Status();
            status2.setStatusName("IN PROGRESS");
            session.save(status2);
    
            Status status3 = new Status();
            status3.setStatusName("PENDING");
            session.save(status3);

            Status status4 = new Status();
            status4.setStatusName("DONE");
            session.save(status4);

            Status status5 = new Status();
            status5.setStatusName("TESTING");
            session.save(status5);

            Status status6 = new Status();
            status6.setStatusName("AWAITING DEPLOYMENT");
            session.save(status6);

            Status status7 = new Status();
            status7.setStatusName("REOPEND");
            session.save(status7);
    
            session.getTransaction().commit();
            System.out.println("Statusses created and saved successfully!");
        } catch (ConstraintViolationException e) {
            // Catch exception for unique constraint violation
            session.getTransaction().rollback();
            System.out.println("Statusses already exist. Skipping creation.");
        } catch (Exception e) {
            // Catch other exceptions
            session.getTransaction().rollback();
            e.printStackTrace();
            System.out.println("Error creating and saving statusses. Please check the logs.");
        }
    }
    
    private static void createAndSavePriority() {
        session.beginTransaction();
    
        try {
            Priority priority1 = new Priority();
            priority1.setPriorityName("LOW");
            session.save(priority1);
    
            Priority priority2 = new Priority();
            priority2.setPriorityName("MEDIUM");
            session.save(priority2);
    
            Priority priority3 = new Priority();
            priority3.setPriorityName("HIGH");
            session.save(priority3);
    
            session.getTransaction().commit();
            System.out.println("Priorities created and saved successfully!");
        } catch (ConstraintViolationException e) {
            // Catch exception for unique constraint violation
            session.getTransaction().rollback();
            System.out.println("Priorities already exist. Skipping creation.");
        } catch (Exception e) {
            // Catch other exceptions
            session.getTransaction().rollback();
            e.printStackTrace();
            System.out.println("Error creating and saving priorities. Please check the logs.");
        }
    }
    

    private static void executeQueriesOnStartup(Session session) {
        // Your query execution logic here
        // Example:
        String jpql = "SELECT COUNT(t) FROM Ticket t WHERE t.status = :status";
        Long count = (Long) session.createQuery(jpql)
                .setParameter("status", getStatusByName("NEW"))
                .getSingleResult();
        System.out.println("Total open tickets: " + count);
    }
    
    private static Status getStatusByName(String statusName) {
        // Your logic to retrieve Status entity by name
        // Example:
        return (Status) session.createQuery("FROM Status WHERE status_name = :status_name")
                .setParameter("status_name", statusName)
                .getSingleResult();
    }

}