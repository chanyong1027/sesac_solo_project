package com.stagelog.Stagelog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class StagelogApplication {

	public static void main(String[] args) {
		SpringApplication.run(StagelogApplication.class, args);
	}

}
