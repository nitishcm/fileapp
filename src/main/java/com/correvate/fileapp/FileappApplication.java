package com.correvate.fileapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableAutoConfiguration
public class FileappApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileappApplication.class, args);
	}

}
