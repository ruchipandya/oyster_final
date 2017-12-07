package test.tfl.billing;

import com.oyster.*;
import com.tfl.underground.*;
import com.tfl.billing.*;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import org.junit.Test;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.Assert.*;

// Tester class to test the TravelTracker.java class. This test file covers the whole project implementation.
public class TravelTrackerTest {

    // Initiate travel tracker file and create final vars.
    final TravelTracker travelTracker = new TravelTracker();
    private final double PEAK_PRICE = 3.20;
    private final double OFF_PEAK_PRICE = 2.40;

    private final double PEAK_LONG = 3.80;
    private final double OFF_PEAK_LONG = 2.70;
    private final double PEAK_SHORT = 2.90;
    private final double OFF_PEAK_SHORT = 1.60;

    private final double PEAK_CAP = 9.00;
    private final double OFF_PEAK_CAP = 7.00;

    // Helper function to return the card ID we use for all our tests.
    public UUID getOysterCardID() {
        CustomerDatabase customerDatabase = CustomerDatabase.getInstance();
        Customer customer = customerDatabase.getCustomers().get(0);
        OysterCard card = new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        return card.id();
    }

    // Helper function to return the reader ID we use for all our tests.
    public UUID getReaderID() {
        OysterCardReader reader = new OysterCardReader();
        return reader.id();
    }

    // The testing methods for charging the correct account fist creates the journey event start and end.
    // It then sets the time for both, making it peak or off peak.
    // It then adds those jouney(s) to a journey list.
    // You call the travel tracker method to get charge amount.
    // An assert is used to check that it calculated the correct price.

    @Test
    // Test for the original functionality of just having peak and non peak journeys.
    // Tests for peak journeys charging correctly.
    public void testPeakChargeAccounts() {
        JourneyEvent start = new JourneyStart(getOysterCardID(), getReaderID());
        start.setTime(1512457451000L);
        JourneyEvent end = new JourneyEnd(getOysterCardID(), getReaderID());
        end.setTime(1512461051000L);
        List<Journey> journeys = new ArrayList<Journey>();
        journeys.add(new Journey(start, end));
        assertEquals(PEAK_PRICE, travelTracker.originalCalculateTotal(journeys).doubleValue(), 0);
    }

    @Test
    // Test for the original functionality of just having peak and non peak journeys.
    // Tests for off peak journeys charging correctly.
    public void testOffPeakChargeAccounts() {
        JourneyEvent start = new JourneyStart(getOysterCardID(), getReaderID());
        start.setTime(1512482651000L);
        JourneyEvent end = new JourneyEnd(getOysterCardID(), getReaderID());
        end.setTime(1512486251000L);
        List<Journey> journeys = new ArrayList<Journey>();
        journeys.add(new Journey(start, end));
        assertEquals(OFF_PEAK_PRICE, travelTracker.originalCalculateTotal(journeys).doubleValue(), 0);
    }

    @Test
    // Test for short peak journeys charging correctly.
    public void testPeakShortChargeAccounts() {
        JourneyEvent start = new JourneyStart(getOysterCardID(), getReaderID());
        start.setTime(1512457232000L);
        JourneyEvent end = new JourneyEnd(getOysterCardID(), getReaderID());
        end.setTime(1512458132000L);
        List<Journey> journeys = new ArrayList<Journey>();
        journeys.add(new Journey(start, end));
        assertEquals(PEAK_SHORT, travelTracker.newCalculateTotal(journeys).doubleValue(), 0);
    }

    @Test
    // Test for long peak journeys charging correctly.
    public void testPeakLongChargeAccounts() {
        JourneyEvent start = new JourneyStart(getOysterCardID(), getReaderID());
        start.setTime(1512457232000L);
        JourneyEvent end = new JourneyEnd(getOysterCardID(), getReaderID());
        end.setTime(1512461732000L);
        List<Journey> journeys = new ArrayList<Journey>();
        journeys.add(new Journey(start, end));
        assertEquals(PEAK_LONG, travelTracker.newCalculateTotal(journeys).doubleValue(), 0);
    }

    @Test
    // Test for short off peak journeys charging correctly.
    public void testOffPeakShortChargeAccounts() {
        JourneyEvent start = new JourneyStart(getOysterCardID(), getReaderID());
        start.setTime(1512478832000L);
        JourneyEvent end = new JourneyEnd(getOysterCardID(), getReaderID());
        end.setTime(1512479732000L);
        List<Journey> journeys = new ArrayList<Journey>();
        journeys.add(new Journey(start, end));
        assertEquals(OFF_PEAK_SHORT, travelTracker.newCalculateTotal(journeys).doubleValue(), 0);
    }

    @Test
    // Test for long off peak journeys charging correctly.
    public void testOffPeakLongChargeAccounts() {
        JourneyEvent start = new JourneyStart(getOysterCardID(), getReaderID());
        start.setTime(1512478832000L);
        JourneyEvent end = new JourneyEnd(getOysterCardID(), getReaderID());
        end.setTime(1512483332000L);
        List<Journey> journeys = new ArrayList<Journey>();
        journeys.add(new Journey(start, end));
        assertEquals(OFF_PEAK_LONG, travelTracker.newCalculateTotal(journeys).doubleValue(), 0);
    }

    @Test
    // Test for off peak start but peak end - short journey charging correctly.
    public void testBothShortChargeAccounts() {
        JourneyEvent start = new JourneyStart(getOysterCardID(), getReaderID());
        start.setTime(1512493089000L);
        JourneyEvent end = new JourneyEnd(getOysterCardID(), getReaderID());
        end.setTime(1512493509000L);
        List<Journey> journeys = new ArrayList<Journey>();
        journeys.add(new Journey(start, end));
        assertEquals(PEAK_SHORT, travelTracker.newCalculateTotal(journeys).doubleValue(), 0);
    }

    @Test
    // Test for off peak start but peak end - long journey charging correctly.
    public void testBothLongChargeAccounts() {
        JourneyEvent start = new JourneyStart(getOysterCardID(), getReaderID());
        start.setTime(1512493089000L);
        JourneyEvent end = new JourneyEnd(getOysterCardID(), getReaderID());
        end.setTime(1512495009000L);
        List<Journey> journeys = new ArrayList<Journey>();
        journeys.add(new Journey(start, end));
        assertEquals(PEAK_LONG, travelTracker.newCalculateTotal(journeys).doubleValue(), 0);
    }

    @Test
    // Testing the off peak cap.
    public void testOffPeakCap(){
        JourneyEvent start = new JourneyStart(getOysterCardID(), getReaderID());
        start.setTime(1512478832000L);
        JourneyEvent end = new JourneyEnd(getOysterCardID(), getReaderID());
        end.setTime(1512483332000L);
        List<Journey> journeys = new ArrayList<Journey>();
        journeys.add(new Journey(start, end));
        journeys.add(new Journey(start, end));
        journeys.add(new Journey(start, end));
        journeys.add(new Journey(start, end));
        assertEquals(OFF_PEAK_CAP, travelTracker.newCalculateTotal(journeys).doubleValue(), 0);
    }

    @Test
    // Testing the peak cap.
    public void testPeakCap(){
        JourneyEvent start = new JourneyStart(getOysterCardID(), getReaderID());
        start.setTime(1512457232000L);
        JourneyEvent end = new JourneyEnd(getOysterCardID(), getReaderID());
        end.setTime(1512461732000L);
        List<Journey> journeys = new ArrayList<Journey>();
        journeys.add(new Journey(start, end));
        journeys.add(new Journey(start, end));
        journeys.add(new Journey(start, end));
        journeys.add(new Journey(start, end));
        assertEquals(PEAK_CAP, travelTracker.newCalculateTotal(journeys).doubleValue(), 0);
    }

    @Test
    // Testing for peak cap when there's a mix of peak and off peak journeys.
    public void testMixPeakCap(){
        JourneyEvent start2 = new JourneyStart(getOysterCardID(), getReaderID());
        start2.setTime(1512478832000L);
        JourneyEvent end2 = new JourneyEnd(getOysterCardID(), getReaderID());
        end2.setTime(1512483332000L);
        JourneyEvent start1 = new JourneyStart(getOysterCardID(), getReaderID());
        start1.setTime(1512457232000L);
        JourneyEvent end1 = new JourneyEnd(getOysterCardID(), getReaderID());
        end1.setTime(1512461732000L);
        List<Journey> journeys = new ArrayList<Journey>();
        journeys.add(new Journey(start2, end2));
        journeys.add(new Journey(start2, end2));
        journeys.add(new Journey(start1, end1));
        journeys.add(new Journey(start2, end2));
        assertEquals(PEAK_CAP, travelTracker.newCalculateTotal(journeys).doubleValue(), 0);
    }

    // Testing the cardScanned method for cases where they're ending the journey
    @Test
    public void testCardScannedJourneyEnd() {
        travelTracker.getCurrentlyTravelling().add(getOysterCardID());
        travelTracker.cardScanned(getOysterCardID(), getReaderID());

        assertEquals(1, travelTracker.getEventLog().size());
        assertEquals(getOysterCardID(), travelTracker.getEventLog().get(0).cardId());
        assertEquals(0, travelTracker.getCurrentlyTravelling().size());
    }

    // Testing the cardScanned method for cases where they're starting the journey.
    @Test
    public void testCardScannedJourneyStart() {
        TravelTracker travel = new TravelTracker();
        CustomerDatabase customerDatabase = CustomerDatabase.getInstance();
        Customer customer = customerDatabase.getCustomers().get(0);
        OysterCard card = new OysterCard("3f1b3b55-f266-4426-ba1b-bcc506541866");
        OysterCardReader reader = new OysterCardReader();

        travel.cardScanned(card.id(), reader.id());

        assertEquals(1, travel.getEventLog().size());
        assertEquals(card.id(), travel.getEventLog().get(0).cardId());
        assertEquals(1, travel.getCurrentlyTravelling().size());
        assertTrue(travel.getCurrentlyTravelling().contains(card.id()));
    }

    // Testing that the cardScanned method throws an error
    @Test
    public void testCardScannedThrowError() {
        CustomerDatabase customerDatabase = CustomerDatabase.getInstance();
        Customer customer = customerDatabase.getCustomers().get(2);
        // This is an incorrect oyster card ID, should throw error.
        OysterCard card = new OysterCard("07b0bcb1-87df-447f-bf5c-d9961ab9d010");
        OysterCardReader reader = new OysterCardReader();

        try {
            travelTracker.cardScanned(card.id(), reader.id());
            fail("expected exception did not occur.");
        } catch(UnknownOysterCardException e) {

        }
    }
}