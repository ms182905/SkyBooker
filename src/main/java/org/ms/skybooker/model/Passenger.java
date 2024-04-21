package org.ms.skybooker.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;

@Data
public class Passenger {
    private final StringProperty name;
    private final StringProperty surname;
    private final StringProperty phoneNumber;

    public Passenger(String name, String surname, String phoneNumber) {
        this.name = new SimpleStringProperty(name);
        this.surname = new SimpleStringProperty(surname);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
    }
}
