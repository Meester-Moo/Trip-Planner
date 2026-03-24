package com.example.d308vacationproject.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.d308vacationproject.entities.Trip;

import java.util.List;

// Data Access Object for the Trip entity.
// Defines all database operations for trips. Room generates the implementation at compile time.
@Dao
public interface TripDAO {

    // Insert a new trip. Returns the auto-generated row ID.
    // IGNORE strategy: silently skips if a trip with the same primary key already exists.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Trip trip);

    // Update an existing trip (matched by primary key).
    @Update
    void update(Trip trip);

    // Delete a trip (matched by primary key).
    @Delete
    void delete(Trip trip);

    // Get all trips ordered by ID. Returns LiveData so the UI automatically updates when data changes.
    @Query("SELECT * FROM TRIPS ORDER BY tripID ASC")
    LiveData<List<Trip>> getmAllTrips();

    // Get only trips belonging to a specific user
    @Query("SELECT * FROM TRIPS WHERE userId = :userId ORDER BY tripID ASC")
    LiveData<List<Trip>> getTripsByUser(int userId);

    // Search trips by name or hotel for a specific user.
    // The '%' wildcards around the search term enable "contains" matching.
    @Query("SELECT * FROM TRIPS WHERE userId = :userId AND (tripName LIKE '%' || :searchQuery || '%' OR hotel LIKE '%' || :searchQuery || '%') ORDER BY tripID ASC")
    LiveData<List<Trip>> searchTrips(String searchQuery, int userId);
}
