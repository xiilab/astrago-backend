package com.xiilab.serverexperiment.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulek8sdb.experiment.entity.ChartEntity;
import com.xiilab.modulek8sdb.experiment.entity.ExperimentColumnEntity;
import com.xiilab.modulek8sdb.experiment.repository.ChartRepository;
import com.xiilab.modulek8sdb.experiment.repository.ExperimentColumnRepository;
import com.xiilab.modulek8sdb.experiment.repository.LabelExperimentMappingRepository;
import com.xiilab.modulek8sdb.label.repository.LabelRepository;
import com.xiilab.modulek8sdb.workload.history.dto.ExperimentQueryResult;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;
import com.xiilab.modulek8sdb.workload.history.repository.ExperimentCustomRepo;
import com.xiilab.modulek8sdb.workload.history.repository.WorkloadHistoryRepo;
import com.xiilab.serverexperiment.domain.mongo.Experiment;
import com.xiilab.serverexperiment.domain.mongo.Workload;
import com.xiilab.serverexperiment.dto.ExperimentDataDTO;
import com.xiilab.serverexperiment.repository.ExperimentCustomRepository;
import com.xiilab.serverexperiment.repository.ExperimentRepository;
import com.xiilab.serverexperiment.repository.WorkloadLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExperimentDataService {
	private final WorkloadLogRepository workloadLogRepository;
	private final WorkloadHistoryRepo workloadHistoryRepo;
	private final ExperimentRepository experimentRepository;
	private final ExperimentCustomRepository experimentCustomRepository;
	private final ExperimentCustomRepo experimentCustomRepo;
	private final ChartRepository chartRepository;
	private final ExperimentColumnRepository experimentColumnRepository;
	private final LabelExperimentMappingRepository labelExperimentMappingRepository;
	private final LabelRepository labelRepository;

	@Transactional
	public void saveExperimentData(ExperimentDataDTO.Req trainDataReq) {
		Optional<WorkloadEntity> workloadOpt = workloadHistoryRepo.findByResourceName(trainDataReq.getWorkloadName());
		workloadOpt.ifPresent(workload -> {
			workload.addExperiment(trainDataReq.getUuid());
			saveExperiment(trainDataReq);
		});
	}

	public List<String> getExperimentDataKeyByIds(List<String> ids) {
		return experimentCustomRepository.getExperimentKeysByIds(ids);
	}

	public ExperimentDataDTO.ChartRes searchExperimentsGraphData(
		Long id,
		List<String> experiments) {
		Optional<ChartEntity> chartOpt = chartRepository.findById(id);
		if (chartOpt.isPresent()) {
			ChartEntity chartEntity = chartOpt.get();
			List<ExperimentDataDTO.SearchRes> searchRes = experimentCustomRepository.searchExperimentsGraphData(
				experiments, chartEntity);
			return new ExperimentDataDTO.ChartRes(chartEntity, searchRes);
		} else {
			return null;
		}
	}

	public Page<ExperimentDataDTO.TableDTO> searchExperimentTableData(String userId, String workspace,
		String searchCondition, WorkloadStatus status, Pageable pageable) {
		Page<ExperimentQueryResult> experiments = experimentCustomRepo.getExperiments(searchCondition, workspace,
			userId, status, pageable);
		List<ExperimentColumnEntity> userColumnData = experimentColumnRepository.findByWorkspaceAndRegUser_RegUserId(
			workspace, userId);
		List<String> metrics = userColumnData.stream().map(ExperimentColumnEntity::getName).toList();
		List<String> exps = experiments.map(ExperimentQueryResult::getId).toList();
		Map<String, ExperimentDataDTO.MetricEntry> stringMetricEntryMap = experimentCustomRepository.searchExperimentsTableData(
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

		experimentRepository.saveAll(experimentList);
	}
}
