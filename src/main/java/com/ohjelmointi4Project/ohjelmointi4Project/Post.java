package com.ohjelmointi4Project.ohjelmointi4Project;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Post {
    private String username;
    private String message;
    private long createdAt;

    public Post(String username, String message, long createdAt) {
        this.username = username;
        this.message = message;
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return this.username;
    }

    public String getMessage() {
        return this.message;
    }

    public Long getCreatedAt() {
        return this.createdAt;
    }

    // Thymeleaf calls this func to get the time as per some treaty of versailles
    // that the get part is removed and i can call it by the rest of the func name
    // AS WELL AS have to make wrapper because of static things that i still dont
    // really understand
    public String getCreationTime() {
        return getCreationTime(this.createdAt);
    }

    public static String getCreationTime(Long createdAt) {
        return Instant.ofEpochMilli(createdAt)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    public static String messageBox(String username, String content, Long createdAt) {
        String postId = username + "_" + content;
        return "<div class='post-box'>"
            + "<strong>" + username + "</strong>"
            + "<p>" + content + "</p>"
            + "<button class='like-button' data-post-id='" + postId + "' onclick='toggleLike(this.dataset.postId)'>"
            + "<i class='bi bi-hand-thumbs-up'></i></button>"
            + "<small>" + getCreationTime(createdAt) + "</small>"
            + "</div>";
    }
}
