package com.ohjelmointi4Project.ohjelmointi4Project;

import java.sql.SQLException;

import org.apache.commons.codec.digest.Crypt;
import org.springframework.stereotype.Component;

@Component
public class UserHandling {

    private final DatabaseConf db;

    public UserHandling(DatabaseConf db) {
        this.db = db;
    }

    public void validateUserInfo(String username, String email, String password) {
        if (username == null || username.isBlank() || username.length() > 64) {
            throw new IllegalArgumentException("Invalid username");
        }
        if (email == null || email.isBlank() || email.length() > 128 || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
    }

    public void validateUserInfo(String username, String password) {
        if (username == null || username.isBlank() || username.length() > 64) {
            throw new IllegalArgumentException("Invalid username");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
    }

    public String hashPassword(String password) {
        return Crypt.crypt(password);
    }

    public void signup(String username, String email, String password) throws SQLException {
        validateUserInfo(username, email, password);

        if (db.usernameExists(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (db.emailExists(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        String hash = hashPassword(password);
        db.insertUser(username, hash, email);
    }

    public void login(String username, String password) throws SQLException {
        validateUserInfo(username, password);

        if (db.usernameExists(username)) {
            throw new IllegalArgumentException("Username already exits");
        }
        String hash = hashPassword(password);
        // TODO: Get db password hash and compare
        if (hash.equals(db.getUsernamePasswordHash())) {
            session.setAttribute("username", username);
            redirectTo /
        } else {
         // TODO: error handling   
        }
    }

    public boolean authenticateUser(String username, String password) throws SQLException {
        if (username == null || password == null)
            return false;

        String storedHash = db.getPasswordHashByUsername(username);
        if (storedHash == null)
            return false;

        String computed = Crypt.crypt(password, storedHash);
        return storedHash.equals(computed);
    }
}
