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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/frontpage")
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
            return Post.messageBox(username, content);
        } catch (SQLException e) {
            return "DB error";
        }
    }

    @GetMapping("/profilepage")
    public String getProfilePage(HttpSession session, Model model) throws SQLException {
        List<Post> usersPosts = db.getAllPostsFromUser((String) session.getAttribute("username"));
        String username = (String) session.getAttribute("username");

        model.addAttribute("userPosts", usersPosts);
        model.addAttribute("username", username);

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
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        return "login.html";
    }

    @PostMapping("/checkSignup")
    public String checkSignup(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session, RedirectAttributes redirectAttributes) throws SQLException {
        try {
            userHandling.signup(username, email, password);
            session.setAttribute("username", username);
            session.setAttribute("email", email);
            return "redirect:/frontpage";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("prevUsername", username);
            redirectAttributes.addFlashAttribute("prevEmail", email);
            return "redirect:/signup";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Something went wrong, try again");
            return "redirect:/signup";
        }
    }

    @PostMapping("/checkLogin")
    public String checkLogin(@RequestParam String username, @RequestParam String password,
            HttpSession session, RedirectAttributes redirectAttributes)
            throws SQLException {
        try {
            if (userHandling.authenticateUser(username, password)) {
                session.setAttribute("username", username);
                session.setAttribute("email", db.getEmailOfUser(username));
                return "redirect:/frontpage";
            } else {
                redirectAttributes.addFlashAttribute("error", "Username or Password is wrong");
                return "redirect:/login";
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("prevUsername", username);
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Something went wrong, try again");
            return "redirect:/login";
        }
    }

    // TODO: Add error messages
    @PostMapping("/changeUsername")
    public String changeUsername(@RequestParam String newUsername, HttpSession session) {
        try {
            if (db.changeUsername((String) session.getAttribute("username"), newUsername)) {
                session.setAttribute("username", newUsername);
                return "redirect:/settingspage";
            } else {
                return "redirect:/settingspage";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/settingspage";
        }
    }

    // TODO: Add error messages
    @PostMapping("/changeEmail")
    public String changeEmail(@RequestParam String newEmail, HttpSession session) {
        try {
            String currentEmail = (String) session.getAttribute("email");
            System.out.println(currentEmail);
            if (currentEmail != null && db.changeEmail(currentEmail, newEmail)) {
                session.setAttribute("email", newEmail);
                return "redirect:/settingspage";
            } else {
                return "redirect:/settingspage";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/settingspage";
        }
    }

    // TODO: Add error messages
    @PostMapping("/changePassword")
    public String changePassword(@RequestParam String newPassword, HttpSession session) {
        try {
            String username = (String) session.getAttribute("username");
            if (username != null && db.changePassword(username, newPassword)) {
                return "redirect:/settingspage";
            } else {
                return "redirect:/settingspage";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/settingspage";
        }
    }

    @PostMapping("/deleteUser")
    public String deleteUser(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            String username = (String) session.getAttribute("username");
            if (db.deleteUser(username)) {
                return "redirect:/login";
            } else {
                redirectAttributes.addFlashAttribute("error", "Something went wrong, try again");
                return "redirect:/settingspage";
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Something went wrong, try again");
            return "redirect:/settingspage";
        }
    }
}