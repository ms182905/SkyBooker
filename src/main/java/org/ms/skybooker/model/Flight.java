package org.ms.skybooker.model;

import javafx.beans.property.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class Flight {
    private final StringProperty flightNumber;
    private final StringProperty startPoint;
    private final StringProperty destination;
    private final StringProperty departureDateTime;
    private final IntegerProperty availableSeats;
    private final IntegerProperty freeSeats;

    public Flight(String flightNumber, String startPoint, String destination, String departureDateTime, int availableSeats, int freeSeats) {
        this.flightNumber = new SimpleStringProperty(flightNumber);
        this.startPoint = new SimpleStringProperty(startPoint);
        this.destination = new SimpleStringProperty(destination);
        this.departureDateTime = new SimpleStringProperty(departureDateTime);
        this.availableSeats = new SimpleIntegerProperty(availableSeats);
        this.freeSeats = new SimpleIntegerProperty(freeSeats);
    }
}
