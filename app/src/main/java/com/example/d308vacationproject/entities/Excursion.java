package com.example.d308vacationproject.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Room entity representing an excursion record in the "excursions" table.
// Each excursion belongs to a trip (linked by tripID) and has a title, date, and notification preference.
@Entity(tableName = "excursions")
public class Excursion extends PlannerItem {

    @PrimaryKey(autoGenerate = true)
    private int excursionID;            // Auto-generated primary key
    private String excursionName;       // Excursion title
    private String excursionDate;       // Excursion date in MM/dd/yyyy format
    private int tripID;                 // Foreign key linking to the parent trip
    private boolean notify = false;     // Whether to alert on the excursion date

    // Constructor used by Room and when creating/updating excursions in the UI.
    // Pass 0 for excursionID when creating a new excursion (Room auto-generates the ID).
    public Excursion(int excursionID, String excursionName, String excursionDate, int tripID, boolean notify) {
        this.excursionID = excursionID;
        this.excursionName = excursionName;
        this.excursionDate = excursionDate;
        this.tripID = tripID;
        this.notify = notify;
    }

    // --- Getters and Setters (required by Room) ---

    public int getExcursionID() {
        return excursionID;
    }

    public void setExcursionID(int excursionID) {
        this.excursionID = excursionID;
    }

    public String getExcursionName() {
        return excursionName;
    }

    public void setExcursionName(String excursionName) {
        this.excursionName = excursionName;
    }

    public String getExcursionDate() {
        return excursionDate;
    }

    public void setExcursionDate(String excursionDate) {
        this.excursionDate = excursionDate;
    }

    public int getTripID() {
        return tripID;
    }

    public void setTripID(int tripID) {
        this.tripID = tripID;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    @Override
    public String getItemName() {
        return excursionName;
    }

    @Override
    public String getSummary() {
        return excursionName + " on " + excursionDate;
    }
}
