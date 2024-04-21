package org.ms.skybooker.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import com.jfoenix.controls.JFXButton;

import java.io.IOException;
import java.util.Objects;

public class SceneController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    AnchorPane contentAnchorPane;
    @FXML
    JFXButton flightsButton;
    @FXML
    JFXButton passengersButton;
    @FXML
    JFXButton bookingsButton;

    @FXML
    public void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/ms/skybooker/FlightContent.fxml"));
            AnchorPane flightContent = loader.load();
            contentAnchorPane.getChildren().setAll(flightContent);

            flightsButton.setStyle("-fx-background-color: brown;");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchToFlightScene(MouseEvent event) throws IOException {
        Node node;
        node = (Node)FXMLLoader.load(getClass().getResource("/org/ms/skybooker/FlightContent.fxml"));
        contentAnchorPane.getChildren().setAll(node);

        flightsButton.setStyle("-fx-background-color: brown;");
        passengersButton.setStyle("-fx-background-color: transparent;");
        bookingsButton.setStyle("-fx-background-color: transparent;");
    }

    public void switchToPassengerScene(MouseEvent event) throws IOException {
        Node node;
        node = (Node)FXMLLoader.load(getClass().getResource("/org/ms/skybooker/PassengerContent.fxml"));
        contentAnchorPane.getChildren().setAll(node);

        flightsButton.setStyle("-fx-background-color: transparent;");
        passengersButton.setStyle("-fx-background-color: brown;");
        bookingsButton.setStyle("-fx-background-color: transparent;");
    }

    public void switchToBookingScene(MouseEvent event) throws IOException {
        Node node;
        node = (Node)FXMLLoader.load(getClass().getResource("/org/ms/skybooker/BookingContent.fxml"));
        contentAnchorPane.getChildren().setAll(node);

        flightsButton.setStyle("-fx-background-color: transparent;");
        passengersButton.setStyle("-fx-background-color: transparent;");
        bookingsButton.setStyle("-fx-background-color: brown;");
    }

    public void exitProgram(MouseEvent event) throws IOException {
        Platform.exit();
        System.exit(0);
    }
}
