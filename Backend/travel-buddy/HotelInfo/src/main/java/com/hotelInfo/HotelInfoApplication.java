package com.hotelInfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class HotelInfoApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelInfoApplication.class, args);
	}

}
