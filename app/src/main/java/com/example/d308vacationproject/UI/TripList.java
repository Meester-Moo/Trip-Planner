package com.example.d308vacationproject.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308vacationproject.R;
import com.example.d308vacationproject.database.Repository;
import com.example.d308vacationproject.entities.Trip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


// Displays a scrollable list of all trips for the logged-in user.
// Includes a SearchView in the action bar to filter trips by name or hotel.
public class TripList extends AppCompatActivity {

    private TripAdapter tripAdapter;
    private Repository repository;
    private int userId;

    // Keeps track of the current LiveData being observed so we can remove old observers
    // when the search query changes. Without this, old observers would pile up and
    // the adapter would get conflicting updates.
    private LiveData<List<Trip>> currentTripData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trip_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // FAB navigates to TripDetails to create a new trip (no ID passed = new trip)
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(TripList.this, TripDetails.class);
            startActivity(intent);
        });

        // Initialize repository and set up RecyclerView with adapter
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTrips);
        repository = new Repository(getApplication());
        tripAdapter = new TripAdapter(this);
        recyclerView.setAdapter(tripAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Observe trip data — LiveData automatically refreshes the list when the database changes
        SharedPreferences prefs = getSharedPreferences("TripPlannerPrefs", MODE_PRIVATE);
        userId = prefs.getInt("loggedInUserId", -1);

        // Load all trips initially (no search filter)
        loadTrips(null);
    }

    // Loads trips from the database, optionally filtered by a search query.
    // If searchQuery is null or empty, loads all trips for the user.
    // If searchQuery has text, loads only matching trips.
    private void loadTrips(String searchQuery) {
        // Remove the previous observer so we don't get duplicate updates
        if (currentTripData != null) {
            currentTripData.removeObservers(this);
        }

        //Choose the right query based on whether we're searching or not
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            currentTripData = repository.getTripsByUser(userId);
        } else {
            currentTripData = repository.searchTrips(searchQuery.trim(), userId);
        }

        // Observe the new LiveData and update the adapter when results arrive
        currentTripData.observe(this, trips -> {
            tripAdapter.setTrips(trips);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trip_list, menu);

        // Find the SearchView menu item and get the actual SearchView widget from it
        MenuItem searchItem = menu.findItem(R.id.action_search_trips);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search trips...");

        // Listen for text changes as the user types
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            // Called every time the user types or deletes a character
            // This is what makes the search feel "live" - results update instantly
            @Override
            public boolean onQueryTextChange(String newText) {
                loadTrips(newText);
                return false;
            }

            // Called when the user presses the search/enter button.
            // We use live filtering so we don't need it but it is required
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });

        // When the user closes the SearchView (taps the X or back arrow),
        // reload all trips with no filter
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                loadTrips(null);    // Reset to all trips
                return true;    // Allow collapsing
            }

            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
                return true;   // Allow expanding
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle the back arrow in the action bar
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
