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

public class TravelTrackerTest {

    final TravelTracker travelTracker = new TravelTracker();
    private final double PEAK_PRICE = 3.20;
    private final double OFF_PEAK_PRICE = 2.40;

    private final double PEAK_LONG = 3.80;
    private final double OFF_PEAK_LONG = 2.70;
    private final double PEAK_SHORT = 2.90;
    private final double OFF_PEAK_SHORT = 1.60;

    private final double PEAK_CAP = 9.00;
    private final double OFF_PEAK_CAP = 7.00;


    public UUID getOysterCardID() {
        CustomerDatabase customerDatabase = CustomerDatabase.getInstance();
        Customer customer = customerDatabase.getCustomers().get(0);
        OysterCard card = new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        return card.id();
    }

    public UUID getReaderID() {
        OysterCardReader reader = new OysterCardReader();
        return reader.id();
    }

    @Test
    //old charging
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
    //old charging
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
    //new charging
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
    //new charging
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
    //new charging
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
    //new charging
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
    //new charging with off peak start but peak end - short journey
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
    //new charging with off peak start but peak end - long journey
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
    //test cap on journeys
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
    //test cap on journeys
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
    //test cap on journeys
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
}