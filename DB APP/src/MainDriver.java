import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class MainDriver {
    public static void main(String[] args) {
        try (Connection connection = DBConnection.getConnection()) {
            System.out.println("Connected to database successfully\n");

            EventDAO eventDAO = new EventDAO(connection);
            Scanner scanner = new Scanner(System.in);

            System.out.println("=== ALL EVENTS ===");
            List<Event> events = eventDAO.viewAllEvents();
            for (Event event : events) {
                System.out.println(event);
            }
            System.out.println();

            System.out.print("Enter Event ID to view details with schedules: ");
            int eventID = scanner.nextInt();

            eventDAO.viewEventWithSchedules(eventID);

            scanner.close();

        } catch (SQLException e) {
            System.err.println("Database error occurred:");
            e.printStackTrace();
        }
    }
}