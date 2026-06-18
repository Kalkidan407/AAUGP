package com.example.aaugp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AaugpApplication {

	public static void main(String[] args) {
		SpringApplication.run(AaugpApplication.class, args);
	}

}
