package org.ms.skybooker.controller;

import java.util.List;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.ms.skybooker.model.Booking;
import org.ms.skybooker.repository.DatabaseManager;

public class BookingContentController {

    @FXML
    private TableView<Booking> tableView;

    @FXML
    private TableColumn<Booking, String> passengerColumn;

    @FXML
    private TableColumn<Booking, String> phoneColumn;

    @FXML
    private TableColumn<Booking, String> flightNumberColumn;

    @FXML
    private TableColumn<Booking, String> routeColumn;

    @FXML
    private TableColumn<Booking, String> dateColumn;


    public void initialize() {
        passengerColumn.setCellValueFactory(data -> data.getValue().getPassenger());
        phoneColumn.setCellValueFactory(data -> data.getValue().getPhoneNumber());
        flightNumberColumn.setCellValueFactory(data -> data.getValue().getFlightNumber());
        routeColumn.setCellValueFactory(data -> data.getValue().getRoute());
        dateColumn.setCellValueFactory(data -> data.getValue().getDate());

        Task<List<Booking>> task = new Task<>() {
            @Override
            protected List<Booking> call() {
                return DatabaseManager.getBookings();
            }
        };

        task.setOnSucceeded(event -> {
            List<Booking> bookings = task.getValue();
            tableView.getItems().addAll(bookings);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

}
