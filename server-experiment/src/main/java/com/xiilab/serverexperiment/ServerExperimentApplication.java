package com.xiilab.serverexperiment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.xiilab.serverexperiment", "com.xiilab.modulecommon",
	"com.xiilab.modulek8sdb"})
public class ServerExperimentApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerExperimentApplication.class, args);
	}

}
