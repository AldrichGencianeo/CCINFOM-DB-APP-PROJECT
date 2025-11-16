package dao;

import model.MerchReceipt;
import model.Merchandise;
import report.EventScheduleReport;
import report.MerchSalesReport;
import repository.MerchReceiptRepo;
import repository.MerchandiseRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportDAO {
    private Connection connection;
    private final MerchReceiptRepo merchReceiptRepo;
    private final MerchandiseRepo merchandiseRepo;

    public ReportDAO(Connection connection, MerchReceiptRepo merchReceiptRepo, MerchandiseRepo merchandiseRepo) {
        this.connection = connection;
        this.merchReceiptRepo = merchReceiptRepo;
        this.merchandiseRepo = merchandiseRepo;
    }

    public EventScheduleReport generateMonthlyReport(int month, int year) throws SQLException {
        String sql = """
                SELECT
                    COUNT(DISTINCT e.eventID) as totalEvents,
                    AVG(e.bookingfee) as avgBookingFee,
                    COUNT(s.scheduleID) as totalSchedules
                FROM events e
                LEFT JOIN schedules s ON e.eventID = s.eventID
                WHERE MONTH(s.scheduleDate) = ? AND YEAR(s.scheduleDate) = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, month);
            statement.setInt(2, year);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int totalEvents = resultSet.getInt("totalEvents");
                double avgBookingFee = resultSet.getDouble("avgBookingFee");
                int totalSchedules = resultSet.getInt("totalSchedules");
                double avgSchedulesPerEvent = totalEvents > 0 ? (double) totalSchedules / totalEvents : 0;

                String monthName = getMonthName(month);
                String timePeriod = monthName + " " + year;

                return new EventScheduleReport(totalEvents, avgBookingFee, totalSchedules,
                        avgSchedulesPerEvent, timePeriod, month, year);
            }
        }
        return null;
    }

    public EventScheduleReport generateYearlyReport(int year) throws SQLException {
        String sql = """
                SELECT
                    COUNT(DISTINCT e.eventID) as totalEvents,
                    AVG(e.bookingfee) as avgBookingFee,
                    COUNT(s.scheduleID) as totalSchedules
                FROM events e
                LEFT JOIN schedules s ON e.eventID = s.eventID
                WHERE YEAR(s.scheduleDate) = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, year);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int totalEvents = resultSet.getInt("totalEvents");
                double avgBookingFee = resultSet.getDouble("avgBookingFee");
                int totalSchedules = resultSet.getInt("totalSchedules");
                double avgSchedulesPerEvent = totalEvents > 0 ? (double) totalSchedules / totalEvents : 0;

                String timePeriod = "Year " + year;

                return new EventScheduleReport(totalEvents, avgBookingFee, totalSchedules,
                        avgSchedulesPerEvent, timePeriod, 0, year);
            }
        }
        return null;
    }

    private String getMonthName(int month) {
        String[] months = {"", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        return months[month];
    }

    public void displayMonthlyReport(int month, int year) throws SQLException {
        EventScheduleReport report = generateMonthlyReport(month, year);

        if (report == null || report.getTotalEvents() == 0) {
            System.out.println("\n=== NO DATA FOUND FOR " + getMonthName(month) + " " + year + " ===");
            return;
        }

        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         EVENT & SCHEDULE REPORT - MONTHLY                  ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Period: %-50s ║%n", report.getTimePeriod());
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Total Number of Events:          %-25d ║%n", report.getTotalEvents());
        System.out.printf("║ Average Booking Fee:             ₱%-24.2f ║%n", report.getAverageBookingFee());
        System.out.printf("║ Total Number of Schedules:       %-25d ║%n", report.getTotalSchedules());
        System.out.printf("║ Average Schedules per model.Event:     %-25.2f ║%n", report.getAverageSchedulesPerEvent());
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }

    public void displayYearlyReport(int year) throws SQLException {
        EventScheduleReport report = generateYearlyReport(year);

        if (report == null || report.getTotalEvents() == 0) {
            System.out.println("\n=== NO DATA FOUND FOR YEAR " + year + " ===");
            return;
        }

        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         EVENT & SCHEDULE REPORT - YEARLY                   ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Period: %-50s ║%n", report.getTimePeriod());
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Total Number of Events:          %-25d ║%n", report.getTotalEvents());
        System.out.printf("║ Average Booking Fee:             ₱%-24.2f ║%n", report.getAverageBookingFee());
        System.out.printf("║ Total Number of Schedules:       %-25d ║%n", report.getTotalSchedules());
        System.out.printf("║ Average Schedules per model.Event:     %-25.2f ║%n", report.getAverageSchedulesPerEvent());
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }

    public List<String> getAvailableMonths() throws SQLException {
        List<String> months = new ArrayList<>();
        String sql = """
                SELECT DISTINCT MONTH(scheduleDate) as month, YEAR(scheduleDate) as year
                FROM schedules
                ORDER BY year DESC, month DESC
                """;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int month = resultSet.getInt("month");
                int year = resultSet.getInt("year");
                months.add(getMonthName(month) + " " + year + " (Month: " + month + ", Year: " + year + ")");
            }
        }
        return months;
    }

    public List<Integer> getAvailableYears() throws SQLException {
        List<Integer> years = new ArrayList<>();
        String sql = """
                SELECT DISTINCT YEAR(scheduleDate) as year
                FROM schedules
                ORDER BY year DESC
                """;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                years.add(resultSet.getInt("year"));
            }
        }
        return years;
    }

    public List<MerchSalesReport> generateReportPerEvent(int eventID) {
        List<MerchReceipt> receipts = merchReceiptRepo.findByEventID(eventID);
        Map<Integer, Integer> soldCount = new HashMap<>();
        Map<Integer, Double> revenueMap = new HashMap<>();

        for (MerchReceipt r : receipts) {
            int merchandiseID = r.getMerchandiseID();

            soldCount.put(merchandiseID, soldCount.getOrDefault(merchandiseID, 0) + r.getQuantity());
            revenueMap.put(merchandiseID, revenueMap.getOrDefault(merchandiseID, 0.0) + r.getTotalPrice());
        }

        List<MerchSalesReport> report = new ArrayList<>();

        for (int merchandiseID : soldCount.keySet()) {
            Merchandise m = merchandiseRepo.findByID(merchandiseID);

            report.add(new MerchSalesReport(merchandiseID, m.getMerchandiseName(), soldCount.get(merchandiseID), revenueMap.get(merchandiseID), m.getStock()));
        }

        return report;
    }

    public List<MerchSalesReport> generateReportPerMonth(int year, int month) {
        List<MerchReceipt> receipts = merchReceiptRepo.findByMonth(year, month);
        Map<Integer, Integer> soldCount = new HashMap<>();
        Map<Integer, Double> revenueMap = new HashMap<>();

        for (MerchReceipt r : receipts) {
            int merchandiseID = r.getMerchandiseID();

            soldCount.put(merchandiseID, soldCount.getOrDefault(merchandiseID, 0) + r.getQuantity());
            revenueMap.put(merchandiseID, revenueMap.getOrDefault(merchandiseID, 0.0) + r.getTotalPrice());
        }

        List<MerchSalesReport> report = new ArrayList<>();

        for (int merchandiseID : soldCount.keySet()) {
            Merchandise m = merchandiseRepo.findByID(merchandiseID);

            report.add(new MerchSalesReport(merchandiseID, m.getMerchandiseName(), soldCount.get(merchandiseID), revenueMap.get(merchandiseID), m.getStock()));
        }

        return report;
    }
}