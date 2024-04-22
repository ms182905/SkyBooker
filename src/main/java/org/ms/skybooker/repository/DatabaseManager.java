package org.ms.skybooker.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.ms.skybooker.model.Booking;
import org.ms.skybooker.model.Flight;
import org.ms.skybooker.model.Passenger;

public class DatabaseManager {
  private static final String DRIVER_CLASS = "org.sqlite.JDBC";
  private static String DATABASE_URL;

  public static Connection getConnection() throws SQLException {
    try {
      Class.forName(DRIVER_CLASS);
      return DriverManager.getConnection(DATABASE_URL);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void createDatabase(String databaseURL) {
    DATABASE_URL = databaseURL;
    try {
      Connection conn = getConnection();
      if (conn != null) {
        Statement statement = conn.createStatement();

        String createFlightTable =
            """
              CREATE TABLE IF NOT EXISTS Flight (
                  flightNumber TEXT PRIMARY KEY,
                  startPoint TEXT NOT NULL,
                  destination TEXT NOT NULL,
                  departureDateTime TEXT NOT NULL,
                  availableSeats INTEGER NOT NULL);
            """;
        statement.execute(createFlightTable);

        String createPassengerTable =
            """
              CREATE TABLE IF NOT EXISTS Passenger (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              firstName TEXT NOT NULL,
              lastName TEXT NOT NULL,
              phoneNumber TEXT NOT NULL);
            """;
        statement.execute(createPassengerTable);

        String createBookingsTable =
            """
              CREATE TABLE IF NOT EXISTS Booking (
              flightNumber TEXT NOT NULL,
              passengerId INTEGER NOT NULL,
              FOREIGN KEY (flightNumber) REFERENCES Flight(flightNumber) ON DELETE CASCADE,
              FOREIGN KEY (passengerId) REFERENCES Passenger(id) ON DELETE CASCADE,
              PRIMARY KEY (flightNumber, passengerId));
            """;
        statement.execute(createBookingsTable);

        statement.close();
        conn.close();
        // addSampleData();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void addSampleData() {
    try {
      Class.forName(DRIVER_CLASS);
      Connection conn = DriverManager.getConnection(DATABASE_URL);
      if (conn != null) {
        Statement statement = conn.createStatement();

        String insertFlights =
            """
              INSERT INTO Flight (flightNumber, startPoint, destination, departureDateTime, availableSeats) VALUES
              ('FL123', 'New York', 'Los Angeles', '2024-04-22 10:00', 150),
              ('FL456', 'London', 'Paris', '2024-04-23 09:30', 120),
              ('FL789', 'Tokyo', 'Sydney', '2024-04-24 12:00', 200);
            """;
        statement.execute(insertFlights);

        String insertPassengers =
            """
              INSERT INTO Passenger (firstName, lastName, phoneNumber) VALUES
              ('John', 'Doe', '123-456-7890'),
              ('Jane', 'Smith', '987-654-3210'),
              ('Alice', 'Johnson', '555-123-4567');
            """;
        statement.execute(insertPassengers);

        String insertBookings =
            """
              INSERT INTO Booking (flightNumber, passengerId) VALUES
              ('FL123', 1),
              ('FL123', 2),
              ('FL456', 2),
              ('FL789', 3);
            """;

        statement.execute(insertBookings);

        statement.close();
        conn.close();
      }
    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    }
  }

  public static List<Flight> getFlights() {
    var flightsList = new ArrayList<Flight>();
    try {
      Connection conn = getConnection();

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
          String takenSeatsQuery =
              "SELECT COUNT(*) FROM Booking WHERE flightNumber = '" + flightNumber + "';";
          ResultSet takenSeatsResultSet = takenSeatsStatement.executeQuery(takenSeatsQuery);

          int takenSeats = 0;
          if (takenSeatsResultSet.next()) {
            takenSeats = takenSeatsResultSet.getInt(1);
          }

          Flight flight =
              new Flight(
                  flightNumber,
                  startPoint,
                  destination,
                  departureDateTime,
                  availableSeats,
                  availableSeats - takenSeats);
          flightsList.add(flight);
        }

        statement.close();
        conn.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return flightsList;
  }

  public static List<Passenger> getPassengers() {
    var passengerList = new ArrayList<Passenger>();

    try {
      Connection conn = getConnection();

      if (conn != null) {
        Statement statement = conn.createStatement();
        String passengersQuery = "SELECT * from Passenger;";

        ResultSet resultSet = statement.executeQuery(passengersQuery);

        while (resultSet.next()) {
          String firstName = resultSet.getString("firstName");
          String lastName = resultSet.getString("lastName");
          String phoneNumber = resultSet.getString("phoneNumber");

          passengerList.add(new Passenger(firstName, lastName, phoneNumber));
        }

        statement.close();
        conn.close();
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return passengerList;
  }

  public static List<Booking> getBookings() {
    var bookingList = new ArrayList<Booking>();

    try {
      Connection conn = getConnection();

      if (conn != null) {
        Statement statement = conn.createStatement();
        String bookingsQuery =
            """
              SELECT Booking.flightNumber, Booking.passengerId,
              Flight.startPoint, Flight.destination, Flight.departureDateTime,
              Passenger.firstName, Passenger.lastName, Passenger.phoneNumber
              FROM Booking
              LEFT JOIN Passenger
              ON Booking.passengerId = Passenger.id
              LEFT JOIN Flight
              ON Booking.flightNumber = Flight.flightNumber;
            """;

        ResultSet resultSet = statement.executeQuery(bookingsQuery);

        while (resultSet.next()) {
          int passengerId = resultSet.getInt(2);
          String passenger =
              resultSet.getString("firstName") + " " + resultSet.getString("lastName");
          String phoneNumber = resultSet.getString("phoneNumber");
          String flightNumber = resultSet.getString("flightNumber");
          String route =
              resultSet.getString("startPoint") + " - " + resultSet.getString("destination");
          String date = resultSet.getString("departureDateTime");

          bookingList.add(
              new Booking(passengerId, passenger, phoneNumber, flightNumber, route, date));
        }

        statement.close();
        conn.close();
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return bookingList;
  }

  public static void addFlight(Flight flight) {
    String addFlightQuery =
        "INSERT INTO Flight (flightNumber, startPoint, destination, departureDateTime, availableSeats) VALUES (?, ?, ?, ?, ?)";

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

    try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
      conn.createStatement().execute("PRAGMA foreign_keys = ON");

      try (PreparedStatement pstmt = conn.prepareStatement(deleteFlightQuery)) {
        pstmt.setString(1, flightNumber);
        pstmt.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void modifyFlight(Flight flight) {
    String modifyFlightQuery =
        "UPDATE Flight SET startPoint = ?, destination = ?, departureDateTime = ?, availableSeats = ? WHERE flightNumber = ?";

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

  public static void addPassenger(Passenger passenger) {
    String addFlightQuery =
        "INSERT INTO Passenger (firstName, lastName, phoneNumber) VALUES (?, ?, ?)";

    try (Connection conn = DriverManager.getConnection(DATABASE_URL);
        PreparedStatement pstmt = conn.prepareStatement(addFlightQuery)) {

      pstmt.setString(1, passenger.getName().getValue());
      pstmt.setString(2, passenger.getSurname().getValue());
      pstmt.setString(3, passenger.getPhoneNumber().getValue());

      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void deletePassenger(String phoneNumber) {
    String deletePassengerQuery = "DELETE FROM Passenger WHERE phoneNumber = ?";

    try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
      conn.createStatement().execute("PRAGMA foreign_keys = ON");

      try (PreparedStatement pstmt = conn.prepareStatement(deletePassengerQuery)) {
        pstmt.setString(1, phoneNumber);
        pstmt.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void modifyPassengerPhoneNumber(String newPhoneNumber, String oldPhoneNumber) {

    String modifyFlightQuery = "UPDATE Passenger SET phoneNumber = ? WHERE phoneNumber = ?";

    try (Connection conn = DriverManager.getConnection(DATABASE_URL);
        PreparedStatement pstmt = conn.prepareStatement(modifyFlightQuery)) {

      pstmt.setString(1, newPhoneNumber);
      pstmt.setString(2, oldPhoneNumber);

      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void deleteBooking(String flightNumber, int passengerId) {
    String deleteBookingQuery = "DELETE FROM Booking WHERE flightNumber = ? AND passengerId = ?";

    try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
      conn.createStatement().execute("PRAGMA foreign_keys = ON");

      try (PreparedStatement pstmt = conn.prepareStatement(deleteBookingQuery)) {
        pstmt.setString(1, flightNumber);
        pstmt.setInt(2, passengerId);
        pstmt.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static Flight getFlight(String flightNumber) {
    Flight flight = null;
    try {
      Connection conn = getConnection();
      if (conn != null) {
        Statement statement = conn.createStatement();
        String query = "SELECT * FROM Flight WHERE flightNumber = '" + flightNumber + "'";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
          String startPoint = resultSet.getString("startPoint");
          String destination = resultSet.getString("destination");
          String departureDateTime = resultSet.getString("departureDateTime");
          int availableSeats = resultSet.getInt("availableSeats");

          Statement takenSeatsStatement = conn.createStatement();
          String takenSeatsQuery =
              "SELECT COUNT(*) FROM Booking WHERE flightNumber = '" + flightNumber + "';";
          ResultSet takenSeatsResultSet = takenSeatsStatement.executeQuery(takenSeatsQuery);

          int takenSeats = 0;
          if (takenSeatsResultSet.next()) {
            takenSeats = takenSeatsResultSet.getInt(1);
          }

          flight =
              new Flight(
                  flightNumber,
                  startPoint,
                  destination,
                  departureDateTime,
                  availableSeats,
                  availableSeats - takenSeats);
        }
        statement.close();
        conn.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return flight;
  }

  public static Passenger getPassenger(String phoneNumber) {
    Passenger passenger = null;
    try {
      Connection conn = getConnection();
      if (conn != null) {
        Statement statement = conn.createStatement();
        String query = "SELECT * FROM Passenger WHERE phoneNumber = '" + phoneNumber + "'";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
          String firstName = resultSet.getString("firstName");
          String lastName = resultSet.getString("lastName");

          passenger = new Passenger(firstName, lastName, phoneNumber);
        }
        statement.close();
        conn.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return passenger;
  }

  public static int getPassengerId(String phoneNumber) {
    int passengerId = -1;
    try {
      Connection conn = getConnection();
      if (conn != null) {
        Statement statement = conn.createStatement();
        String query = "SELECT id FROM Passenger WHERE phoneNumber = '" + phoneNumber + "'";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
          passengerId = resultSet.getInt("id");
        }
        statement.close();
        conn.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return passengerId;
  }

  public static void addBooking(Booking booking) {
    String addFlightQuery = "INSERT INTO Booking (flightNumber, passengerId) VALUES (?, ?)";

    try (Connection conn = DriverManager.getConnection(DATABASE_URL);
        PreparedStatement pstmt = conn.prepareStatement(addFlightQuery)) {

      pstmt.setString(1, booking.getFlightNumber().getValue());
      pstmt.setInt(2, booking.getPassengerId());

      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static List<Flight> getFilteredFlights(
      String from, String to, String date, int minFreeSeats) {
    var filteredFlightsList = new ArrayList<Flight>();
    try {
      Connection conn = getConnection();

      if (conn != null) {
        StringBuilder flightsQuery = getFlightsQuery(from, to, date);

        PreparedStatement statement = conn.prepareStatement(flightsQuery.toString());

        int parameterIndex = 1;
        if (from != null) {
          statement.setString(parameterIndex++, from);
        }
        if (to != null) {
          statement.setString(parameterIndex++, to);
        }
        if (date != null) {
          statement.setString(parameterIndex++, date + "%");
        }
        statement.setInt(parameterIndex, minFreeSeats);

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
          String flightNumber = resultSet.getString("flightNumber");
          String startPoint = resultSet.getString("startPoint");
          String destination = resultSet.getString("destination");
          String departureDateTime = resultSet.getString("departureDateTime");
          int availableSeats = resultSet.getInt("availableSeats");

          Statement takenSeatsStatement = conn.createStatement();
          String takenSeatsQuery =
              "SELECT COUNT(*) FROM Booking WHERE flightNumber = '" + flightNumber + "';";
          ResultSet takenSeatsResultSet = takenSeatsStatement.executeQuery(takenSeatsQuery);

          int takenSeats = 0;
          if (takenSeatsResultSet.next()) {
            takenSeats = takenSeatsResultSet.getInt(1);
          }

          Flight flight =
              new Flight(
                  flightNumber,
                  startPoint,
                  destination,
                  departureDateTime,
                  availableSeats,
                  availableSeats - takenSeats);
          filteredFlightsList.add(flight);
        }

        statement.close();
        conn.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return filteredFlightsList;
  }

  private static StringBuilder getFlightsQuery(String from, String to, String date) {
    StringBuilder flightsQuery = new StringBuilder("SELECT * FROM Flight WHERE 1=1");

    if (from != null) {
      flightsQuery.append(" AND startPoint = ?");
    }

    if (to != null) {
      flightsQuery.append(" AND destination = ?");
    }

    if (date != null) {
      flightsQuery.append(" AND departureDateTime LIKE ?");
    }

    flightsQuery.append(
        " AND availableSeats - (SELECT COUNT(*) FROM Booking WHERE Flight.flightNumber = Booking.flightNumber) >= ?");
    return flightsQuery;
  }
}
