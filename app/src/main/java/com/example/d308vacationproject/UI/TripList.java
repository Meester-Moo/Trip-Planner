package com.example.d308vacationproject.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308vacationproject.R;
import com.example.d308vacationproject.database.Repository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


// Displays a scrollable list of all trips.
// The FAB (+) button lets the user create a new trip.
// The menu provides an option to add sample trip data.
public class TripList extends AppCompatActivity {

    private TripAdapter tripAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trip_list);

        // Adjust padding so content doesn't overlap system bars
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
        Repository repository = new Repository(getApplication());
        tripAdapter = new TripAdapter(this);
        recyclerView.setAdapter(tripAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Observe trip data — LiveData automatically refreshes the list when the database changes
        repository.getmAllTrips().observe(this, trips -> {
            tripAdapter.setTrips(trips);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trip_list, menu);
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
