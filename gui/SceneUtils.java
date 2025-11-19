package gui;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.sql.Connection;

public class SceneUtils {

    public static HBox createBackButton(MainMenuScene mainMenu, Connection connection) {
        Button backBtn = new Button("← Back to Main Menu");
        backBtn.setMinWidth(180);
        backBtn.setStyle("-fx-font-size: 12px; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");

        backBtn.setOnMouseEntered(e -> {
            backBtn.setStyle("-fx-font-size: 12px; -fx-background-color: #c0392b; -fx-text-fill: white; -fx-cursor: hand;");
        });

        backBtn.setOnMouseExited(e -> {
            backBtn.setStyle("-fx-font-size: 12px; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
        });

        backBtn.setOnAction(e -> {
            mainMenu.showMainMenu();
        });

        HBox box = new HBox(backBtn);
        box.setAlignment(Pos.TOP_LEFT);
        box.setPadding(new Insets(10, 10, 10, 10));

        return box;
    }
}