package com.xiilab.serverbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {"com.xiilab.modulek8s", "com.xiilab.modulek8sdb", "com.xiilab.modulecommon", "com.xiilab.moduleuser", "com.xiilab.modulealert", "com.xiilab.serverbatch"})
public class ServerBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerBatchApplication.class, args);
	}

}
