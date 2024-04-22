package org.ms.skybooker;

import java.io.IOException;
import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.ms.skybooker.repository.DatabaseManager;

public class Main extends Application {
  public static void main(String[] args) {
    launch();
  }

  @Override
  public void start(Stage stage) throws IOException {
    String databaseUrl = "jdbc:sqlite:src/main/resources/flightDatabase.db";
    DatabaseManager.createDatabase(databaseUrl);

    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("MainView.fxml")));
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }
}
