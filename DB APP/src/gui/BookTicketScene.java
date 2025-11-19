package gui;

import dao.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class BookTicketScene {
    private VBox root;
    private Connection connection;
    private MainMenuScene mainMenu;
    private TextField tfTicketID;
    private Button btnCancel;

    private ComboBox<String> cbCustomer;
    private ComboBox<String> cbEvent;
    private ComboBox<String> cbSchedule;
    private ComboBox<String> cbSection;
    private Button btnBook;

    public BookTicketScene(Connection connection, MainMenuScene mainMenu) {
        this.connection = connection;
        this.mainMenu = mainMenu;

        root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        cbCustomer = new ComboBox<>();
        cbCustomer.setPromptText("Select Customer");

        cbEvent = new ComboBox<>();
        cbEvent.setPromptText("Select Event");

        cbSchedule = new ComboBox<>();
        cbSchedule.setPromptText("Select Schedule");

        cbSection = new ComboBox<>();
        cbSection.setPromptText("Select Section");

        btnBook = new Button("Book Ticket");

        btnCancel = new Button("Cancel Ticket");

        root.getChildren().addAll(cbCustomer, cbEvent, cbSchedule, cbSection, btnBook, btnCancel);

        // Populate customers and events
        populateCustomers();
        populateEvents();
        populateSections();

        btnBook.setOnAction(e -> bookTicket());
        btnCancel.setOnAction(e -> cancelTicket());

    }

    private void populateCustomers() {
        try {
            CustomerDAO dao = new CustomerDAO(connection);
            List<model.Customer> customers = dao.viewAllCustomers();
            for (model.Customer c : customers) {
                cbCustomer.getItems().add(c.getCustomerID() + " - " + c.getFirstName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateEvents() {
        try {
            EventDAO dao = new EventDAO(connection);
            List<model.Event> events = dao.viewAllEvents();

            for (model.Event e : events) {
                cbEvent.getItems().add(e.getEventID() + " - " + e.getEventName());
            }

            cbEvent.setOnAction(ev -> populateSchedules());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateSchedules() {
        cbSchedule.getItems().clear();
        cbSection.getItems().clear();

        if (cbEvent.getValue() == null) return;

        int eventID = Integer.parseInt(cbEvent.getValue().split(" - ")[0]);

        try {
            String sql = """
            SELECT s.scheduleID, s.scheduleDate, s.startTime, s.endTime
            FROM schedules s
            WHERE s.eventID = ?
            ORDER BY s.scheduleDate, s.startTime
            """;

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, eventID);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int scheduleID = rs.getInt("scheduleID");
                    cbSchedule.getItems().add(scheduleID + " - " + rs.getDate("scheduleDate") +
                            " " + rs.getTime("startTime") + "-" + rs.getTime("endTime"));
                }
            }

            cbSchedule.setOnAction(ev -> populateSections());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void populateSections() {
        cbSection.getItems().clear();

        if (cbSchedule.getValue() == null) return;

        int scheduleID = Integer.parseInt(cbSchedule.getValue().split(" - ")[0]);

        try {
            String sql = """
            SELECT sec.sectionID, sec.sectionname, ss.availableSlots
            FROM schedule_section ss
            JOIN section sec ON ss.sectionID = sec.sectionID
            WHERE ss.scheduleID = ?
            """;

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, scheduleID);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int sectionID = rs.getInt("sectionID");
                    String sectionName = rs.getString("sectionname");
                    int availableSlots = rs.getInt("availableSlots");
                    cbSection.getItems().add(sectionID + " - " + sectionName + " (Available: " + availableSlots + ")");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void bookTicket() {
        try {
            if (cbCustomer.getValue() == null || cbSchedule.getValue() == null || cbSection.getValue() == null) {
                showAlert("Error", "Please select customer, schedule, and section.");
                return;
            }

            int customerID = Integer.parseInt(cbCustomer.getValue().split(" - ")[0]);
            int scheduleID = Integer.parseInt(cbSchedule.getValue().split(" - ")[0]);
            int sectionID = Integer.parseInt(cbSection.getValue().split(" - ")[0]);

            BookTicketDAO dao = new BookTicketDAO(connection);
            int ticketID = dao.bookTicket(customerID, scheduleID, sectionID);

            if (ticketID > 0) {
                boolean confirmed = dao.confirmTicket(ticketID);
                showAlert("Success", "Ticket booked! Confirmed: " + confirmed);
            } else {
                showAlert("Error", "Booking failed. Slots may be full.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Error", "Database error: " + ex.getMessage());
        }
    }

    private void cancelTicket() {
        Stage popup = new Stage();
        popup.setTitle("Cancel Ticket");

        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setAlignment(Pos.CENTER);

        TextField tfTicketID = new TextField();
        tfTicketID.setPromptText("Ticket ID");

        Button btnConfirm = new Button("Cancel Ticket");
        box.getChildren().addAll(tfTicketID, btnConfirm);

        btnConfirm.setOnAction(e -> {
            String text = tfTicketID.getText();
            if (text == null || text.isEmpty()) {
                showAlert("Error", "Enter ticket ID.");
                return;
            }

            try {
                int ticketID = Integer.parseInt(text);
                BookTicketDAO dao = new BookTicketDAO(connection);
                boolean success = dao.cancelTicket(ticketID);

                if (success) {
                    showAlert("Success", "Ticket canceled!");
                    popup.close();
                } else {
                    showAlert("Error", "Failed to cancel ticket.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid ticket ID.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert("Error", "DB error: " + ex.getMessage());
            }
        });

        Scene scene = new Scene(box);
        popup.setScene(scene);
        popup.initOwner(root.getScene().getWindow()); // parent window
        popup.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Parent getRoot() {
        return root;
    }
}