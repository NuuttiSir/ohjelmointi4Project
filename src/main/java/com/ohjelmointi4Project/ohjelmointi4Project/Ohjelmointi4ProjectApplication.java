package com.ohjelmointi4Project.ohjelmointi4Project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class Ohjelmointi4ProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(Ohjelmointi4ProjectApplication.class, args);
	}

	@GetMapping("/")
	public String frontPage() {
		return "index.html";
	}

}