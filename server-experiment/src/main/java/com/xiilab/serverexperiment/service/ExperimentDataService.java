package com.xiilab.serverexperiment.service;

import static com.xiilab.modulecommon.exception.errorcode.ChartErrorCode.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.exception.CommonException;
import com.xiilab.modulek8sdb.experiment.entity.ExperimentColumnEntity;
import com.xiilab.modulek8sdb.experiment.repository.ExperimentColumnRepository;
import com.xiilab.modulek8sdb.workload.history.dto.ExperimentQueryResult;
import com.xiilab.modulek8sdb.workload.history.entity.ExperimentEntity;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;
import com.xiilab.modulek8sdb.workload.history.repository.ExperimentMariaCustomRepo;
import com.xiilab.modulek8sdb.workload.history.repository.ExperimentRepo;
import com.xiilab.modulek8sdb.workload.history.repository.WorkloadHistoryRepo;
import com.xiilab.serverexperiment.domain.mongo.ExperimentSystemMetric;
import com.xiilab.serverexperiment.domain.mongo.ExperimentTrainMetric;
import com.xiilab.serverexperiment.dto.ExperimentDataDTO;
import com.xiilab.serverexperiment.repository.ExperimentMongoCustomRepository;
import com.xiilab.serverexperiment.repository.ExperimentSystemMetricMongoRepository;
import com.xiilab.serverexperiment.repository.ExperimentTrainMetricMongoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExperimentDataService {
	private final WorkloadHistoryRepo workloadHistoryRepo;
	private final ExperimentRepo experimentRepo;
	private final ExperimentTrainMetricMongoRepository experimentTrainMetricMongoRepository;
	private final ExperimentSystemMetricMongoRepository experimentSystemMetricMongoRepository;
	private final ExperimentMongoCustomRepository experimentMongoCustomRepository;
	private final ExperimentMariaCustomRepo experimentMariaCustomRepo;
	private final ExperimentColumnRepository experimentColumnRepository;

	@Transactional
	public void saveExperimentTrainData(ExperimentDataDTO.TrainReq trainDataTrainReq) {
		Optional<WorkloadEntity> workloadOpt = workloadHistoryRepo.findByResourceName(
			trainDataTrainReq.getWorkloadName());
		workloadOpt.ifPresent(workload -> {
			workload.addExperiment(trainDataTrainReq.getUuid());
			saveExperimentTrainMetrics(trainDataTrainReq);
		});
	}

	@Transactional
	public void saveExperimentSystemData(ExperimentDataDTO.SystemReq systemReq) {
		Optional<WorkloadEntity> workloadOpt = workloadHistoryRepo.findByResourceName(systemReq.getWorkloadName());
		workloadOpt.ifPresent(workload -> {
			workload.addExperiment(systemReq.getUuid());
			saveExperimentSystemMetric(systemReq);
		});
	}

	public List<String> getExperimentDataKeyByIds(List<String> ids) {
		return experimentMongoCustomRepository.getExperimentKeysByIds(ids);
	}

	public List<ExperimentDataDTO.SearchRes> getGraphMetrics(ExperimentDataDTO.SearchReq searchReq) {
		if (CollectionUtils.isEmpty(searchReq.getExperiments()) || CollectionUtils.isEmpty(searchReq.getMetrics())) {
			throw new CommonException(CHART_ILLEGAL_ARGS);
		}
		return experimentMongoCustomRepository.getGraphMetrics(searchReq.getExperiments(), searchReq.getMetrics());
	}

	public List<ExperimentDataDTO.SystemSearchRes> getSystemMetrics(ExperimentDataDTO.SearchReq searchReq) {
		if (CollectionUtils.isEmpty(searchReq.getExperiments()) || CollectionUtils.isEmpty(searchReq.getMetrics())) {
			throw new CommonException(CHART_ILLEGAL_ARGS);
		}
		return experimentMongoCustomRepository.getSystemMetrics(searchReq.getExperiments(), searchReq.getMetrics());
	}

	public Page<ExperimentDataDTO.TableDTO> searchExperimentTableData(String userId, String workspace,
		String searchCondition, WorkloadStatus status, Pageable pageable) {
		Page<ExperimentQueryResult> experiments = experimentMariaCustomRepo.getExperiments(searchCondition, workspace,
			userId, status, pageable);
		List<ExperimentColumnEntity> userColumnData = experimentColumnRepository.findByWorkspaceAndRegUser_RegUserId(
			workspace, userId);
		List<String> metrics = userColumnData.stream().map(ExperimentColumnEntity::getName).toList();
		List<String> exps = experiments.map(ExperimentQueryResult::getId).toList();
		Map<String, ExperimentDataDTO.MetricEntry> stringMetricEntryMap = experimentMongoCustomRepository.searchExperimentsTableData(
			exps, metrics);
		return experiments.map(exp -> ExperimentDataDTO.TableDTO.builder()
			.id(exp.getId())
			.name(exp.getWorkloadName())
			.status(exp.getStatus())
			.resourceName(exp.getWorkloadResourceName())
			.userName(exp.getUsername())
			.labels(exp.getLabels())
			.metricEntry(stringMetricEntryMap.get(exp.getId()))
			.build());
	}

	@Transactional
	public void deleteExperimentByIds(List<String> ids) {
		List<ExperimentEntity> experiments = experimentRepo.findByUuidIn(ids);
		if (!CollectionUtils.isEmpty(experiments)) {
			//mariaDB에서 삭제
			experimentRepo.deleteAll(experiments);
			//mongoDB에서 삭제
			experimentMongoCustomRepository.deleteExperimentsLogsByUUIDs(ids);
		}
	}

	@Async
	protected void saveExperimentTrainMetrics(ExperimentDataDTO.TrainReq trainDataTrainReq) {
		List<ExperimentTrainMetric> experimentTrainMetricList = trainDataTrainReq.getMetrics()
			.stream()
			.map(req -> ExperimentTrainMetric.builder()
				.workloadId(trainDataTrainReq.getUuid())
				.step(req.getStep())
				.epoch(req.getEpochs())
				.relativeTime(req.getRelativeTime())
				.wallTime(req.getWallTime())
				.metrics(req.getMetrics())
				.build())
			.toList();

		experimentTrainMetricMongoRepository.saveAll(experimentTrainMetricList);
	}

	@Async
	protected void saveExperimentSystemMetric(ExperimentDataDTO.SystemReq systemReq) {
		ExperimentSystemMetric expSystemMetric = ExperimentSystemMetric.builder()
			.workloadId(systemReq.getUuid())
			.cpuUsage(systemReq.getCpuUsage())
			.memUsage(systemReq.getMemUsage())
			.gpuInfos(systemReq.getGpuInfos())
			.wallTime(systemReq.getWallTime())
			.build();

		experimentSystemMetricMongoRepository.save(expSystemMetric);
	}
}
