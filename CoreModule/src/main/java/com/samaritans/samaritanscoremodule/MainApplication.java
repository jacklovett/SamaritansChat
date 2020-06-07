package com.samaritans.samaritanscoremodule;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@SpringBootApplication
@EntityScan(basePackageClasses = { MainApplication.class, Jsr310JpaConverters.class })
public class MainApplication {

	@PostConstruct
	void init() {
		// CET - Central European Time - Change for Charities local timezone
		TimeZone.setDefault(TimeZone.getTimeZone("CET"));
	}

	public static void main(final String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

}
