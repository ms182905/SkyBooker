package org.ms.skybooker.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.ms.skybooker.model.Booking;
import org.ms.skybooker.model.Flight;
import org.ms.skybooker.model.Passenger;
import org.ms.skybooker.repository.DatabaseManager;

import java.time.LocalDateTime;
import java.util.List;

public class PassengerContentController {

    @FXML
    private TableView<Passenger> tableView;

    @FXML
    private TableColumn<Passenger, String> firstNameColumn;

    @FXML
    private TableColumn<Passenger, String> lastNameColumn;

    @FXML
    private TableColumn<Passenger, String> phoneColumn;

    public void initialize() {
        firstNameColumn.setCellValueFactory(data -> data.getValue().getName());
        lastNameColumn.setCellValueFactory(data -> data.getValue().getSurname());
        phoneColumn.setCellValueFactory(data -> data.getValue().getPhoneNumber());

        Task<List<Passenger>> task = new Task<>() {
            @Override
            protected List<Passenger> call() {
                return DatabaseManager.getPassengers();
            }
        };

        task.setOnSucceeded(event -> {
            List<Passenger> passengers = task.getValue();
            tableView.getItems().addAll(passengers);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

}
