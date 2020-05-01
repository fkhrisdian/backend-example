package com.kaspro.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KasproBankApplication {
	public static void main(String[] args) {
		SpringApplication.run(KasproBankApplication.class, args);
	}
}
