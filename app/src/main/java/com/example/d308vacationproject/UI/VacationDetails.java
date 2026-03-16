package com.example.d308vacationproject.UI;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308vacationproject.R;
import com.example.d308vacationproject.database.Repository;
import com.example.d308vacationproject.entities.Excursion;
import com.example.d308vacationproject.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// Detailed view for creating or editing a vacation.
// Displays vacation fields (title, hotel, dates, notification checkboxes) and a list of associated excursions.
// Menu options: Save, Delete, Share. FAB adds a new excursion to this vacation.
public class VacationDetails extends AppCompatActivity {

    private int vacationID = -1;    // -1 means this is a new vacation (not yet saved)
    private EditText editName, editHotel, editStartDate, editEndDate;
    private Repository repository;
    private ExcursionAdapter excursionAdapter;
    private RecyclerView recyclerView;
    private CheckBox checkboxNotifyStart;
    private CheckBox checkboxNotifyEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_details);

        repository = new Repository(getApplication());

        // Get vacation ID from intent (-1 if creating a new vacation)
        vacationID = getIntent().getIntExtra("id", -1);

        // --- Bind UI elements ---
        editName = findViewById(R.id.titletext);
        editHotel = findViewById(R.id.hoteltext);
        editStartDate = findViewById(R.id.startDateText);
        editEndDate = findViewById(R.id.endDateText);
        checkboxNotifyStart = findViewById(R.id.checkboxNotifyStart);
        checkboxNotifyEnd = findViewById(R.id.checkboxNotifyEnd);

        // Adjust padding so content doesn't overlap system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Tapping date fields opens a date picker dialog instead of the keyboard
        editStartDate.setOnClickListener(v -> showDatePicker(true));
        editEndDate.setOnClickListener(v -> showDatePicker(false));

        // FAB: navigate to ExcursionDetails to add a new excursion to this vacation
        FloatingActionButton fab = findViewById(R.id.vacationDetailsFloatingActionButton);
        fab.setOnClickListener(view -> {
            String start = editStartDate.getText().toString().trim();
            String end = editEndDate.getText().toString().trim();

            // Require vacation dates before adding excursions (so excursion date validation works)
            if (start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this, "Set vacation dates first", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, ExcursionDetails.class);
            intent.putExtra("vacationID", vacationID);
            intent.putExtra("vacationStart", start);
            intent.putExtra("vacationEnd", end);
            startActivity(intent);
        });

        // --- Set up excursion RecyclerView ---
        recyclerView = findViewById(R.id.recyclerViewExcursions);
        excursionAdapter = new ExcursionAdapter(this, "", "");
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // If editing an existing vacation, load its data from the database
        if (vacationID != -1) {
            loadVacationFromDatabase();
        } else {
            excursionAdapter.setExcursions(new ArrayList<>());
        }
    }

    // Loads vacation data and associated excursions from the database using LiveData observers.
    // Both observers automatically update the UI when the underlying data changes.
    private void loadVacationFromDatabase() {
        // Observe all vacations and find the one matching our ID
        repository.getmAllVacations().observe(this, vacations -> {
            if (vacations == null) return;
            for (Vacation v : vacations) {
                if (v.getVacationID() == vacationID) {
                    // Populate form fields with vacation data
                    editName.setText(v.getVacationName());
                    editHotel.setText(v.getHotel());
                    editStartDate.setText(v.getStartDate());
                    editEndDate.setText(v.getEndDate());
                    checkboxNotifyStart.setChecked(v.isNotifyStart());
                    checkboxNotifyEnd.setChecked(v.isNotifyEnd());

                    // Update the excursion adapter with the vacation's date range
                    excursionAdapter.updateVacationDateRange(
                            v.getStartDate() != null ? v.getStartDate() : "",
                            v.getEndDate() != null ? v.getEndDate() : ""
                    );
                    break;
                }
            }
        });

        // Observe excursions for this vacation and update the RecyclerView
        repository.getAssociatedExcursions(vacationID).observe(this, excursions -> {
            List<Excursion> list = excursions != null ? new ArrayList<>(excursions) : new ArrayList<>();
            excursionAdapter.setExcursions(list);
        });
    }

    // Shows a DatePickerDialog and sets the selected date on the appropriate field
    private void showDatePicker(boolean isStart) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    String date = String.format("%02d/%02d/%d", month + 1, day, year);
                    if (isStart) editStartDate.setText(date);
                    else editEndDate.setText(date);
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_details, menu);
        return true;
    }

    // Handle menu item selections: Save, Delete, Share
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.vacationSave) {
            saveVacation();
            return true;
        }

        if (id == R.id.vacationDelete) {
            if (vacationID != -1) {
                // Prevent deletion if this vacation has associated excursions
                if (excursionAdapter.getItemCount() > 0) {
                    Toast.makeText(this, "Cannot delete vacation with associated excursions", Toast.LENGTH_LONG).show();
                    return true;
                }
                Vacation vacation = new Vacation(vacationID, "", "", "", "", false, false);
                repository.delete(vacation);
                Toast.makeText(this, "Vacation deleted", Toast.LENGTH_SHORT).show();
                finish();
            }
            return true;
        }

        if (id == R.id.vacationShare) {
            shareVacationDetails();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Builds a text summary of the vacation and its excursions, then opens the system share dialog
    private void shareVacationDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vacation: ").append(editName.getText()).append("\n");
        sb.append("Hotel: ").append(editHotel.getText()).append("\n");
        sb.append("Dates: ").append(editStartDate.getText()).append(" to ").append(editEndDate.getText()).append("\n\nExcursions:\n");

        List<Excursion> excursions = excursionAdapter.getmExcursions();
        if (excursions != null) {
            for (Excursion e : excursions) {
                sb.append("- ").append(e.getExcursionName()).append(" on ").append(e.getExcursionDate()).append("\n");
            }
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Vacation Details");
        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    // Validates input and saves (inserts or updates) the vacation to the database.
    // Schedules notifications if the user checked the notification boxes.
    private void saveVacation() {
        String name = editName.getText().toString().trim();
        String hotel = editHotel.getText().toString().trim();
        String start = editStartDate.getText().toString().trim();
        String end = editEndDate.getText().toString().trim();

        // Vacation name is required
        if (name.isEmpty()) {
            Toast.makeText(this, "Vacation name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate date format (MM/dd/yyyy) and ensure start date is not after end date
        if (!start.isEmpty() && !end.isEmpty()) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            try {
                LocalDate startDate = LocalDate.parse(start, fmt);
                LocalDate endDate = LocalDate.parse(end, fmt);
                if (startDate.isAfter(endDate)) {
                    Toast.makeText(this, "Start date must be before or equal to end date", Toast.LENGTH_LONG).show();
                    return;
                }
            } catch (DateTimeParseException e) {
                Toast.makeText(this, "Invalid date format (use MM/dd/yyyy)", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Create vacation object (pass 0 for new vacations so Room auto-generates the ID)
        Vacation vacation = new Vacation(
                vacationID == -1 ? 0 : vacationID,
                name, hotel, start, end,
                checkboxNotifyStart.isChecked(),
                checkboxNotifyEnd.isChecked()
        );

        // Insert or update depending on whether this is a new or existing vacation
        if (vacationID == -1) {
            repository.insert(vacation);
            Toast.makeText(this, "Vacation created", Toast.LENGTH_SHORT).show();
        } else {
            repository.update(vacation);
            Toast.makeText(this, "Vacation updated", Toast.LENGTH_SHORT).show();
        }

        // Schedule notifications if the user opted in
        if (checkboxNotifyStart.isChecked() && !start.isEmpty()) {
            scheduleNotification(start, "Vacation " + name + " is starting!", vacationID * 10 + 1);
        }
        if (checkboxNotifyEnd.isChecked() && !end.isEmpty()) {
            scheduleNotification(end, "Vacation " + name + " is ending!", vacationID * 10 + 2);
        }

        finish();
    }

    // Schedules a notification using AlarmManager to fire at midnight on the given date.
    // requestCode must be unique per alarm so they don't overwrite each other.
    @SuppressLint("ScheduleExactAlarm")
    private void scheduleNotification(String dateStr, String message, int requestCode) {
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            long millis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("message", message);
            intent.putExtra("notification_id", requestCode);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
        } catch (DateTimeParseException e) {
            // Silently skip if date string is somehow invalid (shouldn't happen after validation)
        }
    }

    // Handle the back/up arrow in the action bar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
