package com.example.d308vacationproject.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

// Room entity representing a user account in the "users" table.
// The @Index with unique=true ensures no two users can have the same username
// This also demonstrates Encapsulation -> all fields are private with controlled access (public methods)
@Entity(tableName = "users", indices = {@Index(value = "username", unique = true)})
public class User {

    @PrimaryKey(autoGenerate = true)
    private int userID;

    private String username;
    private String password; // This stores a hashed password, never plaintext

    public User(int userID, String username, String password) {
        this.userID = userID;
        this.username = username;
        this.password = password;
    }

    // --- Getters and Setters ---


    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
