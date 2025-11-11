import java.sql.Connection;
import java.sql.SQLException;

public class MainDriver {
    public static void main(String[] args) {
        try (Connection connection = DBConnection.getConnection()) {
            System.out.println("Connected to database successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
