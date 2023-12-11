package com.xiilab.servercore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.xiilab.modulek8s", "com.xiilab.modulecommon", "com.xiilab.moduleuser"})
public class ServerCoreApplication {
	public static void main(String[] args) {
		SpringApplication.run(ServerCoreApplication.class, args);
	}
}
