package com.br.soaone.SpringBootWebLogic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class SpringBootWebLogicApplication {

	@RequestMapping("/myweb")
	String home() {
		return "Spring Boot Teste!";
	}

	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebLogicApplication.class, args);
	}

}
