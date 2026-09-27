package com.javid.reviewms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ReviewmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReviewmsApplication.class, args);
	}

}
