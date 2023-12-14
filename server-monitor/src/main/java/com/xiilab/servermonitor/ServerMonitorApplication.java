package com.xiilab.servermonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(scanBasePackages = {"com.xiilab.modulek8s", "com.xiilab.modulecommon", "com.xiilab.moduleuser", "com.xiilab.modulemonitor"})
public class ServerMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerMonitorApplication.class, args);
	}

}
