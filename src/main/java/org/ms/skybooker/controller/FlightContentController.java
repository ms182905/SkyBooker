package org.ms.skybooker.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import net.synedra.validatorfx.Validator;
import org.ms.skybooker.model.Flight;
import org.ms.skybooker.repository.DatabaseManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FlightContentController {

    @FXML
    private TableView<Flight> tableView;

    @FXML
    private TableColumn<Flight, String> flightNumberColumn;

    @FXML
    private TableColumn<Flight, String> fromColumn;

    @FXML
    private TableColumn<Flight, String> toColumn;

    @FXML
    private TableColumn<Flight, String> dateColumn;

    @FXML
    private TableColumn<Flight, Integer> totalSeatsColumn;

    @FXML
    private TableColumn<Flight, Integer> freeSeatsColumn;

    @FXML
    private TextField flightNumberTextField;
    @FXML
    private TextField fromTextField;
    @FXML
    private TextField toTextField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Spinner<Integer> hourSpinner;

    @FXML
    private Spinner<Integer> minuteSpinner;

    @FXML
    private Spinner<Integer> totalSeatsSpinner;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button modifyButton;
    @FXML
    private Button formActionButton;
    @FXML
    private Label actionLabel;

    private final Validator validator = new Validator();

    public void initialize() {
        flightNumberColumn.setCellValueFactory(data -> data.getValue().getFlightNumber());
        fromColumn.setCellValueFactory(data -> data.getValue().getStartPoint());
        toColumn.setCellValueFactory(data -> data.getValue().getDestination());
        dateColumn.setCellValueFactory(data -> data.getValue().getDepartureDateTime());
        totalSeatsColumn.setCellValueFactory(data -> data.getValue().getAvailableSeats().asObject());
        freeSeatsColumn.setCellValueFactory(data -> data.getValue().getFreeSeats().asObject());

        Task<List<Flight>> task = new Task<>() {
            @Override
            protected List<Flight> call() {
                return DatabaseManager.getFlights();
            }
        };

        task.setOnSucceeded(event -> {
            List<Flight> flights = task.getValue();
            tableView.getItems().addAll(flights);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        SpinnerValueFactory<Integer> hourSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23);
        hourSpinnerValueFactory.setValue(12);
        hourSpinner.setValueFactory(hourSpinnerValueFactory);

        SpinnerValueFactory<Integer> minuteSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59);
        minuteSpinnerValueFactory.setValue(30);
        minuteSpinner.setValueFactory(minuteSpinnerValueFactory);

        SpinnerValueFactory<Integer> totalSeatsSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 800);
        totalSeatsSpinnerValueFactory.setValue(200);
        totalSeatsSpinner.setValueFactory(totalSeatsSpinnerValueFactory);

        setButtonsDisabled(true);

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                setButtonsDisabled(false);
            }
        });

        validator.createCheck()
                .withMethod(c -> {
                    String text = c.get("text");
                    if (text == null || text.isEmpty()) {
                        c.error("Flight number is required.");
                    }
                })
                .dependsOn("text", flightNumberTextField.textProperty())
                .decorates(flightNumberTextField);

        validator.createCheck()
                .withMethod(c -> {
                    String text = c.get("text");
                    if (text == null || text.isEmpty()) {
                        c.error("Start point is required.");
                    }
                })
                .dependsOn("text", fromTextField.textProperty())
                .decorates(fromTextField);

        validator.createCheck()
                .withMethod(c -> {
                    String text = c.get("text");
                    if (text == null || text.isEmpty()) {
                        c.error("Destination is required.");
                    }
                })
                .dependsOn("text", toTextField.textProperty())
                .decorates(toTextField);

        validator.createCheck()
                .withMethod(c -> {
                    String text = c.get("text");
                    if (text == null || text.isEmpty()) {
                        c.error("Flight date is required.");
                    }
                })
                .dependsOn("text", datePicker.getEditor().textProperty())
                .decorates(datePicker);
    }

    public void handleAddFlightButtonClicked(MouseEvent event) {
        if (validator.validate()) {
            Flight newFlight = getFlight();
            DatabaseManager.addFlight(newFlight);
            tableView.getItems().add(newFlight);

            clearFields();
        }
    }

    private Flight getFlight() {
        String flightNumber = flightNumberTextField.getText();
        String startPoint = fromTextField.getText();
        String destination = toTextField.getText();
        LocalDate date = datePicker.getValue();
        int hour = hourSpinner.getValue();
        int minute = minuteSpinner.getValue();
        String departureDateTime = LocalDateTime.of(date, LocalTime.of(hour, minute, 0)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        int totalSeats = totalSeatsSpinner.getValue();

        return new Flight(flightNumber, startPoint, destination, departureDateTime, totalSeats, totalSeats);
    }

    private void clearFields() {
        flightNumberTextField.clear();
        fromTextField.clear();
        toTextField.clear();
        datePicker.getEditor().clear();
        hourSpinner.getValueFactory().setValue(12);
        minuteSpinner.getValueFactory().setValue(30);
        totalSeatsSpinner.getValueFactory().setValue(200);
    }

    private void setButtonsDisabled(boolean state) {
        deleteButton.setDisable(state);
        modifyButton.setDisable(state);
        clearButton.setDisable(state);
    }

    public void clearSelection() {
        tableView.getSelectionModel().clearSelection();
        actionLabel.setText("Add flight");
        setButtonsDisabled(true);
    }

    public void deleteElement() {
        Flight selectedFlight = tableView.getSelectionModel().getSelectedItem();
        if (selectedFlight != null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Delete flight confirmation");
            alert.setHeaderText("Are you sure you want to delete this item?");

            ButtonType buttonTypeOK = new ButtonType("Yes");
            ButtonType buttonTypeCancel = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(buttonTypeOK, buttonTypeCancel);

            alert.showAndWait().ifPresent(response -> {
                if (response == buttonTypeOK) {
                    tableView.getItems().remove(selectedFlight);
                    DatabaseManager.deleteFlight(selectedFlight.getFlightNumber().getValue());
                    clearSelection();
                }
            });
        }
    }
}
