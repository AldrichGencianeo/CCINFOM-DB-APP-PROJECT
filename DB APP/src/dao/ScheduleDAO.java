package dao;

import model.Schedule;
import model.ScheduleSection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {
    private Connection connection;

    public ScheduleDAO(Connection connection) {
        this.connection = connection;
    }

    public void addScheduleWithPrices(Schedule schedule, List<ScheduleSection> prices) throws SQLException {
        String scheduleSql = "INSERT INTO schedules (eventID, scheduleDate, startTime, endTime) VALUES (?, ?, ?, ?)";
        String sectionSql = "INSERT INTO schedule_section (scheduleID, sectionID, availableSlots, price) VALUES (?, ?, ?, ?)";

        PreparedStatement scheduleStmt = null;
        PreparedStatement sectionStmt = null;
        ResultSet generatedKeys = null;

        try {
            connection.setAutoCommit(false);

            scheduleStmt = connection.prepareStatement(scheduleSql, Statement.RETURN_GENERATED_KEYS);
            scheduleStmt.setInt(1, schedule.getEventID());
            scheduleStmt.setDate(2, schedule.getScheduleDate());
            scheduleStmt.setTime(3, schedule.getStartTime());
            scheduleStmt.setTime(4, schedule.getEndTime());
            scheduleStmt.executeUpdate();

            generatedKeys = scheduleStmt.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new SQLException("Creating schedule failed, no ID obtained.");
            }
            int newScheduleID = generatedKeys.getInt(1);
            schedule.setScheduleID(newScheduleID);

            sectionStmt = connection.prepareStatement(sectionSql);
            for (ScheduleSection ss : prices) {
                sectionStmt.setInt(1, newScheduleID);
                sectionStmt.setInt(2, ss.getSectionID());
                sectionStmt.setInt(3, ss.getAvailableSlots());
                sectionStmt.setDouble(4, ss.getPrice());
                sectionStmt.addBatch();
            }
            sectionStmt.executeBatch();

            connection.commit();
            System.out.println("New schedule (ID: " + newScheduleID + ") and its prices were added successfully.");

        } catch (SQLException e) {
            System.err.println("Transaction failed! Rolling back changes.");

            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (generatedKeys != null) generatedKeys.close();
            if (scheduleStmt != null) scheduleStmt.close();
            if (sectionStmt != null) sectionStmt.close();
            connection.setAutoCommit(true);
        }
    }

    public void updateSchedule(Schedule schedule) throws SQLException {
        String sql = "UPDATE schedules SET eventID=?, scheduleDate=?, startTime=?, endTime=? WHERE scheduleID=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, schedule.getEventID());
            statement.setDate(2, schedule.getScheduleDate());
            statement.setTime(3, schedule.getStartTime());
            statement.setTime(4, schedule.getEndTime());
            statement.setInt(5, schedule.getScheduleID());
            statement.executeUpdate();
        }
    }

    public void deleteSchedule(int scheduleID) throws SQLException {
        String sql = "DELETE FROM schedules WHERE scheduleID=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, scheduleID);
            statement.executeUpdate();
        }
    }

    public Schedule viewSchedule(int scheduleID) throws SQLException {
        String sql = "SELECT * FROM schedules WHERE scheduleID=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, scheduleID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Schedule(
                            resultSet.getInt("scheduleID"),
                            resultSet.getInt("eventID"),
                            resultSet.getDate("scheduleDate"),
                            resultSet.getTime("startTime"),
                            resultSet.getTime("endTime")
                    );
                }
            }
        }
        return null; // Not found
    }

    public List<Schedule> viewAllSchedules() throws SQLException {
        List<Schedule> list = new ArrayList<>();
        String sql = "SELECT * FROM schedules ORDER BY scheduleDate";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Schedule schedule = new Schedule(
                        resultSet.getInt("scheduleID"),
                        resultSet.getInt("eventID"),
                        resultSet.getDate("scheduleDate"),
                        resultSet.getTime("startTime"),
                        resultSet.getTime("endTime")
                );
                list.add(schedule);
            }
        }
        return list;
    }

    public int getAvailableSlots(int scheduleID, int sectionID) throws SQLException {
        String sql = """
            SELECT ss.availableSlots - COUNT(t.ticketID) AS remainingSlots
            FROM schedule_section ss
            LEFT JOIN tickets t 
                ON t.scheduleID = ss.scheduleID
                AND t.sectionID = ss.sectionID
                AND t.status != 'CA'
            WHERE ss.scheduleID = ? AND ss.sectionID = ?
            GROUP BY ss.availableSlots
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, scheduleID);
            stmt.setInt(2, sectionID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("remainingSlots");
                }
            }
        }

        return 0;
    }
}