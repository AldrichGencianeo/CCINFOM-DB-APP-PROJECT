package gui;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import model.Event;
import model.Merchandise;
import model.Customer;
import model.Category;
import dao.EventDAO;
import dao.MerchTransactionDAO;
import dao.CustomerDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MerchTransactionScene {
    private BorderPane root;
    private Connection connection;

    private TableView<Event> eventTable;
    private TableView<MerchandiseDisplay> merchTable;
    private TableView<Customer> customerTable;

    private Label lblSelectedEvent;
    private Label lblSelectedCustomer;
    private Label lblSelectedMerch;

    private Event selectedEvent;
    private Customer selectedCustomer;
    private MerchandiseDisplay selectedMerchandise;

    public MerchTransactionScene(Connection connection, MainMenuScene mainMenu) {
        this.connection = connection;
        root = new BorderPane();

        HBox backButton = SceneUtils.createBackButton(mainMenu, connection);
        root.setTop(backButton);

        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(15));

        Label title = new Label("Merchandise Transaction Management");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        VBox step1 = createEventSelectionSection();

        VBox step2 = createMerchandiseSection();

        VBox step3 = createCustomerSection();

        VBox step4 = createTransactionSection();

        mainContent.getChildren().addAll(title, step1, step2, step3, step4);

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);

        loadEvents();
        loadCustomers();
    }

    private VBox createEventSelectionSection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 10;");

        Label header = new Label("Step 1: Select Event");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #3498db;");

        eventTable = new TableView<>();
        eventTable.setPrefHeight(150);

        TableColumn<Event, Integer> idCol = new TableColumn<>("Event ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("eventID"));
        idCol.setPrefWidth(80);

        TableColumn<Event, String> nameCol = new TableColumn<>("Event Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        nameCol.setPrefWidth(250);

        TableColumn<Event, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        typeCol.setPrefWidth(120);

        eventTable.getColumns().addAll(idCol, nameCol, typeCol);

        eventTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedEvent = newSelection;
                lblSelectedEvent.setText("Selected Event: " + newSelection.getEventName());
                loadEventMerchandise(newSelection.getEventID());
            }
        });

        lblSelectedEvent = new Label("No event selected");
        lblSelectedEvent.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");

        section.getChildren().addAll(header, eventTable, lblSelectedEvent);
        return section;
    }

    private VBox createMerchandiseSection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-border-color: #9b59b6; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 10;");

        Label header = new Label("Step 2: Select Merchandise");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #9b59b6;");

        merchTable = new TableView<>();
        merchTable.setPrefHeight(150);

        TableColumn<MerchandiseDisplay, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("merchandiseID"));
        idCol.setPrefWidth(60);

        TableColumn<MerchandiseDisplay, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("merchandiseName"));
        nameCol.setPrefWidth(200);

        TableColumn<MerchandiseDisplay, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("merchType"));
        typeCol.setPrefWidth(100);

        TableColumn<MerchandiseDisplay, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(120);

        TableColumn<MerchandiseDisplay, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(100);

        TableColumn<MerchandiseDisplay, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        stockCol.setPrefWidth(80);

        merchTable.getColumns().addAll(idCol, nameCol, typeCol, categoryCol, priceCol, stockCol);

        merchTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedMerchandise = newSelection;
                lblSelectedMerch.setText(String.format("Selected: %s | Price: ₱%.2f | Stock: %d",
                        newSelection.getMerchandiseName(), newSelection.getPrice(), newSelection.getStock()));
            }
        });

        lblSelectedMerch = new Label("No merchandise selected");
        lblSelectedMerch.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");

        section.getChildren().addAll(header, merchTable, lblSelectedMerch);
        return section;
    }

    private VBox createCustomerSection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-border-color: #e67e22; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 10;");

        Label header = new Label("Step 3: Select Customer");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #e67e22;");

        customerTable = new TableView<>();
        customerTable.setPrefHeight(150);

        TableColumn<Customer, Integer> idCol = new TableColumn<>("Customer ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        idCol.setPrefWidth(100);

        TableColumn<Customer, String> firstCol = new TableColumn<>("First Name");
        firstCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstCol.setPrefWidth(120);

        TableColumn<Customer, String> lastCol = new TableColumn<>("Last Name");
        lastCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastCol.setPrefWidth(120);

        TableColumn<Customer, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);

        TableColumn<Customer, Double> balanceCol = new TableColumn<>("Balance");
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        balanceCol.setPrefWidth(100);

        customerTable.getColumns().addAll(idCol, firstCol, lastCol, emailCol, balanceCol);

        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedCustomer = newSelection;
                lblSelectedCustomer.setText(String.format("Selected: %s %s | Balance: ₱%.2f",
                        newSelection.getFirstName(), newSelection.getLastName(), newSelection.getBalance()));
            }
        });

        lblSelectedCustomer = new Label("No customer selected");
        lblSelectedCustomer.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");

        section.getChildren().addAll(header, customerTable, lblSelectedCustomer);
        return section;
    }

    private VBox createTransactionSection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-border-color: #27ae60; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 10;");

        Label header = new Label("Step 4: Process Transaction");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        Label lblQty = new Label("Quantity:");
        TextField txtQuantity = new TextField("1");
        txtQuantity.setPrefWidth(100);

        Button btnPreview = new Button("Preview Transaction");
        Button btnProcess = new Button("Process Transaction");

        btnPreview.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        btnProcess.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        Label lblPreview = new Label("");
        lblPreview.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-color: #ecf0f1; -fx-border-radius: 5;");

        grid.add(lblQty, 0, 0);
        grid.add(txtQuantity, 1, 0);
        grid.add(btnPreview, 2, 0);
        grid.add(btnProcess, 3, 0);

        btnPreview.setOnAction(e -> {
            if (!validateSelection()) return;

            try {
                int quantity = Integer.parseInt(txtQuantity.getText());
                if (quantity <= 0) {
                    showWarning("Quantity must be greater than 0");
                    return;
                }

                double totalPrice = selectedMerchandise.getPrice() * quantity;

                StringBuilder preview = new StringBuilder();
                preview.append("═══════════════════════════════════════════════════\n");
                preview.append("                TRANSACTION PREVIEW\n");
                preview.append("═══════════════════════════════════════════════════\n");
                preview.append(String.format("Event: %s\n", selectedEvent.getEventName()));
                preview.append(String.format("Merchandise: %s (Type: %s)\n",
                        selectedMerchandise.getMerchandiseName(), selectedMerchandise.getMerchType()));
                preview.append(String.format("Price per item: ₱%.2f\n", selectedMerchandise.getPrice()));
                preview.append(String.format("Quantity: %d\n", quantity));
                preview.append(String.format("Total Price: ₱%.2f\n", totalPrice));
                preview.append("───────────────────────────────────────────────────\n");
                preview.append(String.format("Customer: %s %s\n",
                        selectedCustomer.getFirstName(), selectedCustomer.getLastName()));
                preview.append(String.format("Current Balance: ₱%.2f\n", selectedCustomer.getBalance()));
                preview.append(String.format("New Balance: ₱%.2f\n", selectedCustomer.getBalance() - totalPrice));
                preview.append("───────────────────────────────────────────────────\n");
                preview.append(String.format("Available Stock: %d\n", selectedMerchandise.getStock()));
                preview.append(String.format("Stock After Purchase: %d\n", selectedMerchandise.getStock() - quantity));
                preview.append("═══════════════════════════════════════════════════\n");

                boolean hasStock = selectedMerchandise.getStock() >= quantity;
                boolean hasBalance = selectedCustomer.getBalance() >= totalPrice;

                if (!hasStock) {
                    preview.append("⚠️  INSUFFICIENT STOCK!\n");
                }
                if (!hasBalance) {
                    preview.append("⚠️  INSUFFICIENT BALANCE!\n");
                }
                if (hasStock && hasBalance) {
                    preview.append("✓ Transaction ready to process\n");
                }

                lblPreview.setText(preview.toString());
            } catch (NumberFormatException ex) {
                showWarning("Please enter a valid quantity");
            }
        });

        btnProcess.setOnAction(e -> {
            if (!validateSelection()) return;

            try {
                int quantity = Integer.parseInt(txtQuantity.getText());
                if (quantity <= 0) {
                    showWarning("Quantity must be greater than 0");
                    return;
                }

                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirm Transaction");
                confirm.setHeaderText("Process Merchandise Transaction");
                confirm.setContentText(String.format(
                        "Process purchase of %d x %s for ₱%.2f?\n\nCustomer: %s %s",
                        quantity, selectedMerchandise.getMerchandiseName(),
                        selectedMerchandise.getPrice() * quantity,
                        selectedCustomer.getFirstName(), selectedCustomer.getLastName()
                ));

                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        processTransaction(quantity);
                    }
                });

            } catch (NumberFormatException ex) {
                showWarning("Please enter a valid quantity");
            }
        });

        section.getChildren().addAll(header, grid, lblPreview);
        return section;
    }

    private boolean validateSelection() {
        if (selectedEvent == null) {
            showWarning("Please select an event first");
            return false;
        }
        if (selectedMerchandise == null) {
            showWarning("Please select merchandise");
            return false;
        }
        if (selectedCustomer == null) {
            showWarning("Please select a customer");
            return false;
        }
        return true;
    }

    private void processTransaction(int quantity) {
        try {
            MerchTransactionDAO dao = new MerchTransactionDAO(connection);

            boolean success = dao.processMerchTransaction(
                    selectedCustomer.getCustomerID(),
                    selectedEvent.getEventID(),
                    selectedMerchandise.getMerchandiseID(),
                    quantity
            );

            if (success) {
                showSuccess("Transaction completed successfully!");
                loadEventMerchandise(selectedEvent.getEventID());
                loadCustomers();

                selectedMerchandise = null;
                selectedCustomer = null;
                lblSelectedMerch.setText("No merchandise selected");
                lblSelectedCustomer.setText("No customer selected");
            } else {
                showError("Transaction Failed", "The transaction could not be completed. Please check stock and balance.");
            }

        } catch (SQLException ex) {
            showError("Database Error", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void loadEvents() {
        try {
            EventDAO dao = new EventDAO(connection);
            List<Event> events = dao.viewAllEvents();
            eventTable.getItems().setAll(events);
        } catch (SQLException ex) {
            showError("Failed to load events", ex.getMessage());
        }
    }

    private void loadEventMerchandise(int eventID) {
        try {
            merchTable.getItems().clear();

            String sql = """
                SELECT m.merchandiseID, m.merchandisename, m.category, m.price, m.stock, em.merchtype
                FROM event_merch em
                JOIN merchandise m ON em.merchandiseID = m.merchandiseID
                WHERE em.eventID = ?
                """;

            java.sql.PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, eventID);
            java.sql.ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                merchTable.getItems().add(new MerchandiseDisplay(
                        rs.getInt("merchandiseID"),
                        rs.getString("merchandisename"),
                        rs.getString("merchtype"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                ));
            }

        } catch (SQLException ex) {
            showError("Failed to load merchandise", ex.getMessage());
        }
    }

    private void loadCustomers() {
        try {
            CustomerDAO dao = new CustomerDAO(connection);
            List<Customer> customers = dao.viewAllCustomers();
            customerTable.getItems().setAll(customers);
        } catch (SQLException ex) {
            showError("Failed to load customers", ex.getMessage());
        }
    }

    public BorderPane getRoot() {
        return root;
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

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class MerchandiseDisplay {
        private int merchandiseID;
        private String merchandiseName;
        private String merchType;
        private String category;
        private double price;
        private int stock;

        public MerchandiseDisplay(int merchandiseID, String merchandiseName, String merchType,
                                  String category, double price, int stock) {
            this.merchandiseID = merchandiseID;
            this.merchandiseName = merchandiseName;
            this.merchType = merchType;
            this.category = category;
            this.price = price;
            this.stock = stock;
        }

        public int getMerchandiseID() { return merchandiseID; }
        public String getMerchandiseName() { return merchandiseName; }
        public String getMerchType() { return merchType; }
        public String getCategory() { return category; }
        public double getPrice() { return price; }
        public int getStock() { return stock; }
    }
}