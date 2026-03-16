package com.example.d308vacationproject.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.d308vacationproject.entities.Vacation;

import java.util.List;

// Data Access Object for the Vacation entity.
// Defines all database operations for vacations. Room generates the implementation at compile time.
@Dao
public interface VacationDAO {

    // Insert a new vacation. Returns the auto-generated row ID.
    // IGNORE strategy: silently skips if a vacation with the same primary key already exists.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Vacation vacation);

    // Update an existing vacation (matched by primary key).
    @Update
    void update(Vacation vacation);

    // Delete a vacation (matched by primary key).
    @Delete
    void delete(Vacation vacation);

    // Get all vacations ordered by ID. Returns LiveData so the UI automatically updates when data changes.
    @Query("SELECT * FROM VACATIONS ORDER BY vacationID ASC")
    LiveData<List<Vacation>> getmAllVacations();
}
