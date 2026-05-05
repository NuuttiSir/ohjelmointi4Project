package com.ohjelmointi4Project.ohjelmointi4Project;

import java.sql.SQLException;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;

@SpringBootApplication
@Controller
public class Ohjelmointi4ProjectApplication {

    private final DatabaseConf db;
    private final UserHandling userHandling;

    public Ohjelmointi4ProjectApplication(DatabaseConf db, UserHandling userHandling) {
        this.db = db;
        this.userHandling = userHandling;
    }

    public static void main(String[] args) {
        SpringApplication.run(Ohjelmointi4ProjectApplication.class, args);
    }

    @GetMapping("/")
    public String frontPage(Model model) throws SQLException {
        List<Post> posts = db.getAllPosts();
        model.addAttribute("posts", posts);
        return "index.html";
    }

    @PostMapping("/posts")
    @ResponseBody
    public String createPost(@RequestParam("content") String content, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "Not logged in";
        }
        try {
            db.insertMessage(content, username);
            return "<div class='post-box'><strong>" + username + "</strong><p>" + content + "</p></div>";
        } catch (SQLException e) {
            return "DB error";
        }
    }

    // @PostMapping("/preview")
    // @ResponseBody
    // public String inputtedTextToShow(@RequestParam(value = "demo-multi-string",
    // defaultValue = "") String text,
    // String username) {
    // try {
    // db.insertMessage(text, username);
    // } catch (SQLException e) {
    // e.printStackTrace();
    // }
    // return "<div class=\"post-box\"><strong>" + username + "</strong><p>" + text
    // + "</p></div>";
    // }

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

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "login.html";
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        return "login.html";
    }

    @PostMapping("/checkSignup")
    public String checkSignup(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password) throws SQLException {

        try {
            userHandling.signup(username, email, password);
            return "redirect:/";
        } catch (Exception e) {
            return "redirect:/signup";
        }
    }

    @PostMapping("/checkLogin")
    public String checkLogin(@RequestParam String username, @RequestParam String password, HttpSession session)
            throws SQLException {
        try {
            if (userHandling.authenticateUser(username, password)) {
                session.setAttribute("username", username);
                return "redirect:/";
            } else {
                return "redirect:/login";
            }
        } catch (Exception e) {
            return "redirect:/login";
        }
    }
}
