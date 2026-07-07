package com.sanchez.juarez;

import com.sanchez.juarez.infrastructure.rest.adapters.JsonPlaceholderCustomerProviderAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JuarezApplication {

	@Autowired
	JsonPlaceholderCustomerProviderAdapter jsonPlaceholderCustomerProviderAdapter;
	public static void main(String[] args) {
		SpringApplication.run(JuarezApplication.class, args);
	}


}
