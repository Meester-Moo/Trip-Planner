package com.example.d308vacationproject.UI;

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

// Registration screen for creating new user accounts.
// Validates input, checks for duplicate usernames, and hashes the password before storing
public class RegisterActivity extends AppCompatActivity {

    private EditText editUsername;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new Repository(getApplication());
        editUsername = findViewById(R.id.editNewUsername);
        editPassword = findViewById(R.id.editNewPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);

        Button createButton = findViewById(R.id.buttonCreateAccount);
        createButton.setOnClickListener(v -> {
            attemptRegister();
        });
    }

    private void attemptRegister() {
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();

        // Validate all fields are filled
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check passwords match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hash the password and attempt to register
        String hashedPassword = PasswordHasher.hashPassword(password);
        boolean success = repository.registerUser(username, hashedPassword);

        if (success) {
            Toast.makeText(this, "Account created! Please log in.", Toast.LENGTH_SHORT).show();
            finish();   // Go back to logins screen
        } else {
            Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show();
        }

    }

}
