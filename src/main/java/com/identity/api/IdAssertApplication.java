package com.identity.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@SpringBootApplication
public class IdAssertApplication {
	private static final Logger log = LoggerFactory.getLogger(IdAssertApplication.class);
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins(
								"http://localhost:3000",
								"http://staging.roottorisebotanicals.com",
								"http://www.roottorisebotanicals.com"
						);
			}
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(IdAssertApplication.class, args);
	}

}
