package com.example.d308vacationproject.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.d308vacationproject.entities.Excursion;

import java.util.List;

// Data Access Object for the Excursion entity.
// Defines all database operations for excursions. Room generates the implementation at compile time.
@Dao
public interface ExcursionDAO {

    // Insert a new excursion. Returns the auto-generated row ID.
    // REPLACE strategy: overwrites if an excursion with the same primary key exists.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Excursion excursion);

    // Update an existing excursion (matched by primary key).
    @Update
    void update(Excursion excursion);

    // Delete an excursion (matched by primary key).
    @Delete
    void delete(Excursion excursion);

    // Get all excursions for a specific trip, ordered by ID.
    // Returns LiveData so the UI automatically updates when excursions change.
    @Query("SELECT * FROM EXCURSIONS WHERE tripID=:tripID ORDER BY excursionID ASC")
    LiveData<List<Excursion>> getAssociatedExcursions(int tripID);

    // Search excursions by name within a specific trip.
    @Query("SELECT * FROM EXCURSIONS WHERE tripID = :tripID AND excursionName LIKE '%' || :searchQuery || '%' ORDER BY excursionID ASC")
    LiveData<List<Excursion>> searchExcursions(String searchQuery, int tripID);

    // Non-LiveData query for reports - returns all excursions for a trip as a regular List
    @Query("SELECT * FROM EXCURSIONS WHERE tripID = :tripID ORDER BY excursionID ASC")
    List<Excursion> getAssociatedExcursionsDirect(int tripID);
}
