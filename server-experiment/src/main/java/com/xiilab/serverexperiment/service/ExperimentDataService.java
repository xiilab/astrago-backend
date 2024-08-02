package com.xiilab.serverexperiment.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulek8sdb.experiment.entity.ChartEntity;
import com.xiilab.modulek8sdb.experiment.repository.ChartRepository;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;
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
	private final ChartRepository chartRepository;

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

	public List<ExperimentDataDTO.SearchRes> searchExperimentsGraphData(
		Long id,
		List<String> experiments) {
		Optional<ChartEntity> chartOpt = chartRepository.findById(id);
		if (chartOpt.isPresent()) {
			ChartEntity chartEntity = chartOpt.get();
			return experimentCustomRepository.searchExperimentsGraphData(experiments, chartEntity.getAllAxis());
		} else {
			return List.of();
		}
	}

	public List<ExperimentDataDTO.Res> searchExperimentTableData(List<String> experiments, List<String> metrics) {

		return experimentCustomRepository.searchExperimentsTableData(experiments, metrics);
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
