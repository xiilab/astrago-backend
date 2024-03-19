package com.xiilab.serverbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.xiilab.modulek8s", "com.xiilab.modulek8sdb", "com.xiilab.modulecommon", "com.xiilab.modulemonitor", "com.xiilab.moduleuser", "com.xiilab.serverbatch"})
public class ServerBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerBatchApplication.class, args);
	}

}
