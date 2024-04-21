package org.ms.skybooker.repository;

import org.ms.skybooker.model.Booking;
import org.ms.skybooker.model.Flight;
import org.ms.skybooker.model.Passenger;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DRIVER_CLASS = "org.sqlite.JDBC";
    private static final String DATABASE_URL = "jdbc:sqlite:src/main/java/org/ms/skybooker/repository/flightDatabase.db";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER_CLASS);
            return DriverManager.getConnection(DATABASE_URL);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void createDatabase() {
        try {
            Class.forName(DRIVER_CLASS);
            Connection conn = DriverManager.getConnection(DATABASE_URL);
            if (conn != null) {
                Statement statement = conn.createStatement();

                String createFlightTable = """
                        CREATE TABLE IF NOT EXISTS Flight (
                            flightNumber TEXT PRIMARY KEY,
                            startPoint TEXT NOT NULL,
                            destination TEXT NOT NULL,
                            departureDateTime TEXT NOT NULL,
                            availableSeats INTEGER NOT NULL
                        );""";
                statement.execute(createFlightTable);

                String createPassengerTable = """
                        CREATE TABLE IF NOT EXISTS Passenger (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            firstName TEXT NOT NULL,
                            lastName TEXT NOT NULL,
                            phoneNumber TEXT NOT NULL
                        );""";
                statement.execute(createPassengerTable);

                String createFlightPassengerTable = """
                        CREATE TABLE IF NOT EXISTS Booking (
                            flightNumber TEXT NOT NULL,
                            passengerId INTEGER NOT NULL,
                            FOREIGN KEY (flightNumber) REFERENCES Flight(flightNumber),
                            FOREIGN KEY (passengerId) REFERENCES Passenger(id),
                            PRIMARY KEY (flightNumber, passengerId)
                        );""";
                statement.execute(createFlightPassengerTable);

                conn.close();
                System.out.println("Database has been created.");
                //addSampleData();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addSampleData() throws ClassNotFoundException {
        try {
            Class.forName(DRIVER_CLASS);
            Connection conn = DriverManager.getConnection(DATABASE_URL);
            if (conn != null) {
                Statement statement = conn.createStatement();

                String insertFlights = "INSERT INTO Flight (flightNumber, startPoint, destination, departureDateTime, availableSeats) VALUES " + "('FL123', 'New York', 'Los Angeles', '2024-04-22 10:00', 150), " + "('FL456', 'London', 'Paris', '2024-04-23 09:30', 120), " + "('FL789', 'Tokyo', 'Sydney', '2024-04-24 12:00', 200);";
                statement.execute(insertFlights);

                String insertPassengers = "INSERT INTO Passenger (firstName, lastName, phoneNumber) VALUES " + "('John', 'Doe', '123-456-7890'), " + "('Jane', 'Smith', '987-654-3210'), " + "('Alice', 'Johnson', '555-123-4567');";
                statement.execute(insertPassengers);

                String insertBookings = "INSERT INTO Booking (flightNumber, passengerId) VALUES " + "('FL123', 1), " + "('FL123', 2), " + "('FL456', 2), " + "('FL789', 3);";
                statement.execute(insertBookings);

                conn.close();
                System.out.println("Database has been populated.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Flight> getFlights() {
        var flightsList = new ArrayList<Flight>();
        try {
            Class.forName(DRIVER_CLASS);
            Connection conn = DriverManager.getConnection(DATABASE_URL);

            if (conn != null) {
                Statement statement = conn.createStatement();

                String flightsQuery = "SELECT * FROM Flight;";
                ResultSet resultSet = statement.executeQuery(flightsQuery);

                while (resultSet.next()) {
                    String flightNumber = resultSet.getString("flightNumber");
                    String startPoint = resultSet.getString("startPoint");
                    String destination = resultSet.getString("destination");
                    String departureDateTime = resultSet.getString("departureDateTime");
                    int availableSeats = resultSet.getInt("availableSeats");

                    Statement takenSeatsStatement = conn.createStatement();
                    String takenSeatsQuery = "SELECT COUNT(*) FROM Booking WHERE flightNumber = '" + flightNumber + "';";
                    ResultSet takenSeatsResultSet = takenSeatsStatement.executeQuery(takenSeatsQuery);

                    int takenSeats = 0;
                    if (takenSeatsResultSet.next()) {
                        takenSeats = takenSeatsResultSet.getInt(1);
                    }

                    Flight flight = new Flight(flightNumber, startPoint, destination, departureDateTime, availableSeats, availableSeats - takenSeats);
                    flightsList.add(flight);
                }

                conn.close();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        System.out.println(flightsList);
        return flightsList;
    }

    public static List<Passenger> getPassengers() {
        var passengerList = new ArrayList<Passenger>();

        try {
            Class.forName(DRIVER_CLASS);
            Connection conn = DriverManager.getConnection(DATABASE_URL);

            if (conn != null) {
                Statement statement = conn.createStatement();
                String passengersQuery = "SELECT * from Passenger;";

                ResultSet resultSet =  statement.executeQuery(passengersQuery);

                while (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");
                    String phoneNumber = resultSet.getString("phoneNumber");

                    passengerList.add(new Passenger(id, firstName, lastName, phoneNumber));
                }

                conn.close();
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return passengerList;
    }

    public static List<Booking> getBookings() {
        var bookingList = new ArrayList<Booking>();

        try {
            Class.forName(DRIVER_CLASS);
            Connection conn = DriverManager.getConnection(DATABASE_URL);

            if (conn != null) {
                Statement statement = conn.createStatement();
                String bookingsQuery = """
                        SELECT Booking.flightNumber, Booking.passengerId,
                        Flight.startPoint, Flight.destination, Flight.departureDateTime,
                        Passenger.firstName, Passenger.lastName, Passenger.phoneNumber
                        FROM Booking
                        LEFT JOIN Passenger
                        ON Booking.passengerId = Passenger.id
                        LEFT JOIN Flight
                        ON Booking.flightNumber = Flight.flightNumber;""";

                ResultSet resultSet =  statement.executeQuery(bookingsQuery);

                while (resultSet.next()) {
                    int passengerId = resultSet.getInt(2);
                    String passenger = resultSet.getString("firstName") + " " + resultSet.getString("lastName");
                    String phoneNumber = resultSet.getString("phoneNumber");
                    String flightNumber = resultSet.getString("flightNumber");
                    String route = resultSet.getString("startPoint") + " - " + resultSet.getString("destination");
                    String date = resultSet.getString("departureDateTime");

                    System.out.println(passenger);
                    bookingList.add(new Booking(passengerId, passenger, phoneNumber, flightNumber, route, date));
                }

                conn.close();
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return bookingList;
    }

    public static void addFlight(Flight flight) {
        String addFlightQuery = "INSERT INTO Flight (flightNumber, startPoint, destination, departureDateTime, availableSeats) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(addFlightQuery)) {

            pstmt.setString(1, flight.getFlightNumber().getValue());
            pstmt.setString(2, flight.getStartPoint().getValue());
            pstmt.setString(3, flight.getDestination().getValue());
            pstmt.setObject(4, flight.getDepartureDateTime().getValue());
            pstmt.setInt(5, flight.getAvailableSeats().getValue());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFlight(String flightNumber) {
        String deleteFlightQuery = "DELETE FROM Flight WHERE flightNumber = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(deleteFlightQuery)) {
            pstmt.setString(1, flightNumber);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void modifyFlight(Flight flight) {
        String modifyFlightQuery = "UPDATE Flight SET startPoint = ?, destination = ?, departureDateTime = ?, availableSeats = ? WHERE flightNumber = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(modifyFlightQuery)) {

            pstmt.setString(1, flight.getStartPoint().getValue());
            pstmt.setString(2, flight.getDestination().getValue());
            pstmt.setObject(3, flight.getDepartureDateTime().getValue());
            pstmt.setInt(4, flight.getAvailableSeats().getValue());
            pstmt.setString(5, flight.getFlightNumber().getValue());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

