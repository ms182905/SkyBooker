package org.ms.skybooker.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import net.synedra.validatorfx.Validator;
import org.ms.skybooker.model.Flight;
import org.ms.skybooker.repository.DatabaseManager;

public class FlightContentController {

    private final Validator validator = new Validator();
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
    @FXML
    private Pane flightActionPane;

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
                    } else if (Objects.equals(actionLabel.getText(), "Add flight")) {
                        for (Flight flight : tableView.getItems()) {
                            if (text.equals(flight.getFlightNumber().getValue())) {
                                c.error("Flight number must be unique.");
                                break;
                            }
                        }
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

    public void handleFlightActionButtonClicked() {
        if (validator.validate()) {
            Flight flight = getFlight();

            if (Objects.equals(actionLabel.getText(), "Add flight")) {
                DatabaseManager.addFlight(flight);
                tableView.getItems().add(flight);
            } else {
                int index = tableView.getSelectionModel().getSelectedIndex();
                DatabaseManager.modifyFlight(flight);
                tableView.getItems().set(index, flight);
            }

            clearSelection();
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
        totalSeatsSpinner.setDisable(false);
        totalSeatsSpinner.getValueFactory().setValue(200);
    }

    private void setButtonsDisabled(boolean state) {
        deleteButton.setDisable(state);
        modifyButton.setDisable(state);
        clearButton.setDisable(state);
    }

    public void clearSelection() {
        clearFields();
        tableView.getSelectionModel().clearSelection();
        actionLabel.setText("Add flight");
        flightActionPane.setStyle("-fx-background-color: #9fd7f5;");
        setButtonsDisabled(true);
        formActionButton.setText("Add");
        formActionButton.setStyle("-fx-background-color: #1470ba;");
        flightNumberTextField.setDisable(false);
    }

    public void deleteElement() {
        Flight selectedFlight = tableView.getSelectionModel().getSelectedItem();
        if (selectedFlight != null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Delete flight confirmation");
            alert.setHeaderText("Are you sure you want to delete this flight?");

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

    public void modifyElementButtonClicked() {
        Flight selectedFlight = tableView.getSelectionModel().getSelectedItem();
        flightActionPane.setStyle("-fx-background-color: #c92e2e;");
        actionLabel.setText("Modify flight");
        flightNumberTextField.setText(selectedFlight.getFlightNumber().getValue());
        flightNumberTextField.setDisable(true);
        fromTextField.setText(selectedFlight.getStartPoint().getValue());
        toTextField.setText(selectedFlight.getDestination().getValue());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(selectedFlight.getDepartureDateTime().getValue(), formatter);

        datePicker.getEditor().setText(dateTime.getDayOfMonth() + "." + dateTime.getMonthValue() + "." + dateTime.getYear());
        hourSpinner.getValueFactory().setValue(dateTime.getHour());
        minuteSpinner.getValueFactory().setValue(dateTime.getMinute());
        totalSeatsSpinner.getValueFactory().setValue(selectedFlight.getAvailableSeats().getValue());
        totalSeatsSpinner.setDisable(true);
        formActionButton.setText("Modify");
        formActionButton.setStyle("-fx-background-color: #8f1717;");
    }
}
