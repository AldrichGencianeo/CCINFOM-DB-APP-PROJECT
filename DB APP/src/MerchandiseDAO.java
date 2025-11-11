import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MerchandiseDAO {
    private Connection connection;

    public MerchandiseDAO(Connection connection) {
        this.connection = connection;
    }

    // Add function
    public void addMerchandise(Merchandise merchandise) throws SQLException {
        String sql = "INSERT INTO merchandise (merchandiseName, category, price, stock) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, merchandise.getMerchandiseName());
            statement.setString(2, merchandise.getCategory().name());
            statement.setDouble(3, merchandise.getPrice());
            statement.setInt(4, merchandise.getStock());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedID = generatedKeys.getInt(1);
                    merchandise.setMerchandiseID(generatedID);
                }
            }
        }
    }

    // Update function
    public void updateMerchandise(Merchandise merchandise) throws SQLException {
        String sql = "UPDATE merchandise SET merchandiseName=?, category=?, price=?, stock=? WHERE merchandiseID=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, merchandise.getMerchandiseName());
            statement.setString(2, merchandise.getCategory().name());
            statement.setDouble(3, merchandise.getPrice());
            statement.setInt(4, merchandise.getStock());
            statement.setInt(5, merchandise.getMerchandiseID());
            statement.executeUpdate();
        }
    }

    // Delete function
    public void deleteMerchandise(int merchandiseID) throws SQLException {
        String sql = "DELETE FROM merchandise WHERE merchandiseID=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, merchandiseID);
            statement.executeUpdate();
        }
    }

    // View function
    public Merchandise viewMerchandise(int merchandiseID) throws SQLException {
        String sql = "SELECT * FROM merchandise WHERE merchandiseID=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, merchandiseID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Merchandise(
                        resultSet.getInt("merchandiseID"),
                        resultSet.getString("merchandiseName"),
                        Category.valueOf(resultSet.getString("category")),
                        resultSet.getDouble("price"),
                        resultSet.getInt("stock")
                );
            }
        }

        return null;
    }

    // List function
    public List<Merchandise> viewAllMerchandise() throws SQLException {
        List<Merchandise> list = new ArrayList<>();
        String sql = "SELECT * FROM merchandise";
        try (Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Merchandise merchandise = new Merchandise(
                        resultSet.getInt("merchandiseID"),
                        resultSet.getString("merchandiseName"),
                        Category.valueOf(resultSet.getString("category")),
                        resultSet.getDouble("price"),
                        resultSet.getInt("stock")
                );

                list.add(merchandise);
            }
        }

        return list;
    }

    public void viewRelatedEvents(int merchandiseID) throws SQLException {
        String query = """
                SELECT e.eventID, e.eventname, em.merchtype
                FROM event_merch em
                JOIN events e ON em.eventID = e.eventID
                WHERE em.merchandiseID = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, merchandiseID);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("This merchandise is included in the following events:");

            while (resultSet.next()) {
                String merchType = resultSet.getString("merchtype");
                System.out.println("Event ID: " + resultSet.getInt("eventID") +
                                    " | Event Name: " + resultSet.getString("eventname") +
                                    " | Type: " + merchType);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
