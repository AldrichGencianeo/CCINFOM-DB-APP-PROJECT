import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class MainDriver {
    public static void main(String[] args) {
        try (Connection connection = DBConnection.getConnection()) {
            System.out.println("Connected to database successfully\n");

            Scanner scanner = new Scanner(System.in);

            System.out.println("=== DATABASE MANAGEMENT SYSTEM ===");
            System.out.println("1. View Merchandise with Related Events");
            System.out.println("2. View Event with Available Schedules");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            System.out.println();

            if (choice == 1) {
                MerchandiseDAO merchandiseDAO = new MerchandiseDAO(connection);

                System.out.println("=== ALL MERCHANDISE ===");
                List<Merchandise> merchandiseList = merchandiseDAO.viewAllMerchandise();
                for (Merchandise merch : merchandiseList) {
                    System.out.println(merch);
                }
                System.out.println();

                System.out.print("Enter Merchandise ID to view related events: ");
                int merchandiseID = scanner.nextInt();

                Merchandise merchandise = merchandiseDAO.viewMerchandise(merchandiseID);
                if (merchandise != null) {
                    System.out.println("\n=== MERCHANDISE DETAILS ===");
                    System.out.println(merchandise);
                    System.out.println();

                    merchandiseDAO.viewRelatedEvents(merchandiseID);
                } else {
                    System.out.println("Merchandise not found with ID: " + merchandiseID);
                }

            } else if (choice == 2) {
                EventDAO eventDAO = new EventDAO(connection);

                System.out.println("=== ALL EVENTS ===");
                List<Event> events = eventDAO.viewAllEvents();
                for (Event event : events) {
                    System.out.println(event);
                }
                System.out.println();

                System.out.print("Enter Event ID to view details with schedules: ");
                int eventID = scanner.nextInt();

                eventDAO.viewEventWithSchedules(eventID);

            } else {
                System.out.println("Invalid choice!");
            }

            scanner.close();

        } catch (SQLException e) {
            System.err.println("Database error occurred:");
            e.printStackTrace();
        }
    }
}