package com.xiilab.serverexperiment.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ObjectOperators.ObjectToArray;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.mongodb.BasicDBObject;
import com.xiilab.modulek8sdb.experiment.entity.ChartEntity;
import com.xiilab.serverexperiment.domain.mongo.ExperimentTrainMetric;
import com.xiilab.serverexperiment.dto.ExperimentDataDTO;
import com.xiilab.serverexperiment.repository.dto.UniqueKeysResult;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ExperimentMongoCustomRepository {
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

		AggregationResults<UniqueKeysResult> logs = mongoTemplate.aggregate(aggregation, "ExperimentTrainMetrics",
			UniqueKeysResult.class);

		UniqueKeysResult uniqueMappedResult = logs.getUniqueMappedResult();

		if (uniqueMappedResult == null) {
			return new ArrayList<>();
		}

		return uniqueMappedResult.uniqueKes().stream()
			.distinct()
			.toList();
	}

	public List<ExperimentDataDTO.SearchRes> searchExperimentsGraphData(List<String> experimentIds,
		ChartEntity chartEntity) {
		List<AggregationOperation> operations = new ArrayList<>();
		operations.add(eqExperimentIds(experimentIds));
		operations.add(filterMetric(chartEntity.getAllAxis()));
		operations.add(arrayToObject()); // 필터링 후 arrayToObject 호출

		// X축 필터 적용
		AggregationOperation xAxisFilter = applyXAxisRangeFilter(chartEntity.getXAxis(), chartEntity.getXAxisMin(),
			chartEntity.getXAxisMax());
		if (xAxisFilter != null) {
			operations.add(xAxisFilter);
		}

		// Y축 필터 적용
		// List<AggregationOperation> yAxisFilter = applyYAxisRangeFilter(chartEntity.getYAxis(),
		// 	chartEntity.getYAxisMin(), chartEntity.getYAxisMax());
		// if (yAxisFilter != null) {
		// 	operations.addAll(yAxisFilter);
		// }

		operations.add(groupByWorkloadId());
		operations.add(projectFields());

		Aggregation aggregation = Aggregation.newAggregation(operations);

		AggregationResults<ExperimentDataDTO.SearchRes> logs = mongoTemplate.aggregate(aggregation,
			"ExperimentTrainMetrics",
			ExperimentDataDTO.SearchRes.class);
		return logs.getMappedResults();
	}

	public List<ExperimentDataDTO.SearchRes> getGraphMetrics(List<String> experimentIds,
		List<String> metrics) {
		List<AggregationOperation> operations = new ArrayList<>();
		operations.add(eqExperimentIds(experimentIds));
		operations.add(filterMetric(metrics));
		operations.add(arrayToObject()); // 필터링 후 arrayToObject 호출
		operations.add(groupByWorkloadId());
		operations.add(projectFields());

		Aggregation aggregation = Aggregation.newAggregation(operations);

		AggregationResults<ExperimentDataDTO.SearchRes> logs = mongoTemplate.aggregate(aggregation,
			"ExperimentTrainMetrics",
			ExperimentDataDTO.SearchRes.class);
		return logs.getMappedResults();
	}

	public List<ExperimentDataDTO.SystemSearchRes> getSystemMetrics(List<String> experimentIds, List<String> metrics) {
		// 1. AggregationOperation 리스트 초기화
		List<AggregationOperation> operations = new ArrayList<>();

		// 2. Experiment ID 필터 추가
		operations.add(eqExperimentIds(experimentIds));

		// 3. 필드 프로젝션 생성
		operations.add(createProjectOperationWithWorkloadId(metrics));

		// 4. workload_id로 그룹화
		operations.add(createGroupOperation());

		// 5. Aggregation 파이프라인 생성 및 실행
		Aggregation aggregation = Aggregation.newAggregation(operations);
		AggregationResults<ExperimentDataDTO.SystemSearchRes> results =
			mongoTemplate.aggregate(aggregation, "ExperimentSystemMetrics", ExperimentDataDTO.SystemSearchRes.class);

		// 6. 결과 반환 (실제 변환 로직 추가 필요)
		return results.getMappedResults();
	}

	private ProjectionOperation createProjectOperationWithWorkloadId(List<String> metrics) {
		// 기본적으로 workload_id를 포함한 프로젝션
		ProjectionOperation project = Aggregation.project("workload_id");

		if (metrics != null) {
			//wallTime 기본으로 추가
			project = project.andInclude("wallTime");
			for (String metric : metrics) {
				if (metric != null && !metric.trim().isEmpty()) {
					project = project.andInclude(metric);
				}
			}
		}

		return project;
	}

	private GroupOperation createGroupOperation() {
		// workload_id로 그룹화하고 나머지 필드를 배열로 수집
		return Aggregation.group("workload_id")
			.first("workload_id").as("uuid")
			.push(new BasicDBObject("cpuUsage", "$cpuUsage")
				.append("memUsage", "$memUsage")
				.append("gpuInfos", "$gpuInfos")
				.append("wallTime", "$wallTime"))
			.as("value");
	}

	public void deleteExperimentsLogsByUUIDs(List<String> uuids) {
		Query query = new Query();
		query.addCriteria(Criteria.where("workload_id").in(uuids));

		mongoTemplate.remove(query, ExperimentTrainMetric.class);
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

	private AggregationOperation applyXAxisRangeFilter(String xAxis, Double xAxisMin, Double xAxisMax) {
		Criteria criteria = new Criteria();
		List<Criteria> criteriaList = new ArrayList<>();
		if (xAxisMin != null) {
			criteriaList.add(Criteria.where(xAxis).gte(xAxisMin));
		}
		if (xAxisMax != null) {
			criteriaList.add(Criteria.where(xAxis).lte(xAxisMax));
		}
		if (!criteriaList.isEmpty()) {
			criteria = criteria.andOperator(criteriaList.toArray(new Criteria[0]));
			return Aggregation.match(criteria);
		}
		return null;
	}

	// public AggregationOperation applyYAxisRangeFilter(List<String> yAxis, Map<String, Double> yAxisMin,
	// 	Map<String, Double> yAxisMax) {
	// 	List<AggregationOperation> operations = new ArrayList<>();
	//
	// 	for (String y : yAxis) {
	// 		Double min = yAxisMin.get(y);
	// 		Double max = yAxisMax.get(y);
	//
	// 		List<Criteria> criteriaList = new ArrayList<>();
	// 		if (min != null) {
	// 			criteriaList.add(Criteria.where("metrics." + y).gte(min));
	// 		}
	// 		if (max != null) {
	// 			criteriaList.add(Criteria.where("metrics." + y).lte(max));
	// 		}
	//
	// 		if (!criteriaList.isEmpty()) {
	// 			Criteria yAxisCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
	// 			operations.add(Aggregation.addFields()
	// 				.addFieldWithValue("metrics." + y,
	// 					ConditionalOperators.when(yAxisCriteria)
	// 						.thenValueOf("metrics." + y)
	// 						.otherwise(null))
	// 				.build());
	// 		}
	// 	}
	// 	if (!operations.isEmpty()) {
	// 		return Aggregation.newAggregation(operations.toArray(new AggregationOperation[0]));
	// 	}
	// 	return null;
	// }

	private AddFieldsOperation arrayToObject() {
		return Aggregation.addFields()
			.addFieldWithValue("metrics",
				ArrayOperators.ArrayToObject.arrayToObject("$metrics"))
			.build();
	}

	private GroupOperation groupByWorkloadId() {
		return Aggregation.group("workload_id")
			.first("workload_id").as("uuid")
			.push(new BasicDBObject("step", "$step")
				.append("epochs", "$epochs")
				.append("metrics", "$metrics")
				.append("relativeTime", "$relativeTime")
				.append("wallTime", "$wallTime"))
			.as("value");
	}

	private ProjectionOperation projectFields() {
		return Aggregation.project()
			.andExclude("_id")
			.andInclude("uuid", "value");
	}

	public Map<String, ExperimentDataDTO.MetricEntry> searchExperimentsTableData(List<String> experiments,
		List<String> metrics) {
		Aggregation aggregation = Aggregation.newAggregation(
			eqExperimentIds(experiments),
			sortByStep(),
			groupByWorkloadIdAndFirstRow(),
			projectTableFields()
		);
		AggregationResults<ExperimentDataDTO.Res> results = mongoTemplate.aggregate(aggregation,
			"ExperimentTrainMetrics",
			ExperimentDataDTO.Res.class);
		List<ExperimentDataDTO.Res> mappedResults = results.getMappedResults();
		if (!CollectionUtils.isEmpty(mappedResults)) {
			return mappedResults.stream().collect(Collectors.toMap(
				ExperimentDataDTO.Res::getWorkloadName,
				entry -> new ExperimentDataDTO.MetricEntry(
					entry.getStep(),
					entry.getEpochs(),
					entry.getWallTime(),
					entry.getRelativeTime(),
					entry.getLog())));
		}
		return Map.of();
	}

	private GroupOperation groupByWorkloadIdAndFirstRow() {
		return Aggregation.group("workload_id")
			.first("workload_id").as("workloadName")
			.first("step").as("step")
			.first("epoch").as("epochs")
			.first("metrics").as("log")
			.first("relativeTime").as("relativeTime")
			.first("wallTime").as("wallTime");
	}

	private ProjectionOperation projectTableFields() {
		return Aggregation.project()
			.andExclude("_id")
			.andInclude("workloadName", "step", "epochs", "log", "relativeTime", "wallTime");
	}

	private SortOperation sortByStep() {
		return Aggregation.sort(Sort.by(Sort.Direction.DESC, "step"));
	}
}
