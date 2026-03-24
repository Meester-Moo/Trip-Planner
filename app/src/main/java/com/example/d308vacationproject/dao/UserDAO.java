package com.example.d308vacationproject.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.d308vacationproject.entities.User;

// Data Access Object (DAO) for user authentication

// These methods do not return LiveData unlike TripDAO and ExcursionDAO because
// Login/registering are not things that are continually observed
@Dao
public interface UserDAO {

    // Insert a new user. ABORT -> throw an error if username
    // already exists (because of the unique index on username)
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(User user);

    // Find a user by username and password hash
    // If this returns a User object, the credentials are valid.
    // If it returns null, the login failed
    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    User login(String username, String password);

    // Check if a username is already taken (used during registration)
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);
}
