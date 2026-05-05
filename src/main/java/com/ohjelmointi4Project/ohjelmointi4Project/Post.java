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
}
