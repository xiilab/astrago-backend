package com.xiilab.serverexperiment.service;

import static com.xiilab.modulecommon.exception.errorcode.ChartErrorCode.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.exception.CommonException;
import com.xiilab.modulek8sdb.experiment.entity.ChartEntity;
import com.xiilab.modulek8sdb.experiment.entity.ExperimentColumnEntity;
import com.xiilab.modulek8sdb.experiment.repository.ChartRepository;
import com.xiilab.modulek8sdb.experiment.repository.ExperimentColumnRepository;
import com.xiilab.modulek8sdb.workload.history.dto.ExperimentQueryResult;
import com.xiilab.modulek8sdb.workload.history.entity.ExperimentEntity;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;
import com.xiilab.modulek8sdb.workload.history.repository.ExperimentMariaCustomRepo;
import com.xiilab.modulek8sdb.workload.history.repository.ExperimentRepo;
import com.xiilab.modulek8sdb.workload.history.repository.WorkloadHistoryRepo;
import com.xiilab.serverexperiment.domain.mongo.Experiment;
import com.xiilab.serverexperiment.domain.mongo.Workload;
import com.xiilab.serverexperiment.dto.ExperimentDataDTO;
import com.xiilab.serverexperiment.repository.ExperimentMongoCustomRepository;
import com.xiilab.serverexperiment.repository.ExperimentMongoRepository;
import com.xiilab.serverexperiment.repository.WorkloadLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExperimentDataService {
	private final WorkloadLogRepository workloadLogRepository;
	private final WorkloadHistoryRepo workloadHistoryRepo;
	private final ExperimentRepo experimentRepo;
	private final ExperimentMongoRepository experimentMongoRepository;
	private final ExperimentMongoCustomRepository experimentMongoCustomRepository;
	private final ExperimentMariaCustomRepo experimentMariaCustomRepo;
	private final ChartRepository chartRepository;
	private final ExperimentColumnRepository experimentColumnRepository;

	@Transactional
	public void saveExperimentData(ExperimentDataDTO.Req trainDataReq) {
		Optional<WorkloadEntity> workloadOpt = workloadHistoryRepo.findByResourceName(trainDataReq.getWorkloadName());
		workloadOpt.ifPresent(workload -> {
			workload.addExperiment(trainDataReq.getUuid());
			saveExperiment(trainDataReq);
		});
	}

	public List<String> getExperimentDataKeyByIds(List<String> ids) {
		return experimentMongoCustomRepository.getExperimentKeysByIds(ids);
	}

	public ExperimentDataDTO.ChartRes searchExperimentsGraphData(
		Long id) {
		Optional<ChartEntity> chartOpt = chartRepository.findById(id);
		if (chartOpt.isPresent()) {
			ChartEntity chartEntity = chartOpt.get();
			return new ExperimentDataDTO.ChartRes(chartEntity);
		} else {
			return null;
		}
	}

	public List<ExperimentDataDTO.SearchRes> getGraphMetrics(ExperimentDataDTO.SearchReq searchReq) {
		if (CollectionUtils.isEmpty(searchReq.getExperiments()) || CollectionUtils.isEmpty(searchReq.getMetrics())) {
			throw new CommonException(CHART_ILLEGAL_ARGS);
		}
		return experimentMongoCustomRepository.getGraphMetrics(searchReq.getExperiments(), searchReq.getMetrics());
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
			experimentMongoCustomRepository.deleteExperimentsByUUIDs(ids);
		}
	}

	private void saveExperiment(ExperimentDataDTO.Req trainDataReq) {
		Workload workload = workloadLogRepository.findByNameAndId(trainDataReq.getWorkloadName(),
				trainDataReq.getUuid())
			.orElseGet(() -> workloadLogRepository.save(Workload.builder()
				.id(trainDataReq.getUuid())
				.name(trainDataReq.getWorkloadName())
				.build()));

		List<Experiment> experimentList = trainDataReq.getMetrics().stream().map(req -> Experiment.builder()
				.workloadId(workload.getId())
				.step(req.getStep())
				.epoch(req.getEpochs())
				.relativeTime(req.getRelativeTime())
				.wallTime(req.getWallTime())
				.metrics(req.getMetrics())
				.build())
			.toList();

		experimentMongoRepository.saveAll(experimentList);
	}
}
