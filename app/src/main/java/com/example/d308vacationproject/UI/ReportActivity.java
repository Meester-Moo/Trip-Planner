package com.example.d308vacationproject.UI;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.d308vacationproject.R;
import com.example.d308vacationproject.database.Repository;
import com.example.d308vacationproject.entities.Excursion;
import com.example.d308vacationproject.entities.PlannerItem;
import com.example.d308vacationproject.entities.Trip;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Report screen that generates a formatted summary of all trips and excursions.
// Demonstrates Polymorphism by using PlannerItem references to handle both
// Trip and Excursion objects through the same getSummary() method.
public class ReportActivity extends AppCompatActivity {

    private String reportText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Enable the back arrow in the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Set the report timestamp to the current date and time
        TextView timestampView = findViewById(R.id.reportTimestamp);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a"));
        timestampView.setText("Generated: " + timestamp);

        // Show the logged-in user's name on the report
        SharedPreferences prefs = getSharedPreferences("TripPlannerPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("loggedInUserId", -1);
        String username = prefs.getString("loggedInUsername", "Unknown");
        TextView userView = findViewById(R.id.reportUser);
        userView.setText("User: " + username);

        // Generate the report
        generateReport(userId);

        // Copy to Clipboard button
        Button copyButton = findViewById(R.id.buttonCopyReport);
        copyButton.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Trip Report", reportText);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Report copied to clipboard!", Toast.LENGTH_SHORT).show();
        });

    }

    private void generateReport(int userId) {
        Repository repository = new Repository(getApplication());

        // Fetch all trips for this user
        List<Trip> trips = repository.getTripsByUserDirect(userId);

        // If no trips exist, show the hidden "no data" message and stop
        if (trips.isEmpty()) {
            findViewById(R.id.noDataMessage).setVisibility(View.VISIBLE);
            // Hide the export report button if there's no data to copy
            findViewById(R.id.buttonCopyReport).setVisibility(View.GONE);
            return;
        }

        // Start building the text version of the report for clipboard export
        StringBuilder sb = new StringBuilder();
        sb.append("=== Trip Planner Report ===\n");
        sb.append(((TextView) findViewById(R.id.reportTimestamp)).getText()).append("\n");
        sb.append(((TextView) findViewById(R.id.reportUser)).getText()).append("\n\n");

        // --- Build the Trips Table ---
        TableLayout tripsTable = findViewById(R.id.tripsTable);

        // Add header row
        tripsTable.addView(createHeaderRow(new String[]{"Trip Name", "Hotel", "Start", "End", "#"}));

        sb.append("TRIPS\n");
        sb.append(String.format("%-20s %-15s %-12s %-12s %s\n", "Trip Name", "Hotel", "Start", "End", "#"));
        sb.append("-------------------------------------------------------------\n");

        // Add a data row for each trip
        for (Trip trip : trips) {
            List<Excursion> excursions = repository.getAssociatedExcursionsDirect(trip.getTripID());
            tripsTable.addView(createDataRow(new String[]{trip.getTripName(), trip.getHotel(), trip.getStartDate(), trip.getEndDate(), String.valueOf(excursions.size())}));

            sb.append(String.format("%-20s %-15s %-12s %-12s %s\n", trip.getTripName() != null ? trip.getTripName() : "", trip.getHotel() != null ? trip.getHotel() : "", trip.getStartDate() != null ? trip.getStartDate() : "", trip.getEndDate() != null ? trip.getEndDate() : "", excursions.size()));
        }

        // --- Build the Excursions Table ---
        TableLayout excursionsTable = findViewById(R.id.excursionsTable);

        // Add header row
        excursionsTable.addView(createHeaderRow(new String[]{"Excursion", "Date", "Trip"}));

        sb.append("\nEXCURSION DETAILS\n");
        sb.append(String.format("%-20s %-12s %s\n", "Excursion", "Date", "Trip"));
        sb.append("---------------------------------------------\n");


        // Collect ALL excursions across all trips into one list.

        // This is also where Polymorphism is demonstrated.
        // We use PlannerItem to treat both trips and excursions
        // through the same interface
        List<PlannerItem> allItems = new ArrayList<>();

        for (Trip trip : trips) {
            List<Excursion> excursions = repository.getAssociatedExcursionsDirect(trip.getTripID());

            for (Excursion excursion : excursions) {
                excursionsTable.addView(createDataRow(new String[]{excursion.getExcursionName(), excursion.getExcursionDate(), trip.getTripName()}));

                sb.append(String.format("%-20s %-12s %s\n", excursion.getExcursionName() != null ? excursion.getExcursionName() : "", excursion.getExcursionDate() != null ? excursion.getExcursionDate() : "", trip.getTripName() != null ? trip.getTripName() : ""));
            }

            // Add to PlannerItem list for the summary section
            allItems.add(trip);
            allItems.addAll(excursions);
        }

        // If there are no excursions at all, show a message in the excursion table
        if (allItems.size() == trips.size()) {
            // Only trips in the list, no excursions were added
            excursionsTable.addView(createDataRow(new String[]{"No excursions found", "", ""}));
            sb.append("No excursions found\n");
        }

        // Polymorphism - append summaries using PlannerItem
        sb.append("\nTRIP SUMMARIES\n");
        sb.append("---------------------------------------------\n");
        for (PlannerItem item : allItems) {
            sb.append(item.getSummary()).append("\n");
        }

        // Store the completed report text for the clipboard button
        reportText = sb.toString();

    }

    // Creates a bold header row for a table.
    // Takes an array of column titles and returns a styled TableRow.
    private TableRow createHeaderRow(String[] headers) {
        TableRow row = new TableRow(this);
        row.setBackgroundColor(Color.parseColor("#E0E0E0"));
        row.setPadding(8, 12, 8, 12);

        for (String header : headers) {
            TextView cell = new TextView(this);
            cell.setText(header);
            cell.setTypeface(null, Typeface.BOLD);
            cell.setPadding(12, 8, 12, 8);
            cell.setTextSize(13);
            row.addView(cell);
        }

        return row;
    }

    // Creates a data row for a table.
    // Takes an array of cell values and returns a styled TableRow with alternating colors
    private TableRow createDataRow(String[] values) {
        TableRow row = new TableRow(this);
        row.setPadding(8, 8, 8, 8);

        for (String value : values) {
            TextView cell = new TextView(this);
            cell.setText(value != null ? value : "");
            cell.setPadding(12, 8, 12, 8);
            cell.setTextSize(12);
            row.addView(cell);
        }

        return row;
    }

    // Handle the back/up arrow in the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
