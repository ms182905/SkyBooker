package org.ms.skybooker.controller;

import com.jfoenix.controls.JFXButton;
import java.util.List;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import net.synedra.validatorfx.Validator;
import org.ms.skybooker.model.Passenger;
import org.ms.skybooker.repository.DatabaseManager;

public class PassengerContentController {

  private final Validator validator = new Validator();
  @FXML private TableView<Passenger> tableView;

  @FXML private TableColumn<Passenger, String> firstNameColumn;

  @FXML private TableColumn<Passenger, String> lastNameColumn;

  @FXML private TableColumn<Passenger, String> phoneColumn;
  @FXML private JFXButton deleteButton;
  @FXML private JFXButton clearButton;
  @FXML private JFXButton modifyButton;
  @FXML private JFXButton formActionButton;
  @FXML private Label actionLabel;
  @FXML private Pane passengerActionPane;
  @FXML private TextField firstNameTextField;
  @FXML private TextField lastNameTextField;
  @FXML private TextField phoneNumberTextField;
  private boolean newPassengerMode = true;

  public void initialize() {
    firstNameColumn.setCellValueFactory(data -> data.getValue().getName());
    lastNameColumn.setCellValueFactory(data -> data.getValue().getSurname());
    phoneColumn.setCellValueFactory(data -> data.getValue().getPhoneNumber());

    Task<List<Passenger>> task =
        new Task<>() {
          @Override
          protected List<Passenger> call() {
            return DatabaseManager.getPassengers();
          }
        };

    task.setOnSucceeded(
        event -> {
          List<Passenger> passengers = task.getValue();
          tableView.getItems().addAll(passengers);
        });

    Thread thread = new Thread(task);
    thread.setDaemon(true);
    thread.start();

    setButtonsDisabled(true);

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
              String text = c.get("text");
              if (text == null || text.isEmpty()) {
                c.error("First name is required.");
              }
            })
        .dependsOn("text", firstNameTextField.textProperty())
        .decorates(firstNameTextField);

    validator
        .createCheck()
        .withMethod(
            c -> {
              String text = c.get("text");
              if (text == null || text.isEmpty()) {
                c.error("Last name is required.");
              }
            })
        .dependsOn("text", lastNameTextField.textProperty())
        .decorates(lastNameTextField);

    validator
        .createCheck()
        .withMethod(
            c -> {
              String text = c.get("text");
              if (text == null || text.isEmpty()) {
                c.error("Phone number is required.");
              } else if (newPassengerMode
                  && tableView.getItems().stream()
                      .anyMatch(passenger -> text.equals(passenger.getPhoneNumber().getValue()))) {
                c.error("Phone number must be unique.");
              }
            })
        .dependsOn("text", phoneNumberTextField.textProperty())
        .decorates(phoneNumberTextField);
  }

  public void handleFlightActionButtonClicked() {
    if (validator.validate()) {
      Passenger passenger = getPassenger();

      if (newPassengerMode) {
        DatabaseManager.addPassenger(passenger);
        tableView.getItems().add(passenger);
      } else {
        int index = tableView.getSelectionModel().getSelectedIndex();
        DatabaseManager.modifyPassenger(passenger);
        tableView.getItems().set(index, passenger);
      }

      clearSelection();
      clearButton.setDisable(true);
    } else {
      clearButton.setDisable(false);
    }
  }

  private Passenger getPassenger() {
    String firstName = firstNameTextField.getText();
    String lastName = lastNameTextField.getText();
    String phoneNumber = phoneNumberTextField.getText();

    return new Passenger(firstName, lastName, phoneNumber);
  }

  private void clearFields() {
    firstNameTextField.clear();
    lastNameTextField.clear();
    phoneNumberTextField.clear();
  }

  private void setButtonsDisabled(boolean state) {
    deleteButton.setDisable(state);
    modifyButton.setDisable(state);
    clearButton.setDisable(state);
  }

  public void clearSelection() {
    clearFields();
    tableView.getSelectionModel().clearSelection();
    actionLabel.setText("Add passenger");
    newPassengerMode = true;
    passengerActionPane.setStyle("-fx-background-color: #9fd7f5; -fx-background-radius: 20;");
    setButtonsDisabled(true);
    formActionButton.setText("Add");
    formActionButton.setStyle("-fx-background-color: #1470ba; -fx-background-radius: 20;");
    firstNameTextField.setDisable(false);
    lastNameTextField.setDisable(false);
  }

  public void deleteElement() {
    Passenger selectedPassenger = tableView.getSelectionModel().getSelectedItem();
    if (selectedPassenger != null) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("Delete passenger confirmation");
      alert.setHeaderText("Are you sure you want to delete this passenger?");

      ButtonType buttonTypeOK = new ButtonType("Yes");
      ButtonType buttonTypeCancel = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
      alert.getButtonTypes().setAll(buttonTypeOK, buttonTypeCancel);

      alert
          .showAndWait()
          .ifPresent(
              response -> {
                if (response == buttonTypeOK) {
                  tableView.getItems().remove(selectedPassenger);
                  DatabaseManager.deletePassenger(selectedPassenger.getPhoneNumber().getValue());
                  clearSelection();
                }
              });
    }
  }

  public void modifyElementButtonClicked() {
    Passenger passenger = tableView.getSelectionModel().getSelectedItem();
    passengerActionPane.setStyle("-fx-background-color: #c92e2e; -fx-background-radius: 20;");
    actionLabel.setText("Modify passenger");
    newPassengerMode = false;
    firstNameTextField.setText(passenger.getName().getValue());
    lastNameTextField.setText(passenger.getSurname().getValue());
    firstNameTextField.setDisable(true);
    lastNameTextField.setDisable(true);
    formActionButton.setText("Modify");
    formActionButton.setStyle("-fx-background-color: #8f1717; -fx-background-radius: 20;");
  }
}
