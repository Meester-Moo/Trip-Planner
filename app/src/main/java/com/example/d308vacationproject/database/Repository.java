package com.example.d308vacationproject.database;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.d308vacationproject.dao.ExcursionDAO;
import com.example.d308vacationproject.dao.TripDAO;
import com.example.d308vacationproject.dao.UserDAO;
import com.example.d308vacationproject.entities.Excursion;
import com.example.d308vacationproject.entities.Trip;
import com.example.d308vacationproject.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Repository acts as a clean API between the UI layer and the database.
// All database writes run on a background thread pool to avoid blocking the UI.
// All database reads return LiveData, which automatically notifies observers when data changes.
public class Repository implements IRepository {

    private final ExcursionDAO mExcursionDAO;
    private final TripDAO mTripDAO;

    private final UserDAO mUserDAO;

    // Thread pool for executing database write operations off the main thread
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Constructor: gets the database singleton and initializes both DAOs
    public Repository(Application application) {
        TripDatabaseBuilder db = TripDatabaseBuilder.getDatabase(application);
        mExcursionDAO = db.excursionDAO();
        mTripDAO = db.tripDAO();
        mUserDAO = db.userDAO();
    }

    // --- Read Operations (return LiveData for automatic UI updates) ---

    // Get all trips, ordered by ID
    public LiveData<List<Trip>> getmAllTrips() {
        return mTripDAO.getmAllTrips();
    }

    // Get trips for a specific user only
    public LiveData<List<Trip>> getTripsByUser(int userId) {
        return mTripDAO.getTripsByUser(userId);
    }

    // Search trips by name or hotel for a specific user
    public LiveData<List<Trip>> searchTrips(String searchQuery, int userId) {
        return mTripDAO.searchTrips(searchQuery, userId);
    }

    // Get all excursions belonging to a specific trip
    public LiveData<List<Excursion>> getAssociatedExcursions(int tripID) {
        return mExcursionDAO.getAssociatedExcursions(tripID);
    }

    // Search excursions by name within a specific trip
    public LiveData<List<Excursion>> searchExcursions(String searchQuery, int tripID) {
        return mExcursionDAO.searchExcursions(searchQuery, tripID);
    }

    // --- Trip Write Operations (databaseExecutor.execute() = fire-and-forget on background thread) ---

    public void insert(Trip trip) {
        databaseExecutor.execute(() -> mTripDAO.insert(trip));
    }

    public void update(Trip trip) {
        databaseExecutor.execute(() -> mTripDAO.update(trip));
    }

    public void delete(Trip trip) {
        databaseExecutor.execute(() -> mTripDAO.delete(trip));
    }

    // --- Excursion Write Operations ---

    // Insert an excursion with a callback that returns the new auto-generated ID.
    // The callback runs on the main thread so it's safe to update the UI from it.
    public void insert(Excursion excursion, OnInsertCompleteListener listener) {
        databaseExecutor.execute(() -> {
            long newId = mExcursionDAO.insert(excursion);
            // Post the result back to the main (UI) thread
            new Handler(Looper.getMainLooper()).post(() -> {
                listener.onInsertComplete(newId);
            });
        });
    }


    public void update(Excursion excursion) {
        databaseExecutor.execute(() -> mExcursionDAO.update(excursion));
    }

    public void delete(Excursion excursion) {
        databaseExecutor.execute(() -> mExcursionDAO.delete(excursion));
    }

    // --- User Operations ---

    // Attempt to register a new user. Returns true if successful, false if username taken.
    // Runs on the background thread pool and uses Future to wait for the result.
    public boolean registerUser(String username, String hashedPassword) {
        Future<Boolean> future = databaseExecutor.submit(() -> {
            if (mUserDAO.getUserByUsername(username) != null) {
                return false;   // Username already exists
            }
            mUserDAO.insert(new User(0, username, hashedPassword));
            return true;
        });
        try {
            return future.get();    // Wait for the background thread to finish
        } catch (Exception e) {
            return false;
        }
    }

    // Attempt to log in. Returns the User object if credentials match, null otherwise.
    public User loginUser(String username, String hashedPassword) {
        Future<User> future = databaseExecutor.submit(() -> {
            return mUserDAO.login(username, hashedPassword);
        });
        try {
            return future.get();
        } catch (Exception e) {
            return null;
        }
    }

    // --- Report Operations (nonLiveData queries) ---

    // Get all trips for a user returned as a List.
    // Uses Future to run on background thread and wait for results
    public List<Trip> getTripsByUserDirect(int userId) {
        Future<List<Trip>> future = databaseExecutor.submit(() -> {
            return mTripDAO.getTripsByUserDirect(userId);
        });
        try {
            return future.get();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // Get all excursions for a trip
    // Uses Future to run on background thread and wait for results
    public List<Excursion> getAssociatedExcursionsDirect(int tripID) {
        Future<List<Excursion>> future = databaseExecutor.submit(() -> {
            return mExcursionDAO.getAssociatedExcursionsDirect(tripID);
        });
        try {
            return future.get();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

}
