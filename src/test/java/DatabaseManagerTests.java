import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;
import org.junit.jupiter.api.*;
import org.ms.skybooker.model.Booking;
import org.ms.skybooker.model.Flight;
import org.ms.skybooker.model.Passenger;
import org.ms.skybooker.repository.DatabaseManager;

class DatabaseManagerTest {

  private static final String TEST_DATABASE_URL = "jdbc:sqlite:src/test/resources/testDatabase.db";

  @BeforeEach
  void setUp() {
    DatabaseManager.createDatabase(TEST_DATABASE_URL);
    DatabaseManager.addSampleData();
  }

  @AfterEach
  void tearDown() {
    File databaseFile = new File("src/test/resources/testDatabase.db");

    if (databaseFile.exists()) {
      if (!databaseFile.delete()) {
        System.out.println("Cannot delete test database");
      }
    }
  }

  @Test
  void testGetFlights() {
    List<Flight> flights = DatabaseManager.getFlights();
    assertNotNull(flights);
    assertFalse(flights.isEmpty());
  }

  @Test
  void testGetPassengers() {
    List<Passenger> passengers = DatabaseManager.getPassengers();
    assertNotNull(passengers);
    assertFalse(passengers.isEmpty());
  }

  @Test
  void testGetBookings() {
    List<Booking> bookings = DatabaseManager.getBookings();
    assertNotNull(bookings);
    assertFalse(bookings.isEmpty());
  }

  @Test
  void testAddFlight() {
    Flight newFlight =
        new Flight("TEST123", "TestCity1", "TestCity2", "2024-04-25 15:00", 200, 200);
    DatabaseManager.addFlight(newFlight);

    Flight retrievedFlight = DatabaseManager.getFlight("TEST123");
    assertNotNull(retrievedFlight);
    assertEquals("TEST123", retrievedFlight.getFlightNumber().getValue());
    assertEquals("TestCity2", retrievedFlight.getDestination().getValue());
    assertEquals("TestCity1", retrievedFlight.getStartPoint().getValue());
    assertEquals("2024-04-25 15:00", retrievedFlight.getDepartureDateTime().getValue());
    assertEquals(200, retrievedFlight.getAvailableSeats().getValue());
    assertEquals(200, retrievedFlight.getFreeSeats().getValue());
  }

  @Test
  void testDeleteFlight() {
    DatabaseManager.deleteFlight("TEST123");
    assertNull(DatabaseManager.getFlight("TEST123"));
  }

  @Test
  void testModifyFlight() {
    Flight flight = DatabaseManager.getFlight("FL123");
    Flight modifiedFlight =
        new Flight(
            flight.getFlightNumber().getValue(),
            "Toronto",
            "Warsaw",
            "2025-02-25 12:33",
            flight.getAvailableSeats().getValue(),
            flight.getAvailableSeats().getValue());
    DatabaseManager.modifyFlight(modifiedFlight);

    Flight retrievedFlight = DatabaseManager.getFlight("FL123");
    assertNotNull(retrievedFlight);
    assertEquals("Toronto", retrievedFlight.getStartPoint().getValue());
    assertEquals("Warsaw", retrievedFlight.getDestination().getValue());
    assertEquals("2025-02-25 12:33", retrievedFlight.getDepartureDateTime().getValue());
  }

  @Test
  void testAddPassenger() {
    Passenger newPassenger = new Passenger("TestFirstName", "TestLastName", "999-888-7777");
    DatabaseManager.addPassenger(newPassenger);

    Passenger retrievedPassenger = DatabaseManager.getPassenger("999-888-7777");
    assertNotNull(retrievedPassenger);
    assertEquals("TestFirstName", retrievedPassenger.getName().getValue());
    assertEquals("TestLastName", retrievedPassenger.getSurname().getValue());
    assertEquals("999-888-7777", retrievedPassenger.getPhoneNumber().getValue());
  }

  @Test
  void testDeletePassenger() {
    DatabaseManager.deletePassenger("999-888-7777");
    assertNull(DatabaseManager.getPassenger("999-888-7777"));
  }

  @Test
  void modifyPassengerPhoneNumber() {
    String oldPhoneNumber = "123-456-7890";
    String newPhoneNumber = "4432";

    Passenger passenger = DatabaseManager.getPassenger(oldPhoneNumber);
    DatabaseManager.modifyPassengerPhoneNumber(newPhoneNumber, oldPhoneNumber);

    Passenger modifiedPassenger = DatabaseManager.getPassenger(newPhoneNumber);
    assertNotNull(modifiedPassenger);

    assertEquals(passenger.getName().getValue(), modifiedPassenger.getName().getValue());
    assertEquals(passenger.getSurname().getValue(), modifiedPassenger.getSurname().getValue());
  }

  @Test
  void testDeleteBooking() {
    DatabaseManager.deleteBooking("FL123", 1);
    List<Booking> bookings = DatabaseManager.getBookings();
    for (Booking booking : bookings) {
      assertFalse(
          booking.getFlightNumber().getValue().equals("FL123") && booking.getPassengerId() == 1);
    }
  }

  @Test
  void testGetFilteredFlights() {
    List<Flight> filteredFlights =
        DatabaseManager.getFilteredFlights("New York", "Los Angeles", null, 10);
    assertNotNull(filteredFlights);
    assertFalse(filteredFlights.isEmpty());
  }
}
