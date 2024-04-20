package org.ms.skybooker.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    AnchorPane contentAnchorPane;

    public void switchToFlightScene(MouseEvent event) throws IOException {
        Node node;
        node = (Node)FXMLLoader.load(getClass().getResource("/org/ms/skybooker/FlightContent.fxml"));
        contentAnchorPane.getChildren().setAll(node);
    }

    public void switchToPassengerScene(MouseEvent event) throws IOException {
        Node node;
        node = (Node)FXMLLoader.load(getClass().getResource("/org/ms/skybooker/PassengerContent.fxml"));
        contentAnchorPane.getChildren().setAll(node);
    }

    public void switchToBookingScene(MouseEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("BookingView.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void exitProgram(MouseEvent event) throws IOException {
        Platform.exit();
        System.exit(0);
    }
}
