
package gui;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import model.Event;
import dao.EventDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Time;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class EventScene {
    private BorderPane root;
    private TableView<Event> eventTable;
    private Connection connection;

    public EventScene(Connection connection, MainMenuScene mainMenu) {
        this.connection = connection;
        root = new BorderPane();

        HBox backButton = SceneUtils.createBackButton(mainMenu, connection);
        root.setTop(backButton);

        eventTable = new TableView<>();
        TableColumn<Event, Integer> idCol = new TableColumn<>("Event ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("eventID"));
        idCol.setPrefWidth(80);

        TableColumn<Event, String> nameCol = new TableColumn<>("Event Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        nameCol.setPrefWidth(200);

        TableColumn<Event, String> typeCol = new TableColumn<>("Event Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        typeCol.setPrefWidth(120);

        TableColumn<Event, Double> feeCol = new TableColumn<>("Booking Fee");
        feeCol.setCellValueFactory(new PropertyValueFactory<>("bookingFee"));
        feeCol.setPrefWidth(100);

        eventTable.getColumns().addAll(idCol, nameCol, typeCol, feeCol);
        loadEvents();

        VBox buttons = new VBox(10);
        buttons.setPadding(new Insets(10));
        buttons.setAlignment(Pos.TOP_CENTER);

        Button btnAdd = new Button("Add Event");
        Button btnUpdate = new Button("Update Selected");
        Button btnDelete = new Button("Delete Selected");
        Button btnViewDetails = new Button("View Event Details");
        Button btnViewSchedules = new Button("View Schedules");
        Button btnViewAll = new Button("Refresh Table");

        btnAdd.setMinWidth(150);
        btnUpdate.setMinWidth(150);
        btnDelete.setMinWidth(150);
        btnViewDetails.setMinWidth(150);
        btnViewSchedules.setMinWidth(150);
        btnViewAll.setMinWidth(150);

        buttons.getChildren().addAll(btnAdd, btnUpdate, btnDelete, btnViewDetails, btnViewSchedules, btnViewAll);

        root.setCenter(eventTable);
        root.setRight(buttons);

        btnAdd.setOnAction(e -> addEvent());
        btnUpdate.setOnAction(e -> updateEvent());
        btnDelete.setOnAction(e -> deleteEvent());
        btnViewDetails.setOnAction(e -> viewEventDetails());
        btnViewSchedules.setOnAction(e -> viewEventSchedules());
        btnViewAll.setOnAction(e -> loadEvents());
    }

    public BorderPane getRoot() {
        return root;
    }

    private void loadEvents() {
        try {
            EventDAO dao = new EventDAO(connection);
            List<Event> events = dao.viewAllEvents();
            eventTable.getItems().setAll(events);
        } catch (SQLException e) {
            showError("Failed to load events", e.getMessage());
        }
    }

    private void addEvent() {
        Stage stage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        Label title = new Label("Add New Event");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField txtName = new TextField();
        txtName.setPromptText("Event Name");

        ComboBox<String> cmbType = new ComboBox<>();
        cmbType.getItems().addAll("Concert", "Theater", "Sports", "Conference", "Festival");
        cmbType.setPromptText("Select Event Type");

        TextField txtFee = new TextField();
        txtFee.setPromptText("Booking Fee");

        Button btnSave = new Button("Save Event");
        Button btnCancel = new Button("Cancel");

        HBox buttonBox = new HBox(10, btnSave, btnCancel);
        buttonBox.setAlignment(Pos.CENTER);

        btnSave.setOnAction(e -> {
            try {
                if (txtName.getText().isEmpty() || cmbType.getValue() == null || txtFee.getText().isEmpty()) {
                    showWarning("Please fill in all fields.");
                    return;
                }

                Event event = new Event();
                event.setEventName(txtName.getText());
                event.setEventType(cmbType.getValue());
                event.setBookingFee(Double.parseDouble(txtFee.getText()));

                EventDAO dao = new EventDAO(connection);
                dao.addEvent(event);
                loadEvents();
                stage.close();
                showInfo("Event added successfully!");
            } catch (NumberFormatException nfe) {
                showError("Invalid Input", "Booking fee must be a valid number.");
            } catch (SQLException ex) {
                showError("Database Error", ex.getMessage());
            }
        });

        btnCancel.setOnAction(e -> stage.close());

        layout.getChildren().addAll(title, new Label("Event Name:"), txtName,
                new Label("Event Type:"), cmbType,
                new Label("Booking Fee:"), txtFee,
                buttonBox);

        stage.setScene(new Scene(layout, 350, 350));
        stage.setTitle("Add Event");
        stage.show();
    }

    private void updateEvent() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select an event to update.");
            return;
        }

        Stage stage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        Label title = new Label("Update Event");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField txtName = new TextField(selected.getEventName());
        ComboBox<String> cmbType = new ComboBox<>();
        cmbType.getItems().addAll("Concert", "Theater", "Sports", "Conference", "Festival");
        cmbType.setValue(selected.getEventType());

        TextField txtFee = new TextField(String.valueOf(selected.getBookingFee()));

        Button btnSave = new Button("Update Event");
        Button btnCancel = new Button("Cancel");

        HBox buttonBox = new HBox(10, btnSave, btnCancel);
        buttonBox.setAlignment(Pos.CENTER);

        btnSave.setOnAction(e -> {
            try {
                selected.setEventName(txtName.getText());
                selected.setEventType(cmbType.getValue());
                selected.setBookingFee(Double.parseDouble(txtFee.getText()));

                EventDAO dao = new EventDAO(connection);
                dao.updateEvent(selected);
                loadEvents();
                stage.close();
                showInfo("Event updated successfully!");
            } catch (NumberFormatException nfe) {
                showError("Invalid Input", "Booking fee must be a valid number.");
            } catch (SQLException ex) {
                showError("Database Error", ex.getMessage());
            }
        });

        btnCancel.setOnAction(e -> stage.close());

        layout.getChildren().addAll(title, new Label("Event Name:"), txtName,
                new Label("Event Type:"), cmbType,
                new Label("Booking Fee:"), txtFee,
                buttonBox);

        stage.setScene(new Scene(layout, 350, 350));
        stage.setTitle("Update Event");
        stage.show();
    }

    private void deleteEvent() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select an event to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Event: " + selected.getEventName());
        confirm.setContentText("Are you sure you want to delete this event?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    EventDAO dao = new EventDAO(connection);
                    dao.deleteEvent(selected.getEventID());
                    loadEvents();
                    showInfo("Event deleted successfully!");
                } catch (SQLException ex) {
                    showError("Delete Failed", ex.getMessage());
                }
            }
        });
    }

    private void viewEventDetails() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select an event to view details.");
            return;
        }

        Stage stage = new Stage();
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f4f4f4;");

        Label title = new Label("Event Details");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox detailsBox = new VBox(8);
        detailsBox.setPadding(new Insets(10));
        detailsBox.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        Label lblID = new Label("Event ID: " + selected.getEventID());
        Label lblName = new Label("Event Name: " + selected.getEventName());
        Label lblType = new Label("Event Type: " + selected.getEventType());
        Label lblFee = new Label("Booking Fee: ₱" + String.format("%.2f", selected.getBookingFee()));

        detailsBox.getChildren().addAll(lblID, lblName, lblType, lblFee);

        Button btnClose = new Button("Close");
        btnClose.setOnAction(e -> stage.close());

        layout.getChildren().addAll(title, detailsBox, btnClose);
        layout.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(layout, 400, 250));
        stage.setTitle("Event Details");
        stage.show();
    }

    private void viewEventSchedules() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select an event to view schedules.");
            return;
        }

        Stage stage = new Stage();
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(15));

        VBox topBox = new VBox(5);
        Label title = new Label("Event: " + selected.getEventName());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label subtitle = new Label("Type: " + selected.getEventType() + " | Booking Fee: ₱" +
                String.format("%.2f", selected.getBookingFee()));
        topBox.getChildren().addAll(title, subtitle);
        layout.setTop(topBox);

        TableView<ScheduleDisplay> scheduleTable = new TableView<>();

        TableColumn<ScheduleDisplay, Integer> schedIDCol = new TableColumn<>("Schedule ID");
        schedIDCol.setCellValueFactory(new PropertyValueFactory<>("scheduleID"));
        schedIDCol.setPrefWidth(100);

        TableColumn<ScheduleDisplay, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(120);

        TableColumn<ScheduleDisplay, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        timeCol.setPrefWidth(150);

        TableColumn<ScheduleDisplay, String> sectionCol = new TableColumn<>("Section");
        sectionCol.setCellValueFactory(new PropertyValueFactory<>("sectionName"));
        sectionCol.setPrefWidth(100);

        TableColumn<ScheduleDisplay, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(100);

        TableColumn<ScheduleDisplay, Integer> capacityCol = new TableColumn<>("Capacity");
        capacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        capacityCol.setPrefWidth(80);

        TableColumn<ScheduleDisplay, Integer> availableCol = new TableColumn<>("Available");
        availableCol.setCellValueFactory(new PropertyValueFactory<>("availableSlots"));
        availableCol.setPrefWidth(80);

        scheduleTable.getColumns().addAll(schedIDCol, dateCol, timeCol, sectionCol,
                priceCol, capacityCol, availableCol);

        try {
            String query = """
                SELECT s.scheduleID, s.scheduleDate, s.startTime, s.endTime,
                       sec.sectionname, sec.price, sec.capacity, ss.availableSlots
                FROM schedules s
                LEFT JOIN schedule_section ss ON s.scheduleID = ss.scheduleID
                LEFT JOIN section sec ON ss.sectionID = sec.sectionID
                WHERE s.eventID = ?
                ORDER BY s.scheduleDate, s.startTime, sec.sectionname
                """;

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, selected.getEventID());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int scheduleID = rs.getInt("scheduleID");
                Date scheduleDate = rs.getDate("scheduleDate");
                Time startTime = rs.getTime("startTime");
                Time endTime = rs.getTime("endTime");
                String sectionName = rs.getString("sectionname");
                Double price = rs.getObject("price", Double.class);
                Integer capacity = rs.getObject("capacity", Integer.class);
                Integer availableSlots = rs.getObject("availableSlots", Integer.class);

                String timeStr = startTime + " - " + endTime;

                scheduleTable.getItems().add(new ScheduleDisplay(
                        scheduleID,
                        scheduleDate != null ? scheduleDate.toString() : "N/A",
                        timeStr,
                        sectionName != null ? sectionName : "No Section",
                        price != null ? price : 0.0,
                        capacity != null ? capacity : 0,
                        availableSlots != null ? availableSlots : 0
                ));
            }

            if (scheduleTable.getItems().isEmpty()) {
                Label noData = new Label("No schedules found for this event.");
                noData.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
                VBox emptyBox = new VBox(noData);
                emptyBox.setAlignment(Pos.CENTER);
                layout.setCenter(emptyBox);
            } else {
                layout.setCenter(scheduleTable);
            }

        } catch (SQLException ex) {
            showError("Failed to load schedules", ex.getMessage());
        }

        Button btnClose = new Button("Close");
        btnClose.setOnAction(e -> stage.close());
        HBox bottomBox = new HBox(btnClose);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10, 0, 0, 0));
        layout.setBottom(bottomBox);

        stage.setScene(new Scene(layout, 850, 500));
        stage.setTitle("Event Schedules - " + selected.getEventName());
        stage.show();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class ScheduleDisplay {
        private int scheduleID;
        private String date;
        private String time;
        private String sectionName;
        private double price;
        private int capacity;
        private int availableSlots;

        public ScheduleDisplay(int scheduleID, String date, String time, String sectionName,
                               double price, int capacity, int availableSlots) {
            this.scheduleID = scheduleID;
            this.date = date;
            this.time = time;
            this.sectionName = sectionName;
            this.price = price;
            this.capacity = capacity;
            this.availableSlots = availableSlots;
        }

        public int getScheduleID() { return scheduleID; }
        public String getDate() { return date; }
        public String getTime() { return time; }
        public String getSectionName() { return sectionName; }
        public double getPrice() { return price; }
        public int getCapacity() { return capacity; }
        public int getAvailableSlots() { return availableSlots; }
    }
}
