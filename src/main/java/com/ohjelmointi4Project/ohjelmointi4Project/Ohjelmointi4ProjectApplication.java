package com.ohjelmointi4Project.ohjelmointi4Project;

import java.sql.SQLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@Controller
public class Ohjelmointi4ProjectApplication {

    private final DatabaseConf db;

    public Ohjelmointi4ProjectApplication(DatabaseConf db) {
        this.db = db;
    }

    public static void main(String[] args) {
        SpringApplication.run(Ohjelmointi4ProjectApplication.class, args);
    }

    @GetMapping("/")
    public String frontPage() {
        return "index.html";
    }

    @PostMapping("/preview")
    @ResponseBody
    public String inputtedTextToShow(@RequestParam(value = "demo-multi-string", defaultValue = "") String text) {
        try {
            db.insertMessage(text);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "<p>" + text + "</p>";
    }

    @GetMapping("/profilepage")
    public String getProfilePage() {
        return "profilepage.html";
    }

    @GetMapping("/settingspage")
    public String getSettingsPage() {
        return "settingspage.html";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup.html";
    }

    @PostMapping("/checkSignup")
    public String checkSignup(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password) throws SQLException {
        db.insertUser(username, password, email);
        return "redirect:/";
    }
}
