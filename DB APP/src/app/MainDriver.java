
package app;

import java.sql.Connection;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import gui.MainMenuScene;

public class MainDriver extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            Connection connection = utility.DBConnection.getConnection();
            MainMenuScene mainMenu = new MainMenuScene(connection);

            primaryStage.setScene(new Scene(mainMenu.getRoot(), 1000, 600));
            primaryStage.setTitle("DB APP");
            primaryStage.show();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database connection failed!");
            alert.showAndWait();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
