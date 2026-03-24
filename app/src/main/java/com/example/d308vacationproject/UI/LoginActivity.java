package com.example.d308vacationproject.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.d308vacationproject.R;
import com.example.d308vacationproject.database.PasswordHasher;
import com.example.d308vacationproject.database.Repository;
import com.example.d308vacationproject.entities.User;

// Login screen - the first screen the users see when opening the app.
// Validates credentials against the database and stores the session in SharedPreferences.
public class LoginActivity extends AppCompatActivity {

    private EditText editUsername;
    private EditText editPassword;
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new Repository(getApplication());
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);

        // Login button - validates credentials and navigates to MainActivity on success
        Button loginButton = findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(v -> attemptLogin());

        Button registerButton = findViewById(R.id.buttonRegister);
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        // Basic input validation
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hash the password and check against database
        String hashedPassword = PasswordHasher.hashPassword(password);
        User user = repository.loginUser(username, hashedPassword);

        if (user != null) {
            // SUCCESS - save the user's session using SharedPreferences
            // SharedPreferences is a small key-value store that persists
            // when the app opens and closes
            SharedPreferences prefs = getSharedPreferences("TripPlannerPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("loggedInUserId", user.getUserID());
            editor.putString("loggedInUsername", user.getUsername());
            editor.apply();

            Toast.makeText(this, "Welcome, " + user.getUsername() + "!", Toast.LENGTH_SHORT).show();

            // Navigate to MainActivity (home screen)
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();   // Remove login from the "back" stack so that "Back" doesn't return here
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }

}
