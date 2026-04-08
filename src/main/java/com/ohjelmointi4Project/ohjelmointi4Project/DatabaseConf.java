package com.ohjelmointi4Project.ohjelmointi4Project;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

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
        this.dbPath = env.getProperty("db.path");
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

    public void initDB() throws SQLException {
        File dbFile = new File(dbPath);
        boolean dbExists = dbFile.exists() && !dbFile.isDirectory();

        if (!dbExists) {
            try (Connection dbConn = getConnection();
                    Statement createStatement = dbConn.createStatement()) {

                String createMessagesTable = """
                        CREATE table messages(
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            message varchar(140)
                        )
                        """;

                createStatement.executeUpdate(createMessagesTable);
            }
        } else {
            System.out.println("Database already exists at: " + dbPath);
        }
    }

    public void insertMessage(String text) throws SQLException {
        String insertMessage = """
                INSERT INTO messages (message) VALUES (?) """;

        try (Connection dbConn = getConnection()) {
            dbConn.setAutoCommit(false);
            try (PreparedStatement ps = dbConn.prepareStatement(insertMessage)) {
                ps.setString(1, text);
                ps.executeUpdate();
            }

            dbConn.commit();
        }
    }
}