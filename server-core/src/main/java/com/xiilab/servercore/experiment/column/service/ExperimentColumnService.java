package com.xiilab.servercore.experiment.column.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.servercore.experiment.column.dto.ExperimentColumnDTO;
import com.xiilab.servercore.experiment.column.entity.ExperimentColumnEntity;
import com.xiilab.servercore.experiment.column.repository.ExperimentColumnRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExperimentColumnService {
	private final ExperimentColumnRepository experimentColumnRepository;

	@Transactional
	public void updateColumn(List<ExperimentColumnDTO.Req> reqList, String workspace, String userId) {
		//기존 칼럼을 workspace와 userId로 가져옴
		List<ExperimentColumnEntity> existingColumns = experimentColumnRepository.findByWorkspaceAndRegUser_RegUserId(
			workspace, userId);
		Map<String, ExperimentColumnDTO.Req> reqMap = reqList.stream()
			.collect(Collectors.toMap(ExperimentColumnDTO.Req::getName, req -> req));

		for (ExperimentColumnEntity existingColumn : existingColumns) {
			ExperimentColumnDTO.Req req = reqMap.get(existingColumn.getName());
			if (req != null) {
				existingColumn.updateOrder(req.getOrder());
				reqMap.remove(existingColumn.getName());
			} else {
				experimentColumnRepository.delete(existingColumn);
			}
		}

		for (ExperimentColumnDTO.Req req : reqMap.values()) {
			ExperimentColumnEntity experimentColumnEntity = new ExperimentColumnEntity(req);
			experimentColumnRepository.save(experimentColumnEntity);
		}
	}

	@Transactional(readOnly = true)
	public List<ExperimentColumnDTO.Res> getColumns(String workspace, String userId) {
		List<ExperimentColumnEntity> existingColumns = experimentColumnRepository.findByWorkspaceAndRegUser_RegUserId(
			workspace, userId);
		return existingColumns.stream().map(column -> ExperimentColumnDTO.Res.builder()
				.id(column.getId())
				.name(column.getName())
				.order(column.getOrder())
				.build())
			.toList();
	}
}
