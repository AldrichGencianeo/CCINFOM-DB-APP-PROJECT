package gui;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.geometry.Insets;

import model.Category;
import model.Event;
import model.Merchandise;
import dao.MerchandiseDAO;
import utility.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MerchandiseScene {
    private BorderPane root;
    private TableView<Merchandise> merchandiseTable;
    private Connection connection;

    public MerchandiseScene(Connection connection) {
        this.connection = connection;
        root = new BorderPane();

        merchandiseTable = new TableView<>();
        TableColumn<Merchandise, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("MerchandiseID"));

        TableColumn<Merchandise, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("merchandiseName"));

        TableColumn<Merchandise, Category> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Merchandise, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Merchandise, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));

        merchandiseTable.getColumns().addAll(idCol, nameCol, categoryCol, priceCol, stockCol);
        loadMerchandise();

        VBox buttons = new VBox(10);
        buttons.setPadding(new Insets(10));
        Button btnAdd = new Button("Add Merchandise");
        Button btnUpdate = new Button("Update Selected");
        Button btnDelete = new Button("Delete Selected");
        Button btnViewEvents = new Button("View Related Events");

        buttons.getChildren().addAll(btnAdd, btnUpdate, btnDelete, btnViewEvents);

        root.setCenter(merchandiseTable);
        root.setRight(buttons);

        // Button actions
        btnAdd.setOnAction(e -> addMerch());
        btnUpdate.setOnAction(e -> updateMerch());
        btnDelete.setOnAction(e -> deleteMerch());
        btnViewEvents.setOnAction(e -> viewRelatedEvents());
    }

    public BorderPane getRoot() {
        return root;
    }

    private void loadMerchandise() {
        try {
            MerchandiseDAO dao = new MerchandiseDAO(connection);
            List<Merchandise> merchandiseList = dao.viewAllMerchandise();
            merchandiseTable.getItems().setAll(merchandiseList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addMerch() {
        Stage stage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField txtName = new TextField();
        txtName.setPromptText("Merchandise Name");
        TextField txtPrice = new TextField();
        txtPrice.setPromptText("Price");
        TextField txtStock = new TextField();
        txtStock.setPromptText("Stock");

        ComboBox<Category> cmbCategory = new ComboBox<>();
        cmbCategory.getItems().setAll(Category.values());

        Button btnSave = new Button("Save");
        btnSave.setOnAction(e -> {
            try {
                Merchandise m = new Merchandise();
                m.setMerchandiseName(txtName.getText());
                m.setPrice(Double.parseDouble(txtPrice.getText()));
                m.setStock(Integer.parseInt(txtStock.getText()));
                m.setCategory(cmbCategory.getValue());

                MerchandiseDAO dao = new MerchandiseDAO(connection);
                dao.addMerchandise(m);
                loadMerchandise();
                stage.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NumberFormatException nfe) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Price and Stock must be numbers.");
                alert.showAndWait();
            }
        });

        layout.getChildren().addAll(new Label("Add Merchandise"), txtName, txtPrice, txtStock, cmbCategory, btnSave);
        stage.setScene(new Scene(layout, 300, 250));
        stage.setTitle("Add Merchandise");
        stage.show();
    }

    private void updateMerch() {
        Merchandise selected = merchandiseTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            return;
        }

        Stage stage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField txtName = new TextField(selected.getMerchandiseName());
        TextField txtPrice = new TextField(String.valueOf(selected.getPrice()));
        TextField txtStock = new TextField(String.valueOf(selected.getStock()));

        ComboBox<Category> cmbCategory = new ComboBox<>();
        cmbCategory.getItems().setAll(Category.values());
        cmbCategory.setValue(selected.getCategory());

        Button btnSave = new Button("Update");
        btnSave.setOnAction(e -> {
            try {
                selected.setMerchandiseName(txtName.getText());
                selected.setPrice(Double.parseDouble(txtPrice.getText()));
                selected.setStock(Integer.parseInt(txtStock.getText()));
                selected.setCategory(cmbCategory.getValue());

                MerchandiseDAO dao = new MerchandiseDAO(connection);
                dao.updateMerchandise(selected);
                loadMerchandise();
                stage.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NumberFormatException nfe) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Price and Stock must be numbers.");
                alert.showAndWait();
            }
        });

        layout.getChildren().addAll(new Label("Update Merchandise"), txtName, txtPrice, txtStock, cmbCategory, btnSave);
        stage.setScene(new Scene(layout, 300, 250));
        stage.setTitle("Update Merchandise");
        stage.show();
    }

    private void deleteMerch() {
        Merchandise selected = merchandiseTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            return;
        }

        try {
            MerchandiseDAO dao = new MerchandiseDAO(connection);
            dao.deleteMerchandise(selected.getMerchandiseID());
            loadMerchandise();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void viewRelatedEvents() {
        Merchandise selected = merchandiseTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            return;
        }

        Stage stage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label title = new Label("Related Events for " + selected.getMerchandiseName());
        layout.getChildren().add(title);

        try {
            MerchandiseDAO dao = new MerchandiseDAO(connection);
            List<Event> events = dao.getRelatedEvents(selected.getMerchandiseID());

            if (events.isEmpty()) {
                layout.getChildren().add(new Label("No related events."));
            } else {
                for (Event e : events) {
                    Label eventLabel = new Label(String.format("ID: %d | Name: %s | Type: %s | Booking Fee: %.2f",
                            e.getEventID(), e.getEventName(), e.getEventType(), e.getBookingFee()));
                    layout.getChildren().add(eventLabel);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            layout.getChildren().add(new Label("Error loading related events."));
        }

        stage.setScene(new Scene(layout, 400, 300));
        stage.setTitle("Related Events");
        stage.show();
    }
}
