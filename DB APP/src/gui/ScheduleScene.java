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
import javafx.util.StringConverter;

import model.Event;
import model.Schedule;
import model.ScheduleSection;
import model.Section;
import dao.ScheduleDAO;
import dao.EventDAO;
import dao.SectionDAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Time;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ScheduleScene {
    private BorderPane root;
    private TableView<Schedule> scheduleTable;
    private Connection connection;

    public ScheduleScene(Connection connection, MainMenuScene mainMenu) {
        this.connection = connection;
        root = new BorderPane();

        HBox backButton = SceneUtils.createBackButton(mainMenu, connection);
        root.setTop(backButton);

        scheduleTable = new TableView<>();
        setupTable();
        loadSchedules();
        root.setCenter(scheduleTable);

        VBox buttons = new VBox(10);
        buttons.setPadding(new Insets(10));
        buttons.setAlignment(Pos.TOP_CENTER);

        Button btnAdd = new Button("Add Schedule");
        Button btnUpdate = new Button("Update Selected");
        Button btnDelete = new Button("Delete Selected");
        Button btnRefresh = new Button("Refresh Table");

        btnAdd.setMinWidth(150);
        btnUpdate.setMinWidth(150);
        btnDelete.setMinWidth(150);
        btnRefresh.setMinWidth(150);

        buttons.getChildren().addAll(btnAdd, btnUpdate, btnDelete, btnRefresh);
        root.setRight(buttons);

        btnAdd.setOnAction(e -> addSchedule());
        btnUpdate.setOnAction(e -> updateSchedule());
        btnDelete.setOnAction(e -> deleteSchedule());
        btnRefresh.setOnAction(e -> loadSchedules());
    }

    public BorderPane getRoot() { return root; }

    private void setupTable() {
        TableColumn<Schedule, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("scheduleID"));

        TableColumn<Schedule, Integer> eventIdCol = new TableColumn<>("Event ID");
        eventIdCol.setCellValueFactory(new PropertyValueFactory<>("eventID"));

        TableColumn<Schedule, Date> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("scheduleDate"));

        TableColumn<Schedule, Time> startCol = new TableColumn<>("Start Time");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));

        TableColumn<Schedule, Time> endCol = new TableColumn<>("End Time");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));

        scheduleTable.getColumns().addAll(idCol, eventIdCol, dateCol, startCol, endCol);
    }

    private void loadSchedules() {
        try {
            ScheduleDAO dao = new ScheduleDAO(connection);
            scheduleTable.getItems().setAll(dao.viewAllSchedules());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addSchedule() {
        Stage stage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        Label title = new Label("Create New Schedule");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ComboBox<Event> cmbEvent = new ComboBox<>();
        try {
            EventDAO eventDAO = new EventDAO(connection);
            cmbEvent.getItems().setAll(eventDAO.viewAllEvents());
        } catch (SQLException e) { e.printStackTrace(); }

        cmbEvent.setConverter(new StringConverter<Event>() {
            @Override
            public String toString(Event e) { return e == null ? "" : e.getEventName() + " (ID:" + e.getEventID() + ")"; }
            @Override
            public Event fromString(String s) { return null; }
        });
        cmbEvent.setPromptText("Select an Event");

        DatePicker datePicker = new DatePicker();
        TextField txtStart = new TextField(); txtStart.setPromptText("Start Time (HH:MM:SS)");
        TextField txtEnd = new TextField(); txtEnd.setPromptText("End Time (HH:MM:SS)");
        
        Label lblPrice = new Label("Set Standard Price for all Sections:");
        TextField txtPrice = new TextField(); txtPrice.setPromptText("e.g. 500.00");

        Button btnSave = new Button("Save Schedule & Prices");
        
        btnSave.setOnAction(e -> {
            try {
                if (cmbEvent.getValue() == null || datePicker.getValue() == null || 
                    txtStart.getText().isEmpty() || txtPrice.getText().isEmpty()) {
                    new Alert(Alert.AlertType.WARNING, "Fill all fields.").showAndWait();
                    return;
                }

                Schedule s = new Schedule();
                s.setEventID(cmbEvent.getValue().getEventID());
                s.setScheduleDate(Date.valueOf(datePicker.getValue())); 
                s.setStartTime(Time.valueOf(txtStart.getText()));       
                s.setEndTime(Time.valueOf(txtEnd.getText()));

                double price = Double.parseDouble(txtPrice.getText());
                SectionDAO sectionDAO = new SectionDAO(connection);
                List<Section> allSections = sectionDAO.viewAllSections();
                
                List<ScheduleSection> priceList = new ArrayList<>();
                for (Section sec : allSections) {
                    priceList.add(new ScheduleSection(0, sec.getSectionID(), sec.getCapacity(), price));
                }

                ScheduleDAO schedDAO = new ScheduleDAO(connection);
                schedDAO.addScheduleWithPrices(s, priceList);

                loadSchedules();
                stage.close();
                new Alert(Alert.AlertType.INFORMATION, "Schedule Created!").showAndWait();

            } catch (IllegalArgumentException timeErr) {
                new Alert(Alert.AlertType.ERROR, "Invalid Time format. Use HH:MM:SS").showAndWait();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
        });

        layout.getChildren().addAll(title, 
            new Label("Event:"), cmbEvent, 
            new Label("Date:"), datePicker, 
            new Label("Times (HH:MM:SS):"), txtStart, txtEnd,
            new Separator(), lblPrice, txtPrice,
            btnSave
        );

        stage.setScene(new Scene(layout, 350, 450));
        stage.setTitle("Add Schedule");
        stage.show();
    }

    private void updateSchedule() {
        Schedule selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Stage stage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        Label title = new Label("Reschedule (Event ID: " + selected.getEventID() + ")");
        
        DatePicker datePicker = new DatePicker(selected.getScheduleDate().toLocalDate());
        TextField txtStart = new TextField(selected.getStartTime().toString());
        TextField txtEnd = new TextField(selected.getEndTime().toString());

        Button btnSave = new Button("Update Time");
        btnSave.setOnAction(e -> {
            try {
                selected.setScheduleDate(Date.valueOf(datePicker.getValue()));
                selected.setStartTime(Time.valueOf(txtStart.getText()));
                selected.setEndTime(Time.valueOf(txtEnd.getText()));

                ScheduleDAO dao = new ScheduleDAO(connection);
                dao.updateSchedule(selected);
                loadSchedules();
                stage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        layout.getChildren().addAll(title, 
            new Label("New Date:"), datePicker,
            new Label("New Start Time:"), txtStart,
            new Label("New End Time:"), txtEnd,
            btnSave);
            
        stage.setScene(new Scene(layout, 300, 300));
        stage.show();
    }

    private void deleteSchedule() {
        Schedule selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                ScheduleDAO dao = new ScheduleDAO(connection);
                dao.deleteSchedule(selected.getScheduleID());
                loadSchedules();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}