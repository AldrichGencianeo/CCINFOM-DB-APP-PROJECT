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

import model.Section;
import dao.SectionDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SectionScene {
    private BorderPane root;
    private TableView<Section> sectionTable;
    private Connection connection;

    public SectionScene(Connection connection, MainMenuScene mainMenu) {
        this.connection = connection;
        root = new BorderPane();

        HBox backButton = SceneUtils.createBackButton(mainMenu, connection);
        root.setTop(backButton);

        sectionTable = new TableView<>();
        setupTable();
        loadSections();
        root.setCenter(sectionTable);

        VBox buttons = new VBox(10);
        buttons.setPadding(new Insets(10));
        buttons.setAlignment(Pos.TOP_CENTER);

        Button btnAdd = new Button("Add Section");
        Button btnUpdate = new Button("Update Selected");
        Button btnDelete = new Button("Delete Selected");
        Button btnViewDetails = new Button("View Usage Details");
        Button btnRefresh = new Button("Refresh Table");

        btnAdd.setMinWidth(150);
        btnUpdate.setMinWidth(150);
        btnDelete.setMinWidth(150);
        btnViewDetails.setMinWidth(150);
        btnRefresh.setMinWidth(150);

        buttons.getChildren().addAll(btnAdd, btnUpdate, btnDelete, btnViewDetails, btnRefresh);
        root.setRight(buttons);

        btnAdd.setOnAction(e -> addSection());
        btnUpdate.setOnAction(e -> updateSection());
        btnDelete.setOnAction(e -> deleteSection());
        btnViewDetails.setOnAction(e -> viewSectionUsage());
        btnRefresh.setOnAction(e -> loadSections());
    }

    public BorderPane getRoot() {
        return root;
    }

    private void setupTable() {
        TableColumn<Section, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("sectionID"));
        idCol.setPrefWidth(50);

        TableColumn<Section, String> nameCol = new TableColumn<>("Section Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("sectionName"));
        nameCol.setPrefWidth(200);

        TableColumn<Section, Integer> capCol = new TableColumn<>("Total Capacity");
        capCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        capCol.setPrefWidth(100);

        sectionTable.getColumns().addAll(idCol, nameCol, capCol);
    }

    private void loadSections() {
        try {
            SectionDAO dao = new SectionDAO(connection);
            List<Section> sections = dao.viewAllSections();
            sectionTable.getItems().setAll(sections);
        } catch (SQLException e) {
            showError("Database Error", e.getMessage());
        }
    }

    private void addSection() {
        Stage stage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        Label title = new Label("Add New Section");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField txtName = new TextField();
        txtName.setPromptText("Section Name (e.g. VIP, General Admission)");

        TextField txtCapacity = new TextField();
        txtCapacity.setPromptText("Capacity (e.g. 100)");

        Button btnSave = new Button("Save Section");
        
        btnSave.setOnAction(e -> {
            try {
                if (txtName.getText().isEmpty() || txtCapacity.getText().isEmpty()) {
                    showWarning("Please fill in all fields.");
                    return;
                }

                Section s = new Section();
                s.setSectionName(txtName.getText());
                s.setCapacity(Integer.parseInt(txtCapacity.getText()));

                SectionDAO dao = new SectionDAO(connection);
                dao.addSection(s);
                
                loadSections();
                stage.close();
                showInfo("Section added successfully!");
            } catch (NumberFormatException nfe) {
                showError("Invalid Input", "Capacity must be a number.");
            } catch (SQLException ex) {
                showError("Database Error", ex.getMessage());
            }
        });

        layout.getChildren().addAll(title, new Label("Name:"), txtName, new Label("Capacity:"), txtCapacity, btnSave);
        stage.setScene(new Scene(layout, 300, 250));
        stage.setTitle("Add Section");
        stage.show();
    }

    private void updateSection() {
        Section selected = sectionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a section to update.");
            return;
        }

        Stage stage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        Label title = new Label("Update Section");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField txtName = new TextField(selected.getSectionName());
        TextField txtCapacity = new TextField(String.valueOf(selected.getCapacity()));

        Button btnSave = new Button("Update");

        btnSave.setOnAction(e -> {
            try {
                selected.setSectionName(txtName.getText());
                selected.setCapacity(Integer.parseInt(txtCapacity.getText()));

                SectionDAO dao = new SectionDAO(connection);
                dao.updateSection(selected);
                
                loadSections();
                stage.close();
                showInfo("Section updated successfully!");
            } catch (Exception ex) {
                showError("Error", ex.getMessage());
            }
        });

        layout.getChildren().addAll(title, new Label("Name:"), txtName, new Label("Capacity:"), txtCapacity, btnSave);
        stage.setScene(new Scene(layout, 300, 250));
        stage.show();
    }

    private void deleteSection() {
        Section selected = sectionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a section to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setContentText("Are you sure? This might fail if schedules are using this section.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    SectionDAO dao = new SectionDAO(connection);
                    dao.deleteSection(selected.getSectionID());
                    loadSections();
                    showInfo("Section deleted.");
                } catch (SQLException ex) {
                    showError("Delete Failed", "Cannot delete section. It might be in use by an event.\n" + ex.getMessage());
                }
            }
        });
    }

    private void viewSectionUsage() {
        Section selected = sectionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Select a section first.");
            return;
        }
        try {
            SectionDAO dao = new SectionDAO(connection);
            dao.viewSectionDetails(selected.getSectionID());
            showInfo("Section details printed to Console/Terminal.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showError(String title, String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }
    private void showWarning(String message) {
        new Alert(Alert.AlertType.WARNING, message).showAndWait();
    }
    private void showInfo(String message) {
        new Alert(Alert.AlertType.INFORMATION, message).showAndWait();
    }
}