package com.xiilab.servermonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.xiilab.modulek8s", "com.xiilab.modulecommon", "com.xiilab.moduleuser", "com.xiilab.servermonitor"})
public class ServerMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerMonitorApplication.class, args);
	}

}
