package org.ms.skybooker.controller;

import com.jfoenix.controls.JFXButton;
import java.util.List;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Pair;
import net.synedra.validatorfx.Validator;
import org.controlsfx.control.SearchableComboBox;
import org.ms.skybooker.model.Booking;
import org.ms.skybooker.model.Flight;
import org.ms.skybooker.model.Passenger;
import org.ms.skybooker.repository.DatabaseManager;

public class BookingContentController {

  private final Validator validator = new Validator();
  @FXML private TableView<Booking> tableView;
  @FXML private TableColumn<Booking, String> passengerColumn;
  @FXML private TableColumn<Booking, String> phoneColumn;
  @FXML private TableColumn<Booking, String> flightNumberColumn;
  @FXML private TableColumn<Booking, String> routeColumn;
  @FXML private TableColumn<Booking, String> dateColumn;
  @FXML private SearchableComboBox<String> passengerComboBox;
  @FXML private SearchableComboBox<String> flightComboBox;
  @FXML private JFXButton deleteButton;
  @FXML private JFXButton clearButton;

  public void initialize() {
    passengerColumn.setCellValueFactory(data -> data.getValue().getPassenger());
    phoneColumn.setCellValueFactory(data -> data.getValue().getPhoneNumber());
    flightNumberColumn.setCellValueFactory(data -> data.getValue().getFlightNumber());
    routeColumn.setCellValueFactory(data -> data.getValue().getRoute());
    dateColumn.setCellValueFactory(data -> data.getValue().getDate());

    setButtonsDisabled(true);

    Thread getDataThread = getDataThread();
    getDataThread.start();

    tableView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, oldSelection, newSelection) -> {
              if (newSelection != null) {
                setButtonsDisabled(false);
              }
            });

    validator
        .createCheck()
        .withMethod(
            c -> {
              String selectedPassenger = c.get("selectedPassenger");
              if (selectedPassenger == null || selectedPassenger.isEmpty()) {
                c.error("Passenger is required.");
              } else if (!flightComboBox.getValue().isEmpty()) {
                int colonIndex = selectedPassenger.indexOf(":");
                String phoneNumber = selectedPassenger.substring(colonIndex + 2);

                String flightComboBoxValue = flightComboBox.getValue();
                int firstSpaceIndex = flightComboBoxValue.indexOf(" ");
                String flightNumber = flightComboBoxValue.substring(0, firstSpaceIndex);

                if (tableView.getItems().stream()
                    .anyMatch(
                        e ->
                            e.getFlightNumber().getValue().equals(flightNumber)
                                && e.getPhoneNumber().getValue().equals(phoneNumber))) {
                  c.error("Selected passenger has booked this flight");
                }
              }
            })
        .dependsOn("selectedPassenger", passengerComboBox.valueProperty())
        .decorates(passengerComboBox);

    validator
        .createCheck()
        .withMethod(
            c -> {
              String selectedFlight = c.get("selectedFlight");
              if (selectedFlight == null || selectedFlight.isEmpty()) {
                c.error("Flight is required.");
              } else {
                String flightComboBoxValue = flightComboBox.getValue();
                int firstSpaceIndex = flightComboBoxValue.indexOf(" ");
                String flightNumber = flightComboBoxValue.substring(0, firstSpaceIndex);
                Flight flight = DatabaseManager.getFlight(flightNumber);

                if (flight.getFreeSeats().getValue() < 1) {
                  c.error("No free seats in this flight");
                }
              }
            })
        .dependsOn("selectedFlight", flightComboBox.valueProperty())
        .decorates(flightComboBox);
  }

  private Thread getDataThread() {
    Task<Pair<Pair<List<Booking>, List<Passenger>>, List<Flight>>> getDataTask =
        new Task<>() {
          @Override
          protected Pair<Pair<List<Booking>, List<Passenger>>, List<Flight>> call() {
            List<Booking> bookings = DatabaseManager.getBookings();
            List<Passenger> passengers = DatabaseManager.getPassengers();
            List<Flight> flights = DatabaseManager.getFlights();
            return new Pair<>(new Pair<>(bookings, passengers), flights);
          }
        };

    getDataTask.setOnSucceeded(
        event -> {
          Pair<Pair<List<Booking>, List<Passenger>>, List<Flight>> result = getDataTask.getValue();
          Pair<List<Booking>, List<Passenger>> bookingsAndPassengers = result.getKey();
          List<Booking> bookings = bookingsAndPassengers.getKey();
          List<Passenger> passengers = bookingsAndPassengers.getValue();
          List<Flight> flights = result.getValue();

          tableView.getItems().addAll(bookings);

          var passengerComboBoxOptions =
              passengers.stream()
                  .map(
                      passenger ->
                          passenger.getName().getValue()
                              + " "
                              + passenger.getSurname().getValue()
                              + ": "
                              + passenger.getPhoneNumber().getValue())
                  .toList();
          passengerComboBox.getItems().addAll(passengerComboBoxOptions);

          var flightComboBoxOptions =
              flights.stream()
                  .map(
                      flight ->
                          flight.getFlightNumber().getValue()
                              + " "
                              + flight.getStartPoint().getValue()
                              + " - "
                              + flight.getDestination().getValue()
                              + " - "
                              + flight.getDepartureDateTime().getValue())
                  .toList();
          flightComboBox.getItems().addAll(flightComboBoxOptions);
        });

    Thread getDataThread = new Thread(getDataTask);
    getDataThread.setDaemon(true);
    return getDataThread;
  }

  public void handleFlightActionButtonClicked() {
    if (validator.validate()) {
      Booking booking = getBooking();
      tableView.getItems().add(booking);
      DatabaseManager.addBooking(booking);
      clearSelection();
      clearButton.setDisable(true);
    } else {
      clearButton.setDisable(false);
    }
  }

  private Booking getBooking() {
    String flightComboBoxValue = flightComboBox.getValue();
    int firstSpaceIndex = flightComboBoxValue.indexOf(" ");
    String flightNumber = flightComboBoxValue.substring(0, firstSpaceIndex);

    String passengerComboBoxValue = passengerComboBox.getValue();
    int colonIndex = passengerComboBoxValue.indexOf(":");
    String phoneNumber = passengerComboBoxValue.substring(colonIndex + 2);

    Flight flight = DatabaseManager.getFlight(flightNumber);
    Passenger passenger = DatabaseManager.getPassenger(phoneNumber);
    int passengerId = DatabaseManager.getPassengerId(phoneNumber);

    return new Booking(
        passengerId,
        passenger.getName().get() + " " + passenger.getSurname().getValue(),
        passenger.getPhoneNumber().getValue(),
        flightNumber,
        flight.getStartPoint().getValue() + " - " + flight.getDestination().getValue(),
        flight.getDepartureDateTime().getValue());
  }

  private void setButtonsDisabled(boolean state) {
    deleteButton.setDisable(state);
    clearButton.setDisable(state);
  }

  public void clearSelection() {
    flightComboBox.setValue("");
    passengerComboBox.setValue("");
    tableView.getSelectionModel().clearSelection();
    setButtonsDisabled(true);
  }

  public void deleteElement() {
    Booking selectedBooking = tableView.getSelectionModel().getSelectedItem();
    if (selectedBooking != null) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("Delete booking confirmation");
      alert.setHeaderText("Are you sure you want to delete this booking?");

      ButtonType buttonTypeOK = new ButtonType("Yes");
      ButtonType buttonTypeCancel = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
      alert.getButtonTypes().setAll(buttonTypeOK, buttonTypeCancel);

      alert
          .showAndWait()
          .ifPresent(
              response -> {
                if (response == buttonTypeOK) {
                  tableView.getItems().remove(selectedBooking);
                  DatabaseManager.deleteBooking(
                      selectedBooking.getFlightNumber().getValue(),
                      selectedBooking.getPassengerId());
                  clearSelection();
                }
              });
    }
  }
}
