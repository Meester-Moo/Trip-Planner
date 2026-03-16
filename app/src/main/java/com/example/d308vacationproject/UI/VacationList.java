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


// Displays a scrollable list of all vacations.
// The FAB (+) button lets the user create a new vacation.
// The menu provides an option to add sample vacation data.
public class VacationList extends AppCompatActivity {

    private VacationAdapter vacationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_list);

        // Adjust padding so content doesn't overlap system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // FAB navigates to VacationDetails to create a new vacation (no ID passed = new vacation)
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(VacationList.this, VacationDetails.class);
            startActivity(intent);
        });

        // Initialize repository and set up RecyclerView with adapter
        RecyclerView recyclerView = findViewById(R.id.recyclerViewVacations);
        Repository repository = new Repository(getApplication());
        vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Observe vacation data — LiveData automatically refreshes the list when the database changes
        repository.getmAllVacations().observe(this, vacations -> {
            vacationAdapter.setVacations(vacations);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle the back arrow in the action bar
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        // "Add Sample Data" menu option — inserts two example vacations
//        if (item.getItemId() == R.id.sample) {
//            Vacation maui = new Vacation(0, "Maui", "Hawaiian Haven", "02/01/2026", "02/10/2026", false, false);
//            repository.insert(maui);
//
//            Vacation miami = new Vacation(0, "Miami", "Miami Beach Bed and Breakfast", "03/01/2026", "03/10/2026", false, false);
//            repository.insert(miami);
//
//            Toast.makeText(this, "Sample data added!", Toast.LENGTH_SHORT).show();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
