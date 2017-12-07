package com.tfl.billing;

import com.oyster.*;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.*;

// This class charges the accounts of all customers. Also records card scans.
public class TravelTracker implements ScanListener {

    // Initiate final variables.
    static final BigDecimal OFF_PEAK_JOURNEY_PRICE = new BigDecimal(2.40);
    static final BigDecimal PEAK_JOURNEY_PRICE = new BigDecimal(3.20);

    static final BigDecimal PEAK_LONG = new BigDecimal(3.80);
    static final BigDecimal OFF_PEAK_LONG = new BigDecimal(2.70);
    static final BigDecimal PEAK_SHORT = new BigDecimal(2.90);
    static final BigDecimal OFF_PEAK_SHORT = new BigDecimal(1.60);

    static final BigDecimal PEAK_CAP = new BigDecimal(9.00);
    static final BigDecimal OFF_PEAK_CAP = new BigDecimal(7.00);

    private final List<JourneyEvent> eventLog = new ArrayList<JourneyEvent>();
    private final Set<UUID> currentlyTravelling = new HashSet<UUID>();

    // Returns the event log list.
    public List<JourneyEvent> getEventLog() {
        return eventLog;
    }

    // Return the currently travelling set.
    public Set<UUID> getCurrentlyTravelling() {
        return currentlyTravelling;
    }

    // This method charges all customers' accounts.
    // Calls totalJourneyFor each customer.
    public void chargeAccounts() {
        CustomerDatabase customerDatabase = CustomerDatabase.getInstance();

        List<Customer> customers = customerDatabase.getCustomers();
        for (Customer customer : customers) {
            totalJourneysFor(customer);
        }
    }

    // For a specific customer, it creates a list of their journeys.
    // It also calls customer total and charges the customer.
    private void totalJourneysFor(Customer customer) {
        List<JourneyEvent> customerJourneyEvents = new ArrayList<JourneyEvent>();
        for (JourneyEvent journeyEvent : eventLog) {
            if (journeyEvent.cardId().equals(customer.cardId())) {
                customerJourneyEvents.add(journeyEvent);
            }
        }

        List<Journey> journeys = new ArrayList<Journey>();

        JourneyEvent start = null;
        for (JourneyEvent event : customerJourneyEvents) {
            if (event instanceof JourneyStart) {
                start = event;
            }
            if (event instanceof JourneyEnd && start != null) {
                journeys.add(new Journey(start, event));
                start = null;
            }
        }

        // Get the customer total
        BigDecimal customerTotal = newCalculateTotal(journeys);
        // Charges the customer by the customer total found.
        PaymentsSystem.getInstance().charge(customer, journeys, roundToNearestPenny(customerTotal));
    }

    // Old, original implementation. Kept for testing.
    // Adds and return a total of the journeys given, with peak and off peak as the different prices.
    public BigDecimal originalCalculateTotal(List<Journey> journeys){
        BigDecimal customerTotal = new BigDecimal(0);
        for (Journey journey : journeys) {
            BigDecimal journeyPrice = OFF_PEAK_JOURNEY_PRICE;
            if (journey.getPeak()) {
                journeyPrice = PEAK_JOURNEY_PRICE;
            }
            customerTotal = customerTotal.add(journeyPrice);
        }

        return roundToNearestPenny(customerTotal);
    }

    // New implementation that accounts for short and long trips and caps.
    // Calculates and returns the customer's total.
    // Different price points for peak short, peak long, off peak short, and off peak long trips.
    // Also sets a peak and off peak caps the total can not go above.
    public BigDecimal newCalculateTotal(List<Journey> journeys){

        BigDecimal customerTotal = new BigDecimal(0);

        // Checks the journey's list for a peak journey. Necessary for determining the proper cap.
        boolean includesPeak = false;
        for (Journey journey1 : journeys) {
            if (journey1.getPeak()) {
                includesPeak = true;
            }
        }

        for (Journey journey : journeys) {
            // Initiate journey price as the lowest cost trip.
            BigDecimal journeyPrice = OFF_PEAK_SHORT;
            // Find the trip length and peak price point.
            // Don't check for off peak short because it's default.
            if (journey.getPeak() && journey.durationSeconds() < 1500) {
                journeyPrice = PEAK_SHORT;
            } else if (journey.getPeak() && journey.durationSeconds() >= 1500) {
                journeyPrice = PEAK_LONG;
            } else if (!journey.getPeak() && journey.durationSeconds() >= 1500) {
                journeyPrice = OFF_PEAK_LONG;
            }

            // If the journey price is less than than the peak peak cap.
            if (includesPeak && customerTotal.compareTo(PEAK_CAP) == -1) {
                // If the customer total plus the new journey price is larger than the cap, set customer total to cap.
                customerTotal = customerTotal.add(journeyPrice).min(PEAK_CAP);

            // If the journey price is less than than the peak peak cap.
            } else if (!includesPeak && customerTotal.compareTo(OFF_PEAK_CAP) == -1) {
                // If the customer total plus the new journey price is larger than the cap, set customer total to cap.
                customerTotal = customerTotal.add(journeyPrice).min(OFF_PEAK_CAP);
            }
        }

        return roundToNearestPenny(customerTotal);
    }

    private BigDecimal roundToNearestPenny(BigDecimal poundsAndPence) {
        return poundsAndPence.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void connect(OysterCardReader... cardReaders) {
        for (OysterCardReader cardReader : cardReaders) {
            cardReader.register(this);
        }
    }

    // Updates the event log and currently travelling when a car is scanned.
    @Override
    public void cardScanned(UUID cardId, UUID readerId) {
        if (currentlyTravelling.contains(cardId)) {
            eventLog.add(new JourneyEnd(cardId, readerId));
            currentlyTravelling.remove(cardId);
        } else {
            if (CustomerDatabase.getInstance().isRegisteredId(cardId)) {
                currentlyTravelling.add(cardId);
                eventLog.add(new JourneyStart(cardId, readerId));
            } else {
                throw new UnknownOysterCardException(cardId);
            }
        }
    }

}
