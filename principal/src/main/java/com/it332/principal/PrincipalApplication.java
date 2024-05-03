package com.it332.principal;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.it332.principal.Services.ExcelService;

@SpringBootApplication
public class PrincipalApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrincipalApplication.class, args);

		// Manually invoke ExcelService to insert LR data upon application startup
		ExcelService excelService = new ExcelService();
		try {
			excelService.insertLRData();
		} catch (IOException e) {
			e.printStackTrace(); // Handle or log the exception appropriately
		}
	}

}
