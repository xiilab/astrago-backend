package com.xiilab.serverexperiment.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.serverexperiment.domain.mongo.Experiment;

@Repository
public interface ExperimentRepository extends MongoRepository<Experiment, String> {
}
