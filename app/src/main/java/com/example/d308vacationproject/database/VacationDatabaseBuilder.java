package com.example.d308vacationproject.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.d308vacationproject.dao.ExcursionDAO;
import com.example.d308vacationproject.dao.VacationDAO;
import com.example.d308vacationproject.entities.Excursion;
import com.example.d308vacationproject.entities.Vacation;

// Room database class. Registers both entities (Vacation, Excursion) and provides access to their DAOs.
// Uses the singleton pattern so only one database instance exists at a time.
// fallbackToDestructiveMigration() wipes and rebuilds the database on schema version changes.
@Database(entities = {Vacation.class, Excursion.class}, version = 42, exportSchema = false)
public abstract class VacationDatabaseBuilder extends RoomDatabase {

    // Abstract methods that Room implements to return each DAO
    public abstract VacationDAO vacationDAO();
    public abstract ExcursionDAO excursionDAO();

    // Singleton instance (volatile ensures thread-safe reads)
    private static volatile VacationDatabaseBuilder INSTANCE;

    // Returns the singleton database instance, creating it if it doesn't exist.
    // Uses double-checked locking to prevent multiple threads from creating separate instances.
    static VacationDatabaseBuilder getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (VacationDatabaseBuilder.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), VacationDatabaseBuilder.class, "MyVacationDatabase.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
