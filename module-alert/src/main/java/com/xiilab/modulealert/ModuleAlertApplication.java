package com.xiilab.modulealert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.xiilab.modulemonitor", "com.xiilab.modulealert"})
public class ModuleAlertApplication {
	public static void main(String[] args) {
		SpringApplication.run(ModuleAlertApplication.class, args);
	}
}
