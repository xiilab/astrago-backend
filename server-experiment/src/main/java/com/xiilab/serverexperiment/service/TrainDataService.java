package com.xiilab.serverexperiment.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;
import com.xiilab.modulek8sdb.workload.history.repository.WorkloadHistoryRepo;
import com.xiilab.serverexperiment.domain.mongo.Log;
import com.xiilab.serverexperiment.domain.mongo.Workload;
import com.xiilab.serverexperiment.dto.TrainDataDTO;
import com.xiilab.serverexperiment.dto.TrainDataSearchDTO;
import com.xiilab.serverexperiment.repository.LogCustomRepository;
import com.xiilab.serverexperiment.repository.LogRepository;
import com.xiilab.serverexperiment.repository.WorkloadLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainDataService {
	private final WorkloadLogRepository workloadLogRepository;
	private final WorkloadHistoryRepo workloadHistoryRepo;
	private final LogRepository logRepository;
	private final LogCustomRepository logCustomRepository;

	@Transactional
	public void saveTrainData(TrainDataDTO.Req trainDataReq) {
		Optional<WorkloadEntity> workloadOpt = workloadHistoryRepo.findByResourceName(trainDataReq.getWorkloadName());

		workloadOpt.ifPresent(workload -> {
			workload.addExperiment(trainDataReq.getUuid());
			saveExperiment(trainDataReq);
		});
	}

	public List<String> getTrainDataKeyByIds(List<String> ids) {
		return logCustomRepository.getExperimentKeysByIds(ids);
	}

	public List<TrainDataSearchDTO> searchTrainData(List<String> experiments, List<String> metrics) {
		return logCustomRepository.getSearchTrainData(experiments, metrics);
	}

	private void saveExperiment(TrainDataDTO.Req trainDataReq) {
		Workload workload = workloadLogRepository.findByNameAndId(trainDataReq.getWorkloadName(),
				trainDataReq.getUuid())
			.orElseGet(() -> workloadLogRepository.save(Workload.builder()
				.id(trainDataReq.getUuid())
				.name(trainDataReq.getWorkloadName())
				.build()));

		List<Log> logList = trainDataReq.getMetrics().stream().map(req -> Log.builder()
				.workloadId(workload.getId())
				.step(req.getStep())
				.epoch(req.getEpochs())
				.relativeTime(req.getRelativeTime())
				.wallTime(req.getWallTime())
				.metrics(req.getLog())
				.build())
			.toList();

		logRepository.saveAll(logList);
	}
}
