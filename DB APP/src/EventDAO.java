import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    private Connection connection;

    public EventDAO(Connection connection) {
        this.connection = connection;
    }

    public void addEvent(Event event) throws SQLException {
        String sql = "INSERT INTO events (eventname, eventtype, bookingfee) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, event.getEventName());
            statement.setString(2, event.getEventType());
            statement.setDouble(3, event.getBookingFee());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedID = generatedKeys.getInt(1);
                    event.setEventID(generatedID);
                }
            }
        }
    }

    public void updateEvent(Event event) throws SQLException {
        String sql = "UPDATE events SET eventname=?, eventtype=?, bookingfee=? WHERE eventID=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, event.getEventName());
            statement.setString(2, event.getEventType());
            statement.setDouble(3, event.getBookingFee());
            statement.setInt(4, event.getEventID());
            statement.executeUpdate();
        }
    }

    public void deleteEvent(int eventID) throws SQLException {
        String sql = "DELETE FROM events WHERE eventID=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, eventID);
            statement.executeUpdate();
        }
    }

    public Event viewEvent(int eventID) throws SQLException {
        String sql = "SELECT * FROM events WHERE eventID=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, eventID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Event(
                        resultSet.getInt("eventID"),
                        resultSet.getString("eventname"),
                        resultSet.getString("eventtype"),
                        resultSet.getDouble("bookingfee")
                );
            }
        }

        return null;
    }

    public List<Event> viewAllEvents() throws SQLException {
        List<Event> list = new ArrayList<>();
        String sql = "SELECT * FROM events";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Event event = new Event(
                        resultSet.getInt("eventID"),
                        resultSet.getString("eventname"),
                        resultSet.getString("eventtype"),
                        resultSet.getDouble("bookingfee")
                );

                list.add(event);
            }
        }

        return list;
    }

    public void viewEventWithSchedules(int eventID) throws SQLException {
        Event event = viewEvent(eventID);

        if (event == null) {
            System.out.println("Event not found with ID: " + eventID);
            return;
        }

        System.out.println("\n=== EVENT DETAILS ===");
        System.out.println(event);
        System.out.println();

        String query = """
                SELECT s.scheduleID, s.scheduleDate, s.startTime, s.endTime, 
                       s.status, sec.sectionname, sec.price, sec.availableslots
                FROM schedules s
                JOIN section sec ON s.sectionID = sec.sectionID
                WHERE s.eventID = ?
                ORDER BY s.scheduleDate, s.startTime
                """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, eventID);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("=== AVAILABLE SCHEDULES ===");
            boolean hasSchedules = false;

            while (resultSet.next()) {
                hasSchedules = true;
                int scheduleID = resultSet.getInt("scheduleID");
                Date scheduleDate = resultSet.getDate("scheduleDate");
                Time startTime = resultSet.getTime("startTime");
                Time endTime = resultSet.getTime("endTime");
                String status = resultSet.getString("status");
                String sectionName = resultSet.getString("sectionname");
                double sectionPrice = resultSet.getDouble("price");
                int availableSlots = resultSet.getInt("availableslots");

                String statusText = status.equals("A") ? "Available" : "Full";

                System.out.printf("Schedule ID: %d | Date: %s | Time: %s - %s%n",
                        scheduleID, scheduleDate, startTime, endTime);
                System.out.printf("  Section: %s | Price: %.2f | Status: %s | Available Slots: %d%n",
                        sectionName, sectionPrice, statusText, availableSlots);
                System.out.println();
            }

            if (!hasSchedules) {
                System.out.println("No schedules found for this event.");
            }
        }
    }
}