package com.xiilab.servercore.chart.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.exception.CommonException;
import com.xiilab.modulecommon.exception.errorcode.ChartErrorCode;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.chart.dto.ChartDTO;
import com.xiilab.servercore.chart.entity.ChartEntity;
import com.xiilab.servercore.chart.entity.PanelEntity;
import com.xiilab.servercore.chart.repository.ChartRepository;
import com.xiilab.servercore.chart.repository.PanelRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChartService {
	private final ChartRepository chartRepository;
	private final PanelRepository panelRepository;

	@Transactional
	public void saveChartPanel(String title) {
		panelRepository.save(PanelEntity.builder()
			.title(title)
			.build());
	}

	@Transactional(readOnly = true)
	public Page<ChartDTO.Panel> getChartPartByUserId(Pageable pageable, UserDTO.UserInfo userDTO) {
		Page<PanelEntity> chartPanelList = panelRepository.findByRegUser_RegUserId(userDTO.getId(),
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

	private PanelEntity getChartPanelEntity(Long chartPartId) {
		return panelRepository.findById(chartPartId)
			.orElseThrow(() -> new CommonException(ChartErrorCode.CHART_NOT_FOUND));
	}

	private ChartEntity getChartEntity(Long chartId) {
		return chartRepository.findById(chartId)
			.orElseThrow(() -> new CommonException(ChartErrorCode.CHART_PANEL_NOT_FOUND));
	}
}
