package com.ohjelmointi4Project.ohjelmointi4Project;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

//TODO: en diggaa etta database nimi on databaseconf vois muuttaa interfaceks jos se olis oikee tapa. pitaa tsiigaa
@Component
public class DatabaseConf {
    @Autowired
    private Environment env;

    static String dbPath;

    @PostConstruct
    private void init() {
        DatabaseConf.dbPath = env.getProperty("db.path");
        try {
            initDB();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    // TODO: Init on every startup so delete previous on starrtup
    public void initDB() throws SQLException {
        File dbFile = new File(dbPath);
        boolean dbExists = dbFile.exists() && !dbFile.isDirectory();

        if (!dbExists) {
            try (Connection dbConn = getConnection();
                    Statement createStatement = dbConn.createStatement()) {

                // TODO: lisaa created_at users tableen as timestamp integer whatever
                String createUsersTable = """
                        CREATE table users(
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        username varchar(64) NOT NULL UNIQUE,
                                email varchar(128) NOT NULL UNIQUE,
                                password varchar(128) NOT NULL
                        )
                        """;
                String createMessagesTable = """
                        CREATE table messages(
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            message varchar(140),
                            like_count integer DEFAULT 0,
                            comment_count integer DEFAULT 0,
                            user_id INTEGER,
                            created_at INTEGER,
                            FOREIGN KEY (user_id) REFERENCES users(id)
                        )
                        """;

                createStatement.executeUpdate(createMessagesTable);
                createStatement.executeUpdate(createUsersTable);
            }
        } else {
            System.out.println("Database already exists at: " + dbPath);
        }
    }

    public void insertMessage(String text, String username) throws SQLException {
        int user_id = 0;
        long now = Instant.now().toEpochMilli();

        String findUserID = """
                    SELECT id FROM users WHERE username = ?
                """;

        String insertMessage = """
                INSERT INTO messages (message, user_id, created_at) VALUES (?, ?, ?) """;

        try (Connection dbConn = getConnection()) {
            dbConn.setAutoCommit(false);
            try (PreparedStatement ps0 = dbConn.prepareStatement(findUserID)) {
                ps0.setString(1, username);
                try (ResultSet rs = ps0.executeQuery()) {
                    if (rs.next()) {
                        user_id = rs.getInt("id");
                    } else {
                        throw new SQLException("Username not found in DB");
                    }
                }
            }
            try (PreparedStatement ps = dbConn.prepareStatement(insertMessage)) {
                ps.setString(1, text);
                ps.setInt(2, user_id);
                ps.setLong(3, now);
                ps.executeUpdate();
            }

            dbConn.commit();
        }
    }

    public void insertUser(String username, String password, String email) throws SQLException {
        String insertUser = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        try (Connection dbConn = getConnection()) {
            dbConn.setAutoCommit(false);
            try (PreparedStatement ps = dbConn.prepareStatement(insertUser)) {
                ps.setString(1, username);
                ps.setString(2, email);
                ps.setString(3, password);
                ps.executeUpdate();
            }
            dbConn.commit();
        }
    }

    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
        try (Connection c = getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";
        try (Connection c = getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public String getPasswordHashByUsername(String username) throws SQLException {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection c = getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("password") : null;
            }
        }
    }

    public List<Post> getAllPosts() throws SQLException {
        String sql = """
                SELECT messages.id, messages.message, messages.created_at, users.username FROM messages
                JOIN users ON messages.user_id = users.id
                ORDER BY messages.created_at DESC""";
        List<Post> posts = new ArrayList<>();
        try (Connection c = getConnection();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                posts.add(new Post(rs.getString("username"), rs.getString("message"), rs.getLong("created_at")));
            }
        }
        return posts;
    }

    public List<Post> getAllPostsFromUser(String username) throws SQLException {
        String sql = """
                SELECT messages.id, messages.message, messages.created_at, users.username FROM messages
                JOIN users ON messages.user_id = users.id
                WHERE users.username = ?
                ORDER BY messages.created_at DESC
                """;
        List<Post> posts = new ArrayList<>();
        try (Connection c = getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    posts.add(new Post(rs.getString("username"), rs.getString("message"), rs.getLong("created_at")));
                }
            }
        }
        return posts;
    }
}
