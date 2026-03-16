package com.example.d308vacationproject.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Room entity representing an excursion record in the "excursions" table.
// Each excursion belongs to a vacation (linked by vacationID) and has a title, date, and notification preference.
@Entity(tableName = "excursions")
public class Excursion {

    @PrimaryKey(autoGenerate = true)
    private int excursionID;            // Auto-generated primary key
    private String excursionName;       // Excursion title
    private String excursionDate;       // Excursion date in MM/dd/yyyy format
    private int vacationID;             // Foreign key linking to the parent vacation
    private boolean notify = false;     // Whether to alert on the excursion date

    // Constructor used by Room and when creating/updating excursions in the UI.
    // Pass 0 for excursionID when creating a new excursion (Room auto-generates the ID).
    public Excursion(int excursionID, String excursionName, String excursionDate, int vacationID, boolean notify) {
        this.excursionID = excursionID;
        this.excursionName = excursionName;
        this.excursionDate = excursionDate;
        this.vacationID = vacationID;
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

    public int getVacationID() {
        return vacationID;
    }

    public void setVacationID(int vacationID) {
        this.vacationID = vacationID;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }
}
