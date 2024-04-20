package org.ms.skybooker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.ms.skybooker.persistence.DatabaseManager;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FlightView.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        DatabaseManager.createDatabase();
    }

    public static void main(String[] args) {
        launch();
    }
}