package org.ms.skybooker.controller;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.util.Objects;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class SceneController {
  @FXML AnchorPane contentAnchorPane;
  @FXML JFXButton flightsButton;
  @FXML JFXButton passengersButton;
  @FXML JFXButton bookingsButton;

  @FXML
  public void initialize() {
    try {
      FXMLLoader loader =
          new FXMLLoader(getClass().getResource("/org/ms/skybooker/FlightContent.fxml"));
      AnchorPane flightContent = loader.load();
      contentAnchorPane.getChildren().setAll(flightContent);

      flightsButton.setStyle("-fx-background-color: brown;");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void switchToFlightScene() throws IOException {
    Node node;
    node =
        FXMLLoader.load(
            Objects.requireNonNull(getClass().getResource("/org/ms/skybooker/FlightContent.fxml")));
    contentAnchorPane.getChildren().setAll(node);

    flightsButton.setStyle("-fx-background-color: brown");
    passengersButton.setStyle("-fx-background-color: transparent;");
    bookingsButton.setStyle("-fx-background-color: transparent;");
  }

  public void switchToPassengerScene() throws IOException {
    Node node;
    node =
        FXMLLoader.load(
            Objects.requireNonNull(
                getClass().getResource("/org/ms/skybooker/PassengerContent.fxml")));
    contentAnchorPane.getChildren().setAll(node);

    flightsButton.setStyle("-fx-background-color: transparent;");
    passengersButton.setStyle("-fx-background-color: brown;");
    bookingsButton.setStyle("-fx-background-color: transparent;");
  }

  public void switchToBookingScene() throws IOException {
    Node node;
    node =
        FXMLLoader.load(
            Objects.requireNonNull(
                getClass().getResource("/org/ms/skybooker/BookingContent.fxml")));
    contentAnchorPane.getChildren().setAll(node);

    flightsButton.setStyle("-fx-background-color: transparent;");
    passengersButton.setStyle("-fx-background-color: transparent;");
    bookingsButton.setStyle("-fx-background-color: brown;");
  }

  public void exitProgram() {
    Platform.exit();
    System.exit(0);
  }
}
