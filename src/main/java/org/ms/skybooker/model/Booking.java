package org.ms.skybooker.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;

@Data
public class Booking {
  private final int passengerId;
  private final StringProperty passenger;
  private final StringProperty phoneNumber;
  private final StringProperty flightNumber;
  private final StringProperty route;
  private final StringProperty date;

  public Booking(
      int passengerId,
      String passenger,
      String phoneNumber,
      String flightNumber,
      String route,
      String date) {
    this.passengerId = passengerId;
    this.passenger = new SimpleStringProperty(passenger);
    this.phoneNumber = new SimpleStringProperty(phoneNumber);
    this.flightNumber = new SimpleStringProperty(flightNumber);
    this.route = new SimpleStringProperty(route);
    this.date = new SimpleStringProperty(date);
  }
}
