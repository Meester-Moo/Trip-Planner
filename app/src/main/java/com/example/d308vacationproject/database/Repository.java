package com.example.d308vacationproject.database;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.d308vacationproject.dao.ExcursionDAO;
import com.example.d308vacationproject.dao.VacationDAO;
import com.example.d308vacationproject.entities.Excursion;
import com.example.d308vacationproject.entities.Vacation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Repository acts as a clean API between the UI layer and the database.
// All database writes run on a background thread pool to avoid blocking the UI.
// All database reads return LiveData, which automatically notifies observers when data changes.
public class Repository {

    private final ExcursionDAO mExcursionDAO;
    private final VacationDAO mVacationDAO;

    // Thread pool for executing database write operations off the main thread
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Constructor: gets the database singleton and initializes both DAOs
    public Repository(Application application) {
        VacationDatabaseBuilder db = VacationDatabaseBuilder.getDatabase(application);
        mExcursionDAO = db.excursionDAO();
        mVacationDAO = db.vacationDAO();
    }

    // --- Read Operations (return LiveData for automatic UI updates) ---

    // Get all vacations, ordered by ID
    public LiveData<List<Vacation>> getmAllVacations() {
        return mVacationDAO.getmAllVacations();
    }

    // Get all excursions belonging to a specific vacation
    public LiveData<List<Excursion>> getAssociatedExcursions(int vacationID) {
        return mExcursionDAO.getAssociatedExcursions(vacationID);
    }

    // --- Vacation Write Operations (fire-and-forget on background thread) ---

    public void insert(Vacation vacation) {
        databaseExecutor.execute(() -> mVacationDAO.insert(vacation));
    }

    public void update(Vacation vacation) {
        databaseExecutor.execute(() -> mVacationDAO.update(vacation));
    }

    public void delete(Vacation vacation) {
        databaseExecutor.execute(() -> mVacationDAO.delete(vacation));
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
