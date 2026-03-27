package com.example.d308vacationproject.database;

import androidx.lifecycle.LiveData;

import com.example.d308vacationproject.entities.Excursion;
import com.example.d308vacationproject.entities.Trip;
import com.example.d308vacationproject.entities.User;

import java.util.List;

// Interface defining all data operations the app needs.
// This makes the app scalable as any class that implements
// this interface can serve as the data source (local database,
// cloud API, etc.).
//
// The UI layer depends on this interface, not a specific
// implementation, so the data source can be swapped without
// changing any Activity code.
public interface IRepository {

    // --- Trip Read Operations ---
    LiveData<List<Trip>> getmAllTrips();

    LiveData<List<Trip>> getTripsByUser(int userId);

    LiveData<List<Trip>> searchTrips(String searchQuery, int userId);

    List<Trip> getTripsByUserDirect(int userId);

    // --- Trip Write Operations ---
    void insert(Trip trip);

    void update(Trip trip);

    void delete(Trip trip);

    // --- Excursion Read Operations ---
    LiveData<List<Excursion>> getAssociatedExcursions(int tripID);

    LiveData<List<Excursion>> searchExcursions(String searchQuery, int tripID);

    List<Excursion> getAssociatedExcursionsDirect(int tripID);

    // --- Excursion Write Operations ---
    void insert(Excursion excursion, OnInsertCompleteListener listener);

    void update(Excursion excursion);

    void delete(Excursion excursion);

    // --- User Operations ---
    boolean registerUser(String username, String hashedPassword);

    User loginUser(String username, String hashedPassword);

    // Callback interface for excursion insert — provides the new ID after insertion completes
    interface OnInsertCompleteListener {
        void onInsertComplete(long newId);
    }

}
