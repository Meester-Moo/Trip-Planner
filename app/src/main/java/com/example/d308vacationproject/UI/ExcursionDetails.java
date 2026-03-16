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

import com.example.d308vacationproject.R;
import com.example.d308vacationproject.database.Repository;
import com.example.d308vacationproject.entities.Excursion;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;

// Detailed view for creating or editing an excursion.
// Displays excursion fields (title, date, notification checkbox).
// Menu options: Save (always visible), Delete (only visible for existing excursions).
// Validates that the excursion date falls within the parent vacation's date range.
public class ExcursionDetails extends AppCompatActivity {

    private EditText editName;
    private EditText editDate;
    private CheckBox checkboxNotify;
    private Repository repository;

    private int excursionID = -1;       // -1 means this is a new excursion (not yet saved)
    private int vacationID = -1;        // Parent vacation's ID

    private String vacationStart;       // Parent vacation's start date (for date range validation)
    private String vacationEnd;         // Parent vacation's end date (for date range validation)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_excursion_details);

        // Adjust padding so content doesn't overlap system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new Repository(getApplication());

        // --- Bind UI elements ---
        editName = findViewById(R.id.editExcursionName);
        editDate = findViewById(R.id.editExcursionDate);
        checkboxNotify = findViewById(R.id.checkboxNotify);

        // --- Read intent extras passed from ExcursionAdapter or VacationDetails FAB ---
        excursionID = getIntent().getIntExtra("excursionID", -1);
        vacationID = getIntent().getIntExtra("vacationID", -1);
        String name = getIntent().getStringExtra("name");
        String date = getIntent().getStringExtra("date");

        // Populate fields if editing an existing excursion
        if (name != null) editName.setText(name);
        if (date != null) editDate.setText(date);

        boolean notify = getIntent().getBooleanExtra("notify", false);
        checkboxNotify.setChecked(notify);

        // Tapping the date field opens a date picker dialog instead of the keyboard
        editDate.setOnClickListener(v -> showDatePicker());

        // Store the parent vacation's date range for excursion date validation
        vacationStart = getIntent().getStringExtra("vacationStart");
        vacationEnd = getIntent().getStringExtra("vacationEnd");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursion_details, menu);

        // Hide the Delete option if this is a new excursion (nothing to delete yet)
        if (excursionID == -1) {
            MenuItem deleteItem = menu.findItem(R.id.excursionDelete);
            if (deleteItem != null) {
                deleteItem.setVisible(false);
            }
        }
        return true;
    }

    // Handle menu item selections: Save, Delete
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.excursionSave) {
            saveOrUpdateExcursion();
            return true;
        }

        if (id == R.id.excursionDelete) {
            deleteExcursion();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Validates input and saves (inserts or updates) the excursion to the database.
    // Schedules a notification if the user checked the notification box.
    private void saveOrUpdateExcursion() {
        String name = editName.getText().toString().trim();
        String date = editDate.getText().toString().trim();

        // Validate that excursion date falls within the parent vacation's date range
        if (vacationStart != null && vacationEnd != null && !vacationStart.isEmpty() && !vacationEnd.isEmpty()) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            try {
                LocalDate excDate = LocalDate.parse(date, fmt);
                LocalDate vacStart = LocalDate.parse(vacationStart, fmt);
                LocalDate vacEnd = LocalDate.parse(vacationEnd, fmt);
                if (excDate.isBefore(vacStart) || excDate.isAfter(vacEnd)) {
                    Toast.makeText(this, "Excursion date must be within vacation dates", Toast.LENGTH_LONG).show();
                    return;
                }
            } catch (DateTimeParseException e) {
                Toast.makeText(this, "Invalid date format (use MM/dd/yyyy)", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Validate that name is provided and date matches MM/DD/YYYY pattern
        if (name.isEmpty() || date.isEmpty() || !date.matches("\\d{2}/\\d{2}/\\d{4}")) {
            Toast.makeText(this, "Name and valid MM/DD/YYYY date required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prevent saving if no valid vacation is associated
        if (vacationID <= 0) {
            Toast.makeText(this, "Invalid vacation ID", Toast.LENGTH_LONG).show();
            return;
        }

        // Create excursion object (pass 0 for new excursions so Room auto-generates the ID)
        Excursion excursion = new Excursion(
                excursionID == -1 ? 0 : excursionID,
                name, date, vacationID,
                checkboxNotify.isChecked()
        );

        if (excursionID == -1) {
            // Insert new excursion with callback to get the generated ID for notifications
            repository.insert(excursion, newId -> {
                if (newId > 0) {
                    Toast.makeText(this, "Excursion added", Toast.LENGTH_SHORT).show();
                    if (checkboxNotify.isChecked()) {
                        scheduleNotification(date, "Excursion " + name + " today!", (int) newId * 10);
                    }
                } else {
                    Toast.makeText(this, "Failed to add excursion", Toast.LENGTH_LONG).show();
                }
                finish();
            });
        } else {
            // Update existing excursion
            repository.update(excursion);
            Toast.makeText(this, "Excursion updated", Toast.LENGTH_SHORT).show();
            if (checkboxNotify.isChecked()) {
                scheduleNotification(date, "Excursion " + name + " today!", excursionID * 10);
            }
            finish();
        }
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

    // Deletes the current excursion from the database and navigates back
    private void deleteExcursion() {
        if (excursionID == -1) {
            Toast.makeText(this, "Cannot delete a new excursion", Toast.LENGTH_SHORT).show();
            return;
        }

        Excursion excursion = new Excursion(excursionID, "", "", vacationID, false);
        repository.delete(excursion);
        Toast.makeText(this, "Excursion deleted", Toast.LENGTH_SHORT).show();
        finish();
    }

    // Shows a DatePickerDialog and sets the selected date on the date field
    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    String selectedDate = String.format("%02d/%02d/%d", month + 1, day, year);
                    editDate.setText(selectedDate);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );
        picker.show();
    }

    // Handle the back/up arrow in the action bar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
