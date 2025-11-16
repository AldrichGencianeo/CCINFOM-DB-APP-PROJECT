import java.sql.*;
import java.time.LocalDateTime;

public class BookTicketDAO {
    private final Connection connection;

    public BookTicketDAO(Connection connection) {
        this.connection = connection;
    }

    public int bookTicket(int customerID, int scheduleID, int sectionID) throws SQLException {
        String selectAvailSlotsSQL = "SELECT availableSlots FROM schedule_section WHERE scheduleID = ? AND sectionID = ? FOR UPDATE";
        String updateAvailSlotsSQL = "UPDATE schedule_section SET availableSlots = availableSlots - 1 WHERE scheduleID = ? AND sectionID = ?";
        String getPriceSQL = "SELECT price FROM section WHERE sectionID = ?";
        String insertTicketSQL = "INSERT INTO tickets (customerID, scheduleID, sectionID, purchaseDate, ticketPrice, status) VALUES (?, ?, ?, ?, ?, 'P')";

        boolean oldAutoCommit = connection.getAutoCommit();

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement psSelect = connection.prepareStatement(selectAvailSlotsSQL)) {
                psSelect.setInt(1, scheduleID);
                psSelect.setInt(2, sectionID);

                try (ResultSet resultSet = psSelect.executeQuery()) {
                    if (!resultSet.next()) {
                        connection.rollback();

                        return 1;
                    }

                    int availableSlots = resultSet.getInt("availableSlots");

                    if (availableSlots <= 0) {
                        connection.rollback();

                        return -1;
                    }
                }
            }

            double ticketPrice;

            try (PreparedStatement psPrice = connection.prepareStatement(getPriceSQL)) {
                psPrice.setInt(1, sectionID);

                try (ResultSet resultSet = psPrice.executeQuery()) {
                    if (!resultSet.next()) {
                        connection.rollback();

                        return -1;
                    }

                    ticketPrice = resultSet.getDouble("price");
                }
            }

            try (PreparedStatement psUpdate = connection.prepareStatement(updateAvailSlotsSQL)) {
                psUpdate.setInt(1, scheduleID);
                psUpdate.setInt(2, sectionID);
                int updated = psUpdate.executeUpdate();

                if (updated != 1) {
                    connection.rollback();

                    return -1;
                }
            }

            int generatedTicketID = -1;

            try (PreparedStatement psInsert = connection.prepareStatement(insertTicketSQL, Statement.RETURN_GENERATED_KEYS)) {
                psInsert.setInt(1, customerID);
                psInsert.setInt(2, scheduleID);
                psInsert.setInt(3, sectionID);
                psInsert.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                psInsert.setDouble(5, ticketPrice);
                psInsert.executeUpdate();

                try (ResultSet keys = psInsert.getGeneratedKeys()) {
                    if (keys.next()) {
                        generatedTicketID = keys.getInt(1);
                    }
                }
            }

            connection.commit();

            return generatedTicketID;
        } catch (SQLException exception) {
            connection.rollback();
            throw exception;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
        }
    }

    public boolean confirmTicket(int ticketID) throws SQLException {
        String selectTicketSQL = "SELECT t.customerID, t.scheduleID, t.ticketPrice, t.status, s.eventID " +
                                 "FROM tickets t JOIN schedules s ON t.scheduleID = s.scheduleID WHERE t.ticketID = ?";
        String selectBalanceSQL = "SELECT balance FROM customers WHERE customerID = ? FOR UPDATE";
        String updateBalanceSQL = "UPDATE customers SET balance = balance - ? WHERE customerID = ?";
        String updateTicketSQL = "UPDATE tickets SET status = 'CO' WHERE ticketID = ? AND status = 'P'";
        String selectSectionTypeSQL = "SELECT category FROM sections WHERE sectionID = ?";
        String selectEventMerchSQL = "SELECT merchandiseID FROM event_merch WHERE eventID = ? AND merchtype = 'Package'";
        String insertMerchReceiptSQL = "INSERT INTO merch_receipt (ticketID, customerID, eventID, merchandiseID, quantity, totalPrice, purchaseDate) VALUES (?, ?, ?, ?, ?, ?, NOW())";

        boolean oldAutoCommit = connection.getAutoCommit();

        try {
            connection.setAutoCommit(false);

            int customerID, scheduleID, eventID, sectionID;
            double ticketPrice;
            String status;

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectTicketSQL)) {
                preparedStatement.setInt(1, ticketID);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        connection.rollback();

                        return false;
                    }

                    customerID = resultSet.getInt("customerID");
                    scheduleID = resultSet.getInt("scheduleID");
                    ticketPrice = resultSet.getDouble("ticketPrice");
                    status = resultSet.getString("status");
                    eventID = resultSet.getInt("eventID");
                }
            }

            String sectionType;

            try (PreparedStatement ps = connection.prepareStatement(selectSectionTypeSQL)) {
                ps.setInt(1, sectionID);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        connection.rollback();
                        return false;
                    }
                    sectionType = rs.getString("category");
                }
            }

            if (!"P".equals(status)) {
                connection.rollback();

                return false;
            }

            double balance;

            try (PreparedStatement psBalance = connection.prepareStatement(selectBalanceSQL)) {
                psBalance.setInt(1, customerID);

                try (ResultSet resultSet = psBalance.executeQuery()) {
                    if (!resultSet.next()) {
                        connection.rollback();

                        return false;
                    }

                    balance = resultSet.getDouble("balance");
                }
            }

            if (balance < ticketPrice) {
                connection.rollback();

                return false;
            }

            try (PreparedStatement psDeduct = connection.prepareStatement(updateBalanceSQL)) {
                psDeduct.setDouble(1, ticketPrice);
                psDeduct.setInt(2, customerID);
                int updated = psDeduct.executeUpdate();

                if (updated != 1) {
                    connection.rollback();

                    return false;
                }
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateTicketSQL)) {
                preparedStatement.setInt(1, ticketID);

                if (preparedStatement.executeUpdate() != 1) {
                    connection.rollback();

                    return false;
                }
            }

            try (PreparedStatement psMerch = connection.prepareStatement(selectEventMerchSQL);
                 PreparedStatement psInsert = connection.prepareStatement(insertMerchReceiptSQL)) {
                psMerch.setInt(1, eventID);

                try (ResultSet rs = psMerch.executeQuery()) {
                    while (rs.next()) {
                        int merchID = rs.getInt("merchandiseID");

                        psInsert.setInt(1, ticketID);
                        psInsert.setInt(2, customerID);
                        psInsert.setInt(3, eventID);
                        psInsert.setInt(4, merchID);
                        psInsert.setInt(5, 1);
                        psInsert.setDouble(6, 0.0);
                        psInsert.addBatch();
                    }
                }

                psInsert.executeBatch();
            }

            connection.commit();

            return true;
        } catch (SQLException exception) {
            connection.rollback();
            throw exception;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
        }
    }

    public boolean cancelTicket(int ticketID) throws SQLException {
        String selectTicketSQL = "SELECT customerID, scheduleID, sectionID, ticketPrice, status FROM tickets WHERE ticketID = ? FOR UPDATE";
        String updateTicketSQL = "UPDATE tickets SET status = 'CA' WHERE ticketID = ?";
        String incrementAvailSlotsSQL = "UPDATE schedule_section SET availableSlots = availableSlots + 1 WHERE scheduleID = ? AND sectionID = ?";
        String refundSQL = "UPDATE customers SET balance = balance + ? WHERE customerID = ?";
        String deleteMerchReceipt = "DELETE FROM merch_receipt WHERE ticketID = ?";

        boolean oldAutoCommit = connection.getAutoCommit();

        try {
            connection.setAutoCommit(false);

            int customerID, scheduleID, sectionID;
            double ticketPrice;
            String status;

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectTicketSQL)) {
                preparedStatement.setInt(1, ticketID);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        connection.rollback();

                        return false;
                    }

                    customerID = resultSet.getInt("customerID");
                    scheduleID = resultSet.getInt("scheduleID");
                    sectionID = resultSet.getInt("sectionID");
                    ticketPrice = resultSet.getDouble("ticketPrice");
                    status = resultSet.getString("status");
                }
            }

            if (!status.equals("P") && !status.equals("CO")) {
                connection.rollback();

                return false;
            }

            try (PreparedStatement psUpdateTicket = connection.prepareStatement(updateTicketSQL)) {
                psUpdateTicket.setInt(1, ticketID);
                int updated = psUpdateTicket.executeUpdate();

                if (updated != 1) {
                    connection.rollback();

                    return false;
                }
            }

            try (PreparedStatement psIncrement = connection.prepareStatement(incrementAvailSlotsSQL)) {
                psIncrement.setInt(1, scheduleID);
                psIncrement.setInt(2, sectionID);
                int updated = psIncrement.executeUpdate();

                if (updated != 1) {
                    connection.rollback();

                    return false;
                }
            }

            if ("CO".equals(status)) {
                try (PreparedStatement psRefund = connection.prepareStatement(refundSQL)) {
                    psRefund.setDouble(1, ticketPrice);
                    psRefund.setInt(2, customerID);
                    int updated = psRefund.executeUpdate();

                    if (updated != 1) {
                        connection.rollback();

                        return false;
                    }
                }

                try (PreparedStatement psDeleteMerchReceipt = connection.prepareStatement(deleteMerchReceipt)) {
                    psDeleteMerchReceipt.setInt(1, ticketID);
                    psDeleteMerchReceipt.executeUpdate();
                }
            }

            connection.commit();

            return true;
        } catch (SQLException exception) {
            connection.rollback();
            throw exception;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
        }
    }
}
