package com.example.d308vacationproject.database;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.d308vacationproject.dao.ExcursionDAO;
import com.example.d308vacationproject.dao.TripDAO;
import com.example.d308vacationproject.entities.Excursion;
import com.example.d308vacationproject.entities.Trip;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Repository acts as a clean API between the UI layer and the database.
// All database writes run on a background thread pool to avoid blocking the UI.
// All database reads return LiveData, which automatically notifies observers when data changes.
public class Repository {

    private final ExcursionDAO mExcursionDAO;
    private final TripDAO mTripDAO;

    // Thread pool for executing database write operations off the main thread
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Constructor: gets the database singleton and initializes both DAOs
    public Repository(Application application) {
        TripDatabaseBuilder db = TripDatabaseBuilder.getDatabase(application);
        mExcursionDAO = db.excursionDAO();
        mTripDAO = db.tripDAO();
    }

    // --- Read Operations (return LiveData for automatic UI updates) ---

    // Get all trips, ordered by ID
    public LiveData<List<Trip>> getmAllTrips() {
        return mTripDAO.getmAllTrips();
    }

    // Get all excursions belonging to a specific trip
    public LiveData<List<Excursion>> getAssociatedExcursions(int tripID) {
        return mExcursionDAO.getAssociatedExcursions(tripID);
    }

    // --- Trip Write Operations (fire-and-forget on background thread) ---

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

    // Callback interface for excursion insert — provides the new ID after insertion completes
    public interface OnInsertCompleteListener {
        void onInsertComplete(long newId);
    }

    public void update(Excursion excursion) {
        databaseExecutor.execute(() -> mExcursionDAO.update(excursion));
    }

    public void delete(Excursion excursion) {
        databaseExecutor.execute(() -> mExcursionDAO.delete(excursion));
    }
}
