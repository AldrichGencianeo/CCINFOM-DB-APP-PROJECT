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

import model.Category;
import model.Event;
import model.Merchandise;
import dao.MerchandiseDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MerchandiseScene {
    private BorderPane root;
    private TableView<Merchandise> merchandiseTable;
    private Connection connection;

    public MerchandiseScene(Connection connection, MainMenuScene mainMenu) {
        this.connection = connection;
        root = new BorderPane();

        // Back button
        HBox backButton = SceneUtils.createBackButton(mainMenu, connection);
        root.setTop(backButton);

        // Create table but DO NOT SHOW it initially
        merchandiseTable = new TableView<>();
        setupTable();

        // Centered buttons (like MainMenuScene)
        VBox menuBox = new VBox(15);
        menuBox.setPadding(new Insets(20));
        menuBox.setAlignment(Pos.CENTER);

        Button btnAdd = new Button("Add Merchandise");
        Button btnUpdate = new Button("Update Selected");
        Button btnDelete = new Button("Delete Selected");
        Button btnView = new Button("View Selected");
        Button btnViewAll = new Button("View All Merchandise");
        Button btnViewEvents = new Button("View Related Events");

        btnAdd.setPrefWidth(200);
        btnUpdate.setPrefWidth(200);
        btnDelete.setPrefWidth(200);
        btnView.setPrefWidth(200);
        btnViewAll.setPrefWidth(200);
        btnViewEvents.setPrefWidth(200);

        menuBox.getChildren().addAll(
                btnAdd, btnUpdate, btnDelete, btnView, btnViewAll, btnViewEvents
        );

        // Set centered layout as default
        root.setCenter(menuBox);

        // Actions
        btnAdd.setOnAction(e -> addMerch());
        btnUpdate.setOnAction(e -> updateMerch());
        btnDelete.setOnAction(e -> deleteMerch());
        btnView.setOnAction(e -> viewMerch());
        btnViewAll.setOnAction(e -> viewAllMerch());
        btnViewEvents.setOnAction(e -> viewRelatedEvents());
    }

    public BorderPane getRoot() {
        return root;
    }

    private void setupTable() {
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
    }

    private Merchandise openSelectionDialog(String title) {
        Stage stage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TableView<Merchandise> table = new TableView<>();

        TableColumn<Merchandise, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("MerchandiseID"));

        TableColumn<Merchandise, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("merchandiseName"));

        table.getColumns().addAll(idCol, nameCol);

        try {
            MerchandiseDAO dao = new MerchandiseDAO(connection);
            table.getItems().setAll(dao.viewAllMerchandise());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Button selectBtn = new Button("Select");
        Button cancelBtn = new Button("Cancel");

        final Merchandise[] selected = {null};

        selectBtn.setOnAction(e -> {
            selected[0] = table.getSelectionModel().getSelectedItem();
            stage.close();
        });

        cancelBtn.setOnAction(e -> {
            selected[0] = null;
            stage.close();
        });

        layout.getChildren().addAll(
                new Label(title),
                table,
                new HBox(10, selectBtn, cancelBtn)
        );

        stage.setScene(new Scene(layout, 400, 300));
        stage.setTitle(title);
        stage.showAndWait();

        return selected[0];
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
                stage.close();
            } catch (SQLException | NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid input.").showAndWait();
            }
        });

        layout.getChildren().addAll(
                new Label("Add Merchandise"),
                txtName, txtPrice, txtStock, cmbCategory, btnSave
        );

        stage.setScene(new Scene(layout, 300, 250));
        stage.setTitle("Add Merchandise");
        stage.show();
    }

    private void updateMerch() {
        Merchandise selected = openSelectionDialog("Select Merchandise to Update");
        if (selected == null) return;

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
                stage.close();
            } catch (SQLException | NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid input.").showAndWait();
            }
        });

        layout.getChildren().addAll(
                new Label("Update Merchandise"),
                txtName, txtPrice, txtStock, cmbCategory, btnSave
        );

        stage.setScene(new Scene(layout, 300, 250));
        stage.setTitle("Update Merchandise");
        stage.show();
    }

    private void deleteMerch() {
        Merchandise selected = openSelectionDialog("Select Merchandise to Delete");
        if (selected == null) return;

        try {
            MerchandiseDAO dao = new MerchandiseDAO(connection);
            dao.deleteMerchandise(selected.getMerchandiseID());
            new Alert(Alert.AlertType.INFORMATION, "Record deleted.").showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewMerch() {
        Merchandise selected = openSelectionDialog("Select Merchandise to View");
        if (selected == null) return;

        try {
            MerchandiseDAO dao = new MerchandiseDAO(connection);
            Merchandise m = dao.viewMerchandise(selected.getMerchandiseID());

            if (m == null) {
                new Alert(Alert.AlertType.ERROR, "Record not found.").showAndWait();
                return;
            }

            Stage stage = new Stage();
            VBox layout = new VBox(10);
            layout.setPadding(new Insets(15));

            layout.getChildren().addAll(
                    new Label("Merchandise Details"),
                    new Label("ID: " + m.getMerchandiseID()),
                    new Label("Name: " + m.getMerchandiseName()),
                    new Label("Category: " + m.getCategory()),
                    new Label("Price: " + m.getPrice()),
                    new Label("Stock: " + m.getStock()),
                    new Button("Close")
            );

            stage.setScene(new Scene(layout, 300, 250));
            stage.setTitle("View Merchandise");
            stage.show();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void viewAllMerch() {
        Stage stage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label title = new Label("All Merchandise Records");
        TableView<Merchandise> table = new TableView<>();

        // Columns
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

        table.getColumns().addAll(idCol, nameCol, categoryCol, priceCol, stockCol);

        try {
            MerchandiseDAO dao = new MerchandiseDAO(connection);
            List<Merchandise> list = dao.viewAllMerchandise();
            table.getItems().setAll(list);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        Button close = new Button("Close");
        close.setOnAction(e -> stage.close());

        layout.getChildren().addAll(title, table, close);

        stage.setScene(new Scene(layout, 600, 400));
        stage.setTitle("All Merchandise");
        stage.show();
    }

    private void viewRelatedEvents() {
        Merchandise selected = openSelectionDialog("Select Merchandise to View Related Events");
        if (selected == null) return;

        Stage stage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        layout.getChildren().add(new Label("Related Events for " + selected.getMerchandiseName()));

        try {
            MerchandiseDAO dao = new MerchandiseDAO(connection);
            List<Event> events = dao.getRelatedEvents(selected.getMerchandiseID());

            if (events.isEmpty()) {
                layout.getChildren().add(new Label("No related events."));
            } else {
                for (Event e : events) {
                    layout.getChildren().add(new Label(
                            "ID: " + e.getEventID() +
                                    " | Name: " + e.getEventName() +
                                    " | Type: " + e.getEventType() +
                                    " | Booking Fee: " + e.getBookingFee()
                    ));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        stage.setScene(new Scene(layout, 400, 300));
        stage.setTitle("Related Events");
        stage.show();
    }
}