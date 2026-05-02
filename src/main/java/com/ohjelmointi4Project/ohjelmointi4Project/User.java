package com.ohjelmointi4Project.ohjelmointi4Project;

public class User {
   	private String username;
	private String password;
	private String email;

	public User() {
        // Empty
	}

	public User(String name, String password, String email) {
		this.username = name;
		this.password = password;
		this.email = email;
	}

	public String getUserName() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public String getEmail() {
		return this.email;
	}
}
