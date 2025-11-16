package app;

import model.*;
import dao.*;
import utility.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import gui.MainMenuScene;

public class MainDriver extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            Connection connection = utility.DBConnection.getConnection();
            MainMenuScene mainMenu = new MainMenuScene(connection);

            primaryStage.setScene(new Scene(mainMenu.getRoot(), 1000, 600));
            primaryStage.setTitle("DB APP");
            primaryStage.show();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database connection failed!");
            alert.showAndWait();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
//        try (Connection connection = DBConnection.getConnection();
//             Scanner scanner = new Scanner(System.in)) {
//
//            System.out.println("Connected to database successfully\n");
//
//            EventDAO eventDAO = new EventDAO(connection);
//            CustomerDAO customerDAO = new CustomerDAO(connection);
//            MerchTransactionDAO merchTransactionDAO = new MerchTransactionDAO(connection);
//            ReportDAO reportDAO = new ReportDAO(connection);
//
//            while (true) {
//                System.out.println("\n╔════════════════════════════════════════════════════════════╗");
//                System.out.println("║           EVENT MANAGEMENT SYSTEM - MAIN MENU              ║");
//                System.out.println("╠════════════════════════════════════════════════════════════╣");
//                System.out.println("║  1. View All Events                                        ║");
//                System.out.println("║  2. View model.Event model.Merchandise                     ║");
//                System.out.println("║  3. Purchase model.Merchandise                             ║");
//                System.out.println("║  4. View model.Customer Purchase History                   ║");
//                System.out.println("║  5. View All Customers                                     ║");
//                System.out.println("║  6. Generate model.Event & model.Schedule Report           ║");
//                System.out.println("║  7. Exit                                                   ║");
//                System.out.println("╚════════════════════════════════════════════════════════════╝");
//                System.out.print("Choose an option: ");
//
//                int choice = scanner.nextInt();
//                scanner.nextLine();
//
//                switch (choice) {
//                    case 1:
//                        viewAllEvents(eventDAO);
//                        break;
//
//                    case 2:
//                        viewEventMerchandise(eventDAO, merchTransactionDAO, scanner);
//                        break;
//
//                    case 3:
//                        purchaseMerchandise(eventDAO, customerDAO, merchTransactionDAO, scanner);
//                        break;
//
//                    case 4:
//                        viewPurchaseHistory(merchTransactionDAO, scanner);
//                        break;
//
//                    case 5:
//                        viewAllCustomers(customerDAO);
//                        break;
//
//                    case 6:
//                        generateReports(reportDAO, scanner);
//                        break;
//
//                    case 7:
//                        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
//                        System.out.println("║          Thank you for using the system! Goodbye!          ║");
//                        System.out.println("╚════════════════════════════════════════════════════════════╝");
//                        return;
//
//                    default:
//                        System.out.println("Invalid choice. Please try again.");
//                }
//            }
//
//        } catch (SQLException e) {
//            System.err.println("Database error occurred:");
//            e.printStackTrace();
//        }
    }

//    private static void viewAllEvents(EventDAO eventDAO) throws SQLException {
//        System.out.println("\n=== ALL EVENTS ===");
//        List<Event> events = eventDAO.viewAllEvents();
//        for (Event event : events) {
//            System.out.println(event);
//        }
//    }
//
//    private static void viewEventMerchandise(EventDAO eventDAO, MerchTransactionDAO merchTransactionDAO,
//                                             Scanner scanner) throws SQLException {
//        viewAllEvents(eventDAO);
//        System.out.print("\nEnter model.Event ID to view merchandise: ");
//        int eventID = scanner.nextInt();
//        scanner.nextLine();
//
//        Event event = eventDAO.viewEvent(eventID);
//        if (event == null) {
//            System.out.println("model.Event not found!");
//            return;
//        }
//
//        System.out.println("\nmodel.Event: " + event.getEventName());
//        merchTransactionDAO.getEventMerchandise(eventID);
//    }
//
//    private static void purchaseMerchandise(EventDAO eventDAO, CustomerDAO customerDAO,
//                                            MerchTransactionDAO merchTransactionDAO, Scanner scanner)
//            throws SQLException {
//        viewAllEvents(eventDAO);
//        System.out.print("\nEnter model.Event ID: ");
//        int eventID = scanner.nextInt();
//        scanner.nextLine();
//
//        Event event = eventDAO.viewEvent(eventID);
//        if (event == null) {
//            System.out.println("model.Event not found!");
//            return;
//        }
//
//        System.out.println("\nmodel.Event: " + event.getEventName());
//        List<Merchandise> eventMerch = merchTransactionDAO.getEventMerchandise(eventID);
//        if (eventMerch.isEmpty()) {
//            System.out.println("No merchandise available for this event.");
//            return;
//        }
//
//        System.out.println("\n=== SELECT CUSTOMER ===");
//        List<Customer> customers = customerDAO.viewAllCustomers();
//        for (Customer customer : customers) {
//            System.out.println(customer);
//        }
//
//        System.out.print("\nEnter model.Customer ID: ");
//        int customerID = scanner.nextInt();
//        scanner.nextLine();
//
//        Customer customer = customerDAO.viewCustomer(customerID);
//        if (customer == null) {
//            System.out.println("model.Customer not found!");
//            return;
//        }
//
//        System.out.println("\nSelected model.Customer: " + customer);
//
//        System.out.print("\nEnter model.Merchandise ID to purchase: ");
//        int merchandiseID = scanner.nextInt();
//        scanner.nextLine();
//
//        System.out.print("Enter Quantity: ");
//        int quantity = scanner.nextInt();
//        scanner.nextLine();
//
//        System.out.println("\nProcessing transaction...");
//        boolean success = merchTransactionDAO.processMerchTransaction(customerID, eventID, merchandiseID, quantity);
//
//        if (!success) {
//            System.out.println("\nTransaction failed. Please try again.");
//        }
//    }
//
//    private static void viewPurchaseHistory(MerchTransactionDAO merchTransactionDAO, Scanner scanner)
//            throws SQLException {
//        System.out.print("\nEnter model.Customer ID: ");
//        int customerID = scanner.nextInt();
//        scanner.nextLine();
//
//        merchTransactionDAO.viewCustomerReceipts(customerID);
//    }
//
//    private static void viewAllCustomers(CustomerDAO customerDAO) throws SQLException {
//        System.out.println("\n=== ALL CUSTOMERS ===");
//        List<Customer> customers = customerDAO.viewAllCustomers();
//        for (Customer customer : customers) {
//            System.out.println(customer);
//        }
//    }
//
//    private static void generateReports(ReportDAO reportDAO, Scanner scanner) throws SQLException {
//        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
//        System.out.println("║           EVENT & SCHEDULE REPORT GENERATOR                ║");
//        System.out.println("╠════════════════════════════════════════════════════════════╣");
//        System.out.println("║  1. Monthly Report                                         ║");
//        System.out.println("║  2. Yearly Report                                          ║");
//        System.out.println("║  3. View Available Periods                                 ║");
//        System.out.println("║  4. Back to Main Menu                                      ║");
//        System.out.println("╚════════════════════════════════════════════════════════════╝");
//        System.out.print("Choose an option: ");
//
//        int choice = scanner.nextInt();
//        scanner.nextLine();
//
//        switch (choice) {
//            case 1:
//                System.out.print("\nEnter Month (1-12): ");
//                int month = scanner.nextInt();
//                System.out.print("Enter Year: ");
//                int year = scanner.nextInt();
//                scanner.nextLine();
//                reportDAO.displayMonthlyReport(month, year);
//                break;
//
//            case 2:
//                System.out.print("\nEnter Year: ");
//                int yearInput = scanner.nextInt();
//                scanner.nextLine();
//                reportDAO.displayYearlyReport(yearInput);
//                break;
//
//            case 3:
//                System.out.println("\n=== AVAILABLE MONTHS WITH DATA ===");
//                List<String> months = reportDAO.getAvailableMonths();
//                if (months.isEmpty()) {
//                    System.out.println("No schedule data available.");
//                } else {
//                    for (String m : months) {
//                        System.out.println("- " + m);
//                    }
//                }
//
//                System.out.println("\n=== AVAILABLE YEARS WITH DATA ===");
//                List<Integer> years = reportDAO.getAvailableYears();
//                if (years.isEmpty()) {
//                    System.out.println("No schedule data available.");
//                } else {
//                    for (Integer y : years) {
//                        System.out.println("- " + y);
//                    }
//                }
//                break;
//
//            case 4:
//                return;
//
//            default:
//                System.out.println("Invalid choice.");
//        }
//    }
}