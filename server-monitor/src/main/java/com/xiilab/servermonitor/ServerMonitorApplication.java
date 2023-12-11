package com.xiilab.servermonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.xiilab.modulemonitor"})
@EnableAsync
public class ServerMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerMonitorApplication.class, args);
	}

}
