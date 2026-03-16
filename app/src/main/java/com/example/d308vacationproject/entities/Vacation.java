package com.example.d308vacationproject.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Room entity representing a vacation record in the "vacations" table.
// Each vacation has a title, hotel, date range, and notification preferences.
@Entity(tableName = "vacations")
public class Vacation {

    @PrimaryKey(autoGenerate = true)
    private int vacationID;             // Auto-generated primary key
    private String vacationName;        // Vacation title
    private String hotel;               // Hotel or lodging name
    private String startDate;           // Start date in MM/dd/yyyy format
    private String endDate;             // End date in MM/dd/yyyy format
    private boolean notifyStart = false; // Whether to alert on start date
    private boolean notifyEnd = false;  // Whether to alert on end date

    // Constructor used by Room and when creating/updating vacations in the UI.
    // Pass 0 for vacationID when creating a new vacation (Room auto-generates the ID).
    public Vacation(int vacationID, String vacationName, String hotel, String startDate, String endDate, boolean notifyStart, boolean notifyEnd) {
        this.vacationID = vacationID;
        this.vacationName = vacationName;
        this.hotel = hotel;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notifyStart = notifyStart;
        this.notifyEnd = notifyEnd;
    }

    // --- Getters and Setters (required by Room) ---

    public int getVacationID() {
        return vacationID;
    }

    public void setVacationID(int vacationID) {
        this.vacationID = vacationID;
    }

    public String getVacationName() {
        return vacationName;
    }

    public String getHotel() {
        return hotel;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public boolean isNotifyStart() {
        return notifyStart;
    }

    public boolean isNotifyEnd() {
        return notifyEnd;
    }

}
