package com.xiilab.serverexperiment.domain.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

@Document(collection = "workloads")
@Builder
@Getter
public class Workload {
	@Id
	//uuid
	private String id;
	private String name;
	private String workspace;
}
