package com.example.d308vacationproject.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.d308vacationproject.dao.ExcursionDAO;
import com.example.d308vacationproject.dao.TripDAO;
import com.example.d308vacationproject.entities.Excursion;
import com.example.d308vacationproject.entities.Trip;
import com.example.d308vacationproject.entities.User;
import com.example.d308vacationproject.dao.UserDAO;

// Room database class. Registers both entities (Trip, Excursion) and provides access to their DAOs.
// Uses the singleton pattern so only one database instance exists at a time.
// fallbackToDestructiveMigration() wipes and rebuilds the database on schema version changes.
@Database(entities = {Trip.class, Excursion.class, User.class}, version = 44, exportSchema = false)
public abstract class TripDatabaseBuilder extends RoomDatabase {

    // Abstract methods that Room implements to return each DAO
    public abstract TripDAO tripDAO();

    public abstract ExcursionDAO excursionDAO();

    public abstract UserDAO userDAO();

    // Singleton instance (volatile ensures thread-safe reads)
    private static volatile TripDatabaseBuilder INSTANCE;

    // Returns the singleton database instance, creating it if it doesn't exist.
    // Uses double-checked locking to prevent multiple threads from creating separate instances.
    static TripDatabaseBuilder getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TripDatabaseBuilder.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), TripDatabaseBuilder.class, "MyTripDatabase.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
