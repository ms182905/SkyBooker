package org.ms.skybooker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.ms.skybooker.repository.DatabaseManager;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("MainView.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        DatabaseManager.createDatabase();
    }

    public static void main(String[] args) {
        launch();
    }
}