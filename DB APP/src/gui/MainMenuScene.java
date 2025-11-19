package gui;

import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.sql.Connection;

public class MainMenuScene {
    private BorderPane root;
    private Connection connection;
    private VBox menuBox;

    public MainMenuScene(Connection connection) {
        this.connection = connection;
        root = new BorderPane();

        menuBox = new VBox(15);
        menuBox.setPadding(new Insets(20));
        menuBox.setAlignment(Pos.CENTER);

        Button btnEvents = new Button("Manage Events");
        Button btnMerchandise = new Button("Manage Merchandise");
        Button btnSections = new Button("Manage Sections");
        Button btnBookTicket = new Button("Book Ticket");
        Button btnSchedules = new Button("Manage Schedules");
        Button btnMerchTrans = new Button("Merchandise Transaction");
        Button btnReports = new Button("Generate Reports");

        btnEvents.setMinWidth(200);
        btnMerchandise.setMinWidth(200);
        btnSections.setMinWidth(200);
        btnBookTicket.setMinWidth(200);
        btnSchedules.setMinWidth(200);
        btnMerchTrans.setMinWidth(200);
        btnReports.setMinWidth(200);

        menuBox.getChildren().addAll(btnEvents, btnMerchandise, btnSections, btnBookTicket, btnSchedules, btnMerchTrans, btnReports);
        root.setCenter(menuBox);

        // Manage Events
        btnEvents.setOnAction(e -> {
            EventScene scene = new EventScene(connection, this);
            // Clear the root completely and set the new scene
            root.setTop(null);
            root.setCenter(null);
            root.setBottom(null);
            root.setLeft(null);
            root.setRight(null);
            root.setCenter(scene.getRoot());
        });

        // Manage Merchandise
        btnMerchandise.setOnAction(e -> {
            MerchandiseScene scene = new MerchandiseScene(connection, this);
            root.setTop(null);
            root.setCenter(null);
            root.setBottom(null);
            root.setLeft(null);
            root.setRight(null);
            root.setCenter(scene.getRoot());
        });

        // Manage Sections
        btnSections.setOnAction(e -> {
            SectionScene scene = new SectionScene(connection, this);
            // Clear root and set center like the other buttons
            root.setTop(null);
            root.setCenter(null);
            root.setBottom(null);
            root.setLeft(null);
            root.setRight(null);
            root.setCenter(scene.getRoot());
        });
        
        // Book Ticket
        btnBookTicket.setOnAction(e -> {
            BookTicketScene scene = new BookTicketScene(connection, this);
            root.setTop(null);
            root.setCenter(null);
            root.setBottom(null);
            root.setLeft(null);
            root.setRight(null);
            root.setCenter(scene.getRoot());
        });

        // Manage Schedules
        btnSchedules.setOnAction(e -> {
            ScheduleScene scene = new ScheduleScene(connection, this);
            root.setTop(null);
            root.setCenter(null);
            root.setBottom(null);
            root.setLeft(null);
            root.setRight(null);
            root.setCenter(scene.getRoot());
        });

        // Merchandise Transaction
        btnMerchTrans.setOnAction(e -> {
            MerchTransactionScene scene = new MerchTransactionScene(connection, this);
            root.setTop(null);
            root.setCenter(null);
            root.setBottom(null);
            root.setLeft(null);
            root.setRight(null);
            root.setCenter(scene.getRoot());
        });

        // Generate Reports
        btnReports.setOnAction(e -> {
            ReportsScene scene = new ReportsScene(connection, this);
            root.setTop(null);
            root.setCenter(null);
            root.setBottom(null);
            root.setLeft(null);
            root.setRight(null);
            root.setCenter(scene.getRoot());
        });
    }

    public void showMainMenu() {
        root.setTop(null);
        root.setBottom(null);
        root.setLeft(null);
        root.setRight(null);
        root.setCenter(menuBox);
    }

    public Parent getRoot() {
        return root;
    }
}