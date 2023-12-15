package com.xiilab.modulemonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.xiilab.modulecommon"})
public class ModuleMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModuleMonitorApplication.class, args);
	}

}
