package com.be.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Slf4j
@SpringBootApplication
@EnableScheduling
@EnableSwagger2
public class BESampleApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(BESampleApplication.class, args);
	}
}
