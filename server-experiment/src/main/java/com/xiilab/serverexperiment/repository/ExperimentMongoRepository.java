package com.xiilab.serverexperiment.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.serverexperiment.domain.mongo.Experiment;

@Repository
public interface ExperimentMongoRepository extends MongoRepository<Experiment, String> {
}
