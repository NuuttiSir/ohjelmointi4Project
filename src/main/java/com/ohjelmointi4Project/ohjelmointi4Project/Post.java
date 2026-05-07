package com.ohjelmointi4Project.ohjelmointi4Project;

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

    public static String messageBox(String username, String content) {
        return "<div class='post-box'><strong>" + username + "</strong><p>" + content
                + "</p><div class='thumbUpButton' type='button'><button class='button'><i class='bi bi-hand-thumbs-up'></i></button></div></div>";
    }
}
