import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private Connection connection;

    public CustomerDAO(Connection connection) {
        this.connection = connection;
    }

    public Customer viewCustomer(int customerID) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customerID=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Customer(
                        resultSet.getInt("customerID"),
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("email"),
                        resultSet.getString("phoneNumber"),
                        resultSet.getDouble("balance")
                );
            }
        }
        return null;
    }

    public List<Customer> viewAllCustomers() throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Customer customer = new Customer(
                        resultSet.getInt("customerID"),
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("email"),
                        resultSet.getString("phoneNumber"),
                        resultSet.getDouble("balance")
                );
                list.add(customer);
            }
        }
        return list;
    }

    public boolean updateBalance(int customerID, double amount) throws SQLException {
        String sql = "UPDATE customers SET balance = balance - ? WHERE customerID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, amount);
            statement.setInt(2, customerID);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean hasSufficientBalance(int customerID, double amount) throws SQLException {
        Customer customer = viewCustomer(customerID);
        if (customer == null) {
            return false;
        }
        return customer.getBalance() >= amount;
    }
}