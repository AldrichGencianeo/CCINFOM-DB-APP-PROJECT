import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class MainDriver {
    public static void main(String[] args) {
        try (Connection connection = DBConnection.getConnection();
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to database successfully\n");

            EventDAO eventDAO = new EventDAO(connection);
            CustomerDAO customerDAO = new CustomerDAO(connection);
            MerchTransactionDAO merchTransactionDAO = new MerchTransactionDAO(connection);

            while (true) {
                System.out.println("\n=== MERCHANDISE TRANSACTION MANAGEMENT SYSTEM ===");
                System.out.println("1. View All Events");
                System.out.println("2. View Event Merchandise");
                System.out.println("3. Purchase Merchandise");
                System.out.println("4. View Customer Purchase History");
                System.out.println("5. View All Customers");
                System.out.println("6. Exit");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        viewAllEvents(eventDAO);
                        break;

                    case 2:
                        viewEventMerchandise(eventDAO, merchTransactionDAO, scanner);
                        break;

                    case 3:
                        purchaseMerchandise(eventDAO, customerDAO, merchTransactionDAO, scanner);
                        break;

                    case 4:
                        viewPurchaseHistory(merchTransactionDAO, scanner);
                        break;

                    case 5:
                        viewAllCustomers(customerDAO);
                        break;

                    case 6:
                        System.out.println("Exiting system. Goodbye!");
                        return;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Database error occurred:");
            e.printStackTrace();
        }
    }

    private static void viewAllEvents(EventDAO eventDAO) throws SQLException {
        System.out.println("\n=== ALL EVENTS ===");
        List<Event> events = eventDAO.viewAllEvents();
        for (Event event : events) {
            System.out.println(event);
        }
    }

    private static void viewEventMerchandise(EventDAO eventDAO, MerchTransactionDAO merchTransactionDAO,
                                             Scanner scanner) throws SQLException {
        viewAllEvents(eventDAO);
        System.out.print("\nEnter Event ID to view merchandise: ");
        int eventID = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Event event = eventDAO.viewEvent(eventID);
        if (event == null) {
            System.out.println("Event not found!");
            return;
        }

        System.out.println("\nEvent: " + event.getEventName());
        merchTransactionDAO.getEventMerchandise(eventID);
    }

    private static void purchaseMerchandise(EventDAO eventDAO, CustomerDAO customerDAO,
                                            MerchTransactionDAO merchTransactionDAO, Scanner scanner)
            throws SQLException {
        // Step 1: Select Event
        viewAllEvents(eventDAO);
        System.out.print("\nEnter Event ID: ");
        int eventID = scanner.nextInt();
        scanner.nextLine();

        Event event = eventDAO.viewEvent(eventID);
        if (event == null) {
            System.out.println("Event not found!");
            return;
        }

        // Step 2: View merchandise for the event
        System.out.println("\nEvent: " + event.getEventName());
        List<Merchandise> eventMerch = merchTransactionDAO.getEventMerchandise(eventID);
        if (eventMerch.isEmpty()) {
            System.out.println("No merchandise available for this event.");
            return;
        }

        // Step 3: Select Customer
        System.out.println("\n=== SELECT CUSTOMER ===");
        List<Customer> customers = customerDAO.viewAllCustomers();
        for (Customer customer : customers) {
            System.out.println(customer);
        }

        System.out.print("\nEnter Customer ID: ");
        int customerID = scanner.nextInt();
        scanner.nextLine();

        Customer customer = customerDAO.viewCustomer(customerID);
        if (customer == null) {
            System.out.println("Customer not found!");
            return;
        }

        System.out.println("\nSelected Customer: " + customer);

        // Step 4: Select Merchandise
        System.out.print("\nEnter Merchandise ID to purchase: ");
        int merchandiseID = scanner.nextInt();
        scanner.nextLine();

        // Step 5: Enter Quantity
        System.out.print("Enter Quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        // Step 6: Process Transaction
        System.out.println("\nProcessing transaction...");
        boolean success = merchTransactionDAO.processMerchTransaction(customerID, eventID, merchandiseID, quantity);

        if (!success) {
            System.out.println("\nTransaction failed. Please try again.");
        }
    }

    private static void viewPurchaseHistory(MerchTransactionDAO merchTransactionDAO, Scanner scanner)
            throws SQLException {
        System.out.print("\nEnter Customer ID: ");
        int customerID = scanner.nextInt();
        scanner.nextLine();

        merchTransactionDAO.viewCustomerReceipts(customerID);
    }

    private static void viewAllCustomers(CustomerDAO customerDAO) throws SQLException {
        System.out.println("\n=== ALL CUSTOMERS ===");
        List<Customer> customers = customerDAO.viewAllCustomers();
        for (Customer customer : customers) {
            System.out.println(customer);
        }
    }
}