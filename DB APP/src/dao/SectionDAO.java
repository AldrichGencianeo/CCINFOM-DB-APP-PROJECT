package dao;

import model.Section;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionDAO {
    private Connection connection;

    public SectionDAO(Connection connection) {
        this.connection = connection;
    }

    public void addSection(Section section) throws SQLException {
        String sql = "INSERT INTO section (sectionname, capacity) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, section.getSectionName());
            statement.setInt(2, section.getCapacity());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedID = generatedKeys.getInt(1);
                    section.setSectionID(generatedID);
                }
            }
        }
    }

    public void updateSection(Section section) throws SQLException {
        String sql = "UPDATE section SET sectionname=?, capacity=? WHERE sectionID=?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, section.getSectionName());
            statement.setInt(2, section.getCapacity());
            statement.setInt(3, section.getSectionID());
            statement.executeUpdate();
        }
    }

    public void deleteSection(int sectionID) throws SQLException {
        String sql = "DELETE FROM section WHERE sectionID=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, sectionID);
            statement.executeUpdate();
        }
    }

    public Section viewSection(int sectionID) throws SQLException {
        String sql = "SELECT * FROM section WHERE sectionID=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, sectionID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Section(
                            resultSet.getInt("sectionID"),
                            resultSet.getString("sectionname"),
                            resultSet.getInt("capacity")
                    );
                }
            }
        }
        return null;
    }

    public List<Section> viewAllSections() throws SQLException {
        List<Section> list = new ArrayList<>();
        String sql = "SELECT * FROM section";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Section section = new Section(
                        resultSet.getInt("sectionID"),
                        resultSet.getString("sectionname"),
                        resultSet.getInt("capacity")
                );
                list.add(section);
            }
        }
        return list;
    }

    public void viewSectionDetails(int sectionID) throws SQLException {
        Section section = viewSection(sectionID);

        if (section == null) {
            System.out.println("model.Section not found with ID: " + sectionID);
            return;
        }

        System.out.println("\n=== SECTION DETAILS ===");
        System.out.println(section.toString());
        System.out.println();

        String sql = """
                     SELECT 
                       e.eventname,
                       sch.scheduleID,
                       ss.price,
                       (SELECT COUNT(t.ticketID)
                        FROM Tickets t
                        WHERE t.scheduleID = sch.scheduleID 
                          AND t.sectionID = s.sectionID 
                          AND t.status != 'CA') AS tickets_sold
                     FROM model.Section s
                     JOIN Schedule_Section ss ON s.sectionID = ss.sectionID
                     JOIN Schedules sch ON ss.scheduleID = ss.scheduleID
                     JOIN Events e ON sch.eventID = e.eventID
                     WHERE s.sectionID = ?
                     ORDER BY sch.scheduleDate, e.eventname;
                     """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, sectionID);

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("=== SCHEDULED EVENTS USING THIS SECTION ===");
                boolean hasSchedules = false;

                while (resultSet.next()) {
                    hasSchedules = true;
                    
                    String eventName = resultSet.getString("eventname");
                    int scheduleID = resultSet.getInt("scheduleID");
                    double price = resultSet.getDouble("price");
                    int ticketsSold = resultSet.getInt("tickets_sold");
                    
                    int capacity = section.getCapacity(); 
                    
                    int availableSlots = capacity - ticketsSold; 

                    System.out.printf("  model.Event: %s (ScheduleID: %d)%n", eventName, scheduleID);
                    System.out.printf("    Price: %.2f%n", price);
                    System.out.printf("    Capacity: %d%n", capacity);
                    System.out.printf("    Sold: %d%n", ticketsSold);
                    System.out.printf("    Available: %d%n%n", availableSlots);
                }

                if (!hasSchedules) {
                    System.out.println("No schedules found for this section.");
                }
            }
        }
    }
}