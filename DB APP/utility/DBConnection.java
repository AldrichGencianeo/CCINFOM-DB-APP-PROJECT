package utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/dbapp";
    private static final String USER = "root";
    private static final String PASSWORD = "p@ssword";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found. Make sure it's in the classpath!");
            e.printStackTrace();
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}