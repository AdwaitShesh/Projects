package com.example.bookbridge.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM users WHERE email = :email OR username = :username")
    User getUserByEmailOrUsername(String email, String username);

    @Query("SELECT * FROM users WHERE (email = :usernameOrEmail OR username = :usernameOrEmail) AND password = :password")
    User login(String usernameOrEmail, String password);

    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUsers();
} 