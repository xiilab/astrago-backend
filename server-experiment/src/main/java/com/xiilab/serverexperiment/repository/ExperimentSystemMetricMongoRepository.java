package com.xiilab.serverexperiment.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.serverexperiment.domain.mongo.ExperimentSystemMetric;

@Repository
public interface ExperimentSystemMetricMongoRepository extends MongoRepository<ExperimentSystemMetric, String> {
}
