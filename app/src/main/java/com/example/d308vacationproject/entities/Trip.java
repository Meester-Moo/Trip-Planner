package com.example.d308vacationproject.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Room entity representing a trip record in the "trips" table.
// Each trip has a title, hotel, date range, and notification preferences.

// Encapsulation is also present as each field in Trip is private and is only
// accessible/modifiable with public getter and setters

@Entity(tableName = "trips")
public class Trip extends PlannerItem {

    @PrimaryKey(autoGenerate = true)
    private int tripID;                  // Auto-generated primary key
    private String tripName;             // Trip title
    private String hotel;                // Hotel or lodging name
    private String startDate;            // Start date in MM/dd/yyyy format
    private String endDate;              // End date in MM/dd/yyyy format
    private boolean notifyStart = false; // Whether to alert on start date
    private boolean notifyEnd = false;   // Whether to alert on end date

    // Constructor used by Room and when creating/updating trips in the UI.
    // Pass 0 for tripID when creating a new trip (Room auto-generates the ID).
    public Trip(int tripID, String tripName, String hotel, String startDate, String endDate, boolean notifyStart, boolean notifyEnd) {
        this.tripID = tripID;
        this.tripName = tripName;
        this.hotel = hotel;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notifyStart = notifyStart;
        this.notifyEnd = notifyEnd;
    }

    // --- Getters and Setters (required by Room) ---

    public int getTripID() {
        return tripID;
    }

    public void setTripID(int tripID) {
        this.tripID = tripID;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isNotifyStart() {
        return notifyStart;
    }

    public void setNotifyStart(boolean notifyStart) {
        this.notifyStart = notifyStart;
    }

    public boolean isNotifyEnd() {
        return notifyEnd;
    }

    public void setNotifyEnd(boolean notifyEnd) {
        this.notifyEnd = notifyEnd;
    }

    @Override
    public String getItemName() {
        return tripName;
    }

    @Override
    public String getSummary() {
        return tripName + " at " + hotel + " (" + startDate + " - " + endDate + ")";
    }
}
