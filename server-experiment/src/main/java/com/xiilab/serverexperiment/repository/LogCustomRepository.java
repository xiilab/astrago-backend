package com.xiilab.serverexperiment.repository;

import java.util.List;
import java.util.Objects;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ObjectOperators.ObjectToArray;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.xiilab.serverexperiment.dto.TrainDataSearchDTO;
import com.xiilab.serverexperiment.repository.dto.UniqueKeysResult;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LogCustomRepository {
	private final MongoTemplate mongoTemplate;

	public List<String> getExperimentKeysByIds(List<String> ids) {
		Criteria workloadCriteria = Criteria.where("workload_id").in(ids);
		MatchOperation match = Aggregation.match(workloadCriteria);
		ProjectionOperation projectToArray = Aggregation.project()
			.and(ObjectToArray.valueOfToArray("metrics"))
			.as("keys");
		UnwindOperation unwind = Aggregation.unwind("keys");
		GroupOperation groupUniqueKeys = Aggregation.group().addToSet("keys.k").as("uniqueKes");
		ProjectionOperation projectFinal = Aggregation.project().andExclude("_id").andInclude("uniqueKes");

		Aggregation aggregation = Aggregation.newAggregation(
			List.of(
				match,
				projectToArray,
				unwind,
				groupUniqueKeys,
				projectFinal)
		);

		AggregationResults<UniqueKeysResult> logs = mongoTemplate.aggregate(aggregation, "logs",
			UniqueKeysResult.class);

		return Objects.requireNonNull(logs.getUniqueMappedResult()).uniqueKes().stream()
			.distinct()
			.toList();
	}

	public List<TrainDataSearchDTO> getSearchTrainData(List<String> experimentIds, List<String> metrics) {
		Aggregation aggregation = Aggregation.newAggregation(
			eqExperimentIds(experimentIds),
			filterMetric(metrics),
			arrayToObject(),
			groupByWorkloadId(),
			projectFields()
		);

		AggregationResults<TrainDataSearchDTO> logs = mongoTemplate.aggregate(aggregation, "logs",
			TrainDataSearchDTO.class);
		return logs.getMappedResults();
	}

	private MatchOperation eqExperimentIds(List<String> experimentIds) {
		Criteria criteria = Criteria.where("workload_id").in(experimentIds);
		return Aggregation.match(criteria);
	}

	private AddFieldsOperation filterMetric(List<String> metrics) {
		return Aggregation.addFields()
			.addFieldWithValue("metrics",
				ArrayOperators.Filter.filter(ObjectToArray.valueOfToArray("metrics"))
					.as("metric")
					.by(ArrayOperators.In.arrayOf(metrics).containsValue("$$metric.k")))
			.build();
	}

	private AddFieldsOperation arrayToObject() {
		return Aggregation.addFields()
			.addFieldWithValue("metrics",
				ArrayOperators.ArrayToObject.arrayToObject("$metrics"))
			.build();
	}

	private GroupOperation groupByWorkloadId() {
		return Aggregation.group("workload_id")
			.first("workload_id").as("workloadName")
			.push(new BasicDBObject("step", "$step")
				.append("epoch", "$epoch")
				.append("metrics", "$metrics")
				.append("relativeTime", "$relativeTime")
				.append("wallTime", "$wallTime"))
			.as("value");
	}

	private ProjectionOperation projectFields() {
		return Aggregation.project()
			.andExclude("_id")
			.andInclude("workloadName", "value");
	}
}
