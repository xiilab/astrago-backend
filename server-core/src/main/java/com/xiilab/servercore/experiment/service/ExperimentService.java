package com.xiilab.servercore.experiment.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.exception.CommonException;
import com.xiilab.modulecommon.exception.errorcode.ChartErrorCode;
import com.xiilab.modulek8sdb.experiment.dto.ChartDTO;
import com.xiilab.modulek8sdb.experiment.dto.ExperimentColumnDTO;
import com.xiilab.modulek8sdb.experiment.entity.ChartEntity;
import com.xiilab.modulek8sdb.experiment.entity.ExperimentColumnEntity;
import com.xiilab.modulek8sdb.experiment.entity.PanelEntity;
import com.xiilab.modulek8sdb.experiment.repository.ChartRepository;
import com.xiilab.modulek8sdb.experiment.repository.ExperimentColumnRepository;
import com.xiilab.modulek8sdb.experiment.repository.LabelExperimentMappingRepository;
import com.xiilab.modulek8sdb.experiment.repository.PanelRepository;
import com.xiilab.modulek8sdb.label.entity.LabelEntity;
import com.xiilab.modulek8sdb.label.repository.LabelRepository;
import com.xiilab.modulek8sdb.workload.history.entity.ExperimentEntity;
import com.xiilab.modulek8sdb.workload.history.repository.ExperimentRepo;
import com.xiilab.moduleuser.dto.UserDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExperimentService {
	private final ChartRepository chartRepository;
	private final PanelRepository panelRepository;
	private final ExperimentColumnRepository experimentColumnRepository;
	private final LabelExperimentMappingRepository labelExperimentMappingRepository;
	private final ExperimentRepo experimentRepo;
	private final LabelRepository labelRepository;

	@Transactional
	public void saveChartPanel(String workspace, String title) {
		panelRepository.save(PanelEntity.builder()
			.workspace(workspace)
			.title(title)
			.build());
	}

	@Transactional(readOnly = true)
	public Page<ChartDTO.Panel> getChartPartByUserId(String workspace, Pageable pageable, UserDTO.UserInfo userDTO) {
		Page<PanelEntity> chartPanelList = panelRepository.findByRegUser_RegUserId(workspace, userDTO.getId(),
			pageable);
		return chartPanelList.map(ChartDTO.Panel::new);
	}

	@Transactional(readOnly = true)
	public Page<ChartDTO.Res> getChartsByPanelId(Long panelId, Pageable pageable) {
		Page<ChartEntity> chartList = chartRepository.findByChartPart_Id(panelId, pageable);
		return chartList.map(ChartDTO.Res::new);
	}

	@Transactional
	public void deletePanelById(Long panelId, UserDTO.UserInfo userInfo) {
		PanelEntity panelEntity = getChartPanelEntity(panelId);
		if (!panelEntity.getRegUser().getRegUserId().equals(userInfo.getId())) {
			throw new CommonException(ChartErrorCode.UNAUTHORIZED_ERROR);
		}
		panelRepository.deleteById(panelId);
	}

	@Transactional
	public void updateChatPartInfo(Long id, String title, UserDTO.UserInfo userInfo) {
		PanelEntity panelEntity = getChartPanelEntity(id);
		if (!panelEntity.getRegUser().getRegUserId().equals(userInfo.getId())) {
			throw new CommonException(ChartErrorCode.UNAUTHORIZED_ERROR);
		}
		panelEntity.updateTitle(title);
	}

	@Transactional
	public void addChart(Long chartPartId, ChartDTO.Req chatReq, UserDTO.UserInfo userInfo) {
		PanelEntity panelEntity = getChartPanelEntity(chartPartId);
		if (!panelEntity.getRegUser().getRegUserId().equals(userInfo.getId())) {
			throw new CommonException(ChartErrorCode.UNAUTHORIZED_ERROR);
		}
		panelEntity.getChartList().add(new ChartEntity(chatReq, panelEntity));
	}

	@Transactional
	public void updateChartInfo(Long chartId, ChartDTO.Req chatReq, UserDTO.UserInfo userInfo) {
		ChartEntity chartEntity = getChartEntity(chartId);
		if (!chartEntity.getRegUser().getRegUserId().equals(userInfo.getId())) {
			throw new CommonException(ChartErrorCode.UNAUTHORIZED_ERROR);
		}
		chartEntity.updateChart(chatReq);
	}

	@Transactional
	public void deleteChart(Long chartId, UserDTO.UserInfo userInfo) {
		ChartEntity chartEntity = getChartEntity(chartId);
		if (!chartEntity.getRegUser().getRegUserId().equals(userInfo.getId())) {
			throw new CommonException(ChartErrorCode.UNAUTHORIZED_ERROR);
		}
		chartRepository.deleteById(chartId);
	}

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

	@Transactional
	public void updateExperimentLabel(String experimentUUID, List<Long> labels) {
		Optional<ExperimentEntity> expOpt = experimentRepo.findByUuid(experimentUUID);
		expOpt.ifPresent(exp -> {
			List<LabelEntity> labelEntityList = labelRepository.findAllById(labels);
			exp.addLabels(labelEntityList);
		});
	}

	private PanelEntity getChartPanelEntity(Long chartPartId) {
		return panelRepository.findById(chartPartId)
			.orElseThrow(() -> new CommonException(ChartErrorCode.CHART_NOT_FOUND));
	}

	private ChartEntity getChartEntity(Long chartId) {
		return chartRepository.findById(chartId)
			.orElseThrow(() -> new CommonException(ChartErrorCode.CHART_PANEL_NOT_FOUND));
	}
}
