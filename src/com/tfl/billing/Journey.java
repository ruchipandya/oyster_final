package com.tfl.billing;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.Calendar;

public class Journey {

    private final JourneyEvent start;
    private final JourneyEvent end;
    private BigDecimal price;
    private boolean peak;

    public Journey(JourneyEvent start, JourneyEvent end) {
        this.start = start;
        this.end = end;
        price = new BigDecimal(0);
        peak = peak();
    }

    private boolean peak() {
        return peak(startTime()) || peak(endTime());
    }

    private boolean peak(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return (hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 19);
    }

    public boolean getPeak() { return peak; }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal newPrice){
        price = newPrice;
    }

    public UUID originId() {
        return start.readerId();
    }

    public UUID destinationId() {
        return end.readerId();
    }

    public String formattedStartTime() {
        return format(start.time());
    }

    public String formattedEndTime() {
        return format(end.time());
    }

    public Date startTime() {
        return new Date(start.time());
    }

    public Date endTime() {
        return new Date(end.time());
    }

    public int durationSeconds() {
        return (int) ((end.time() - start.time()) / 1000);
    }

    public String durationMinutes() {
        return "" + durationSeconds() / 60 + ":" + durationSeconds() % 60;
    }

    private String format(long time) {
        return SimpleDateFormat.getInstance().format(new Date(time));
    }
}
