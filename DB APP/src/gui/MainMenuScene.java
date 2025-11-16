package gui;

import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.geometry.Insets;

import java.sql.Connection;
import java.sql.SQLException;

public class MainMenuScene {
    private BorderPane root;
    private Connection connection;

    public MainMenuScene(Connection connection) {
        this.connection = connection;
        root = new BorderPane();

        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));

        Button btnCustomers = new Button("Manage Customers");
        Button btnEvents = new Button("Manage Events");
        Button btnSchedules = new Button("Manage Schedules");
        Button btnMerchandise = new Button("Manage Merchandise");

        Button btnBookTicket = new Button("Book Ticket");
        Button btnMerchTrans = new Button("Merchandise Transactions");

        Button btnEventReport = new Button("Event Schedule Report");
        Button btnMerchReport = new Button("Merchandise Sales Report");

        sidebar.getChildren().addAll(btnCustomers, btnEvents, btnSchedules, btnMerchandise, btnBookTicket, btnMerchTrans, btnEventReport, btnMerchReport);
        root.setLeft(sidebar);

        VBox mainContent = new VBox();
        root.setCenter(mainContent);

//        btnCustomers.setOnAction(e -> mainContent.getChildren().setAll(new CustomerScene().getRoot()));
//        btnEvents.setOnAction(e -> mainContent.getChildren().setAll(new EventScene().getRoot()));
        MerchandiseScene merchScene = new MerchandiseScene(connection);
        btnMerchandise.setOnAction(e -> mainContent.getChildren().setAll(merchScene.getRoot()));
    }

    public Parent getRoot() {
        return root;
    }
}
