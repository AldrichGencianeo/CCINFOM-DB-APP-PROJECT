package gui;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.sql.Connection;

public class SceneUtils {

    public static HBox createBackButton(MainMenuScene mainMenu, Connection connection) {
        Button backBtn = new Button("Back to Main Menu");
        backBtn.setMinWidth(180);

        backBtn.setOnAction(e -> {
            // Replace root with the main menu
            mainMenu.getRoot().getScene().setRoot(mainMenu.getRoot());
        });

        HBox box = new HBox(backBtn);
        box.setAlignment(Pos.TOP_LEFT);
        box.setPadding(new Insets(10, 10, 10, 10));

        return box;
    }
}