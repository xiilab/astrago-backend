package com.xiilab.serverexperiment.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.serverexperiment.domain.mongo.Workload;

@Repository
public interface WorkloadLogRepository extends MongoRepository<Workload, String> {
	Optional<Workload> findByNameAndId(String name, String uuid);
}
