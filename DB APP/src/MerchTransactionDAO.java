import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MerchTransactionDAO {
    private Connection connection;
    private MerchandiseDAO merchandiseDAO;
    private CustomerDAO customerDAO;

    public MerchTransactionDAO(Connection connection) {
        this.connection = connection;
        this.merchandiseDAO = new MerchandiseDAO(connection);
        this.customerDAO = new CustomerDAO(connection);
    }

    public List<Merchandise> getEventMerchandise(int eventID) throws SQLException {
        List<Merchandise> list = new ArrayList<>();
        String sql = """
                SELECT m.merchandiseID, m.merchandisename, m.category, m.price, m.stock, em.merchtype
                FROM event_merch em
                JOIN merchandise m ON em.merchandiseID = m.merchandiseID
                WHERE em.eventID = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, eventID);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("\n=== MERCHANDISE AVAILABLE FOR EVENT ID: " + eventID + " ===");
            boolean hasItems = false;

            while (resultSet.next()) {
                hasItems = true;
                Merchandise merch = new Merchandise(
                        resultSet.getInt("merchandiseID"),
                        resultSet.getString("merchandisename"),
                        Category.valueOf(resultSet.getString("category")),
                        resultSet.getDouble("price"),
                        resultSet.getInt("stock")
                );
                list.add(merch);

                String merchType = resultSet.getString("merchtype");
                System.out.printf("ID: %d | Name: %s | Type: %s | Price: %.2f | Stock: %d%n",
                        merch.getMerchandiseID(),
                        merch.getMerchandiseName(),
                        merchType,
                        merch.getPrice(),
                        merch.getStock());
            }

            if (!hasItems) {
                System.out.println("No merchandise available for this event.");
            }
            System.out.println();
        }

        return list;
    }

    public boolean checkStock(int merchandiseID, int quantity) throws SQLException {
        Merchandise merch = merchandiseDAO.viewMerchandise(merchandiseID);
        if (merch == null) {
            System.out.println("Merchandise not found.");
            return false;
        }

        if (merch.getStock() < quantity) {
            System.out.printf("Insufficient stock! Available: %d, Requested: %d%n",
                    merch.getStock(), quantity);
            return false;
        }

        return true;
    }

    public boolean processMerchTransaction(int customerID, int eventID, int merchandiseID, int quantity)
            throws SQLException {

        connection.setAutoCommit(false);

        try {
            Merchandise merch = merchandiseDAO.viewMerchandise(merchandiseID);
            if (merch == null) {
                System.out.println("Error: Merchandise not found.");
                connection.rollback();
                return false;
            }

            if (!checkStock(merchandiseID, quantity)) {
                connection.rollback();
                return false;
            }

            // Step 3: Calculate total price
            double totalPrice = merch.getPrice() * quantity;

            Customer customer = customerDAO.viewCustomer(customerID);
            if (customer == null) {
                System.out.println("Error: Customer not found.");
                connection.rollback();
                return false;
            }

            System.out.println("\n=== TRANSACTION DETAILS ===");
            System.out.println("Customer: " + customer.getFirstName() + " " + customer.getLastName());
            System.out.println("Current Balance: " + customer.getBalance());
            System.out.println("Item: " + merch.getMerchandiseName());
            System.out.println("Quantity: " + quantity);
            System.out.println("Price per item: " + merch.getPrice());
            System.out.println("Total Price: " + totalPrice);

            if (!customerDAO.hasSufficientBalance(customerID, totalPrice)) {
                System.out.println("\nError: Insufficient balance!");
                System.out.printf("Required: %.2f, Available: %.2f%n", totalPrice, customer.getBalance());
                connection.rollback();
                return false;
            }

            if (!isMerchandiseInEvent(eventID, merchandiseID)) {
                System.out.println("Error: This merchandise is not available for the selected event.");
                connection.rollback();
                return false;
            }

            if (!customerDAO.updateBalance(customerID, totalPrice)) {
                System.out.println("Error: Failed to update customer balance.");
                connection.rollback();
                return false;
            }

            merch.setStock(merch.getStock() - quantity);
            merchandiseDAO.updateMerchandise(merch);

            String sql = "INSERT INTO merch_receipt (customerID, eventID, merchandiseID, quantity, totalprice) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, customerID);
                statement.setInt(2, eventID);
                statement.setInt(3, merchandiseID);
                statement.setInt(4, quantity);
                statement.setDouble(5, totalPrice);
                statement.executeUpdate();

                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int receiptID = generatedKeys.getInt(1);
                    System.out.println("\n=== TRANSACTION SUCCESSFUL ===");
                    System.out.println("Receipt ID: " + receiptID);
                    System.out.printf("New Balance: %.2f%n", customer.getBalance() - totalPrice);
                    System.out.println("Remaining Stock: " + (merch.getStock()));
                }
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            connection.rollback();
            System.out.println("Transaction failed: " + e.getMessage());
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private boolean isMerchandiseInEvent(int eventID, int merchandiseID) throws SQLException {
        String sql = "SELECT COUNT(*) FROM event_merch WHERE eventID = ? AND merchandiseID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, eventID);
            statement.setInt(2, merchandiseID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        }
        return false;
    }

    public void viewCustomerReceipts(int customerID) throws SQLException {
        String sql = """
                SELECT mr.receiptID, mr.eventID, e.eventname, mr.merchandiseID, 
                       m.merchandisename, mr.quantity, mr.totalprice
                FROM merch_receipt mr
                JOIN events e ON mr.eventID = e.eventID
                JOIN merchandise m ON mr.merchandiseID = m.merchandiseID
                WHERE mr.customerID = ?
                ORDER BY mr.receiptID DESC
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerID);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("\n=== PURCHASE HISTORY FOR CUSTOMER ID: " + customerID + " ===");
            boolean hasReceipts = false;

            while (resultSet.next()) {
                hasReceipts = true;
                System.out.printf("Receipt #%d | Event: %s | Item: %s | Qty: %d | Total: %.2f%n",
                        resultSet.getInt("receiptID"),
                        resultSet.getString("eventname"),
                        resultSet.getString("merchandisename"),
                        resultSet.getInt("quantity"),
                        resultSet.getDouble("totalprice"));
            }

            if (!hasReceipts) {
                System.out.println("No purchase history found.");
            }
            System.out.println();
        }
    }
}