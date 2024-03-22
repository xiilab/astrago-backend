package com.xiilab.servermonitor.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.util.DataConverterUtil;
import com.xiilab.modulek8sdb.workspace.entity.ResourceQuotaEntity;
import com.xiilab.modulek8sdb.workspace.enums.ResourceQuotaStatus;
import com.xiilab.modulek8sdb.workspace.repository.ResourceQuotaCustomRepository;
import com.xiilab.modulemonitor.dto.ReportDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DbMonitorServiceImpl implements DbMonitorService{
	private final ResourceQuotaCustomRepository resourceQuotaCustomRepository;

	@Override
	public ReportDTO.ResourceQuotaDTO getResourceQuota(String startDate, String endDate) {
		List<ResourceQuotaEntity> resourceQuotaByPeriod = getResourceQuotaList(startDate, endDate);

		long gpuRequest = 0;
		long gpuApproval = 0;
		long gpuRefuseCount = 0;
		long cpuRequest = 0;
		long cpuApproval = 0;
		long cpuRefuseCount = 0;
		long memRequest = 0;
		long memApproval = 0;
		long memRefuseCount = 0;
		
		for(ResourceQuotaEntity resourceQuota : resourceQuotaByPeriod){
			switch (resourceQuota.getStatus()){
				case REJECT ->{
					gpuRefuseCount = gpuRefuseCount + resourceQuota.getGpuReq();
					cpuRefuseCount = cpuRefuseCount + resourceQuota.getCpuReq();
					memRefuseCount = memRefuseCount + resourceQuota.getMemReq();
				}
				case APPROVE ->{
					gpuApproval = gpuApproval + resourceQuota.getGpuReq();
					cpuApproval = cpuApproval + resourceQuota.getCpuReq();
					memApproval = memApproval + resourceQuota.getMemReq();
				}
				case WAITING ->{
					gpuRequest = gpuRequest + resourceQuota.getGpuReq();
					cpuRequest = cpuRequest + resourceQuota.getCpuReq();
					memRequest = memRequest + resourceQuota.getMemReq();
				}
			}
		}
		
		return ReportDTO.ResourceQuotaDTO.builder()
			.gpuRequest(gpuRequest + gpuApproval + gpuRefuseCount)
			.gpuApproval(gpuApproval)
			.gpuRefuseCount(gpuRefuseCount)
			.cpuRequest(cpuRequest + cpuApproval + cpuRefuseCount)
			.cpuApproval(cpuApproval)
			.cpuRefuseCount(cpuRefuseCount)
			.memRequest(memRequest + memApproval + memRefuseCount)
			.memApproval(memApproval)
			.memRefuseCount(memRefuseCount)
			.build();
	}

	@Override
	public List<ReportDTO.WorkspaceResourceQuotaDTO> getResourceQuotaByWorkspace(String endDate, String startDate) {

		List<ReportDTO.WorkspaceResourceQuotaDTO> result = new ArrayList<>();

		List<ResourceQuotaEntity> resourceQuotaList = getResourceQuotaList(startDate, endDate);

		Map<String, Map<ResourceQuotaStatus, List<ResourceQuotaEntity>>> collect = resourceQuotaList.stream()
			.collect(Collectors.groupingBy(ResourceQuotaEntity::getWorkspaceResourceName,
				Collectors.groupingBy(ResourceQuotaEntity::getStatus)));

		for (Map.Entry<String, Map<ResourceQuotaStatus, List<ResourceQuotaEntity>>> entry : collect.entrySet()) {
			long gpuRequest = 0;
			long gpuApproval = 0;
			long gpuRefuseCount = 0;
			long cpuRequest = 0;
			long cpuApproval = 0;
			long cpuRefuseCount = 0;
			long memRequest = 0;
			long memApproval = 0;
			long memRefuseCount = 0;
			String workspaceName = "";
			String userName = "";

			for (Map.Entry<ResourceQuotaStatus, List<ResourceQuotaEntity>> entry2 : entry.getValue().entrySet()) {
				List<ResourceQuotaEntity> value = entry2.getValue();
				for (ResourceQuotaEntity resourceQuotaEntity : value) {
					switch (entry2.getKey()) {
						case REJECT -> {
							gpuRefuseCount = gpuRefuseCount + resourceQuotaEntity.getGpuReq();
							cpuRefuseCount = cpuRefuseCount + resourceQuotaEntity.getCpuReq();
							memRefuseCount = memRefuseCount + resourceQuotaEntity.getMemReq();
						}
						case APPROVE -> {
							gpuApproval = gpuApproval + resourceQuotaEntity.getGpuReq();
							cpuApproval = cpuApproval + resourceQuotaEntity.getCpuReq();
							memApproval = memApproval + resourceQuotaEntity.getMemReq();
						}
						case WAITING -> {
							gpuRequest = gpuRequest + resourceQuotaEntity.getGpuReq();
							cpuRequest = cpuRequest + resourceQuotaEntity.getCpuReq();
							memRequest = memRequest + resourceQuotaEntity.getMemReq();
						}
					}
					workspaceName = resourceQuotaEntity.getWorkspaceName();
					userName = resourceQuotaEntity.getRegUser().getRegUserName() + " (" + resourceQuotaEntity.getRegUser().getRegUserRealName() + ")";
				}
			}

			result.add(ReportDTO.WorkspaceResourceQuotaDTO.builder()
				.workspaceResourceName(entry.getKey())
				.workspaceName(workspaceName)
				.userName(userName)
				.gpuRequest(gpuRequest + gpuApproval + gpuRefuseCount)
				.gpuApproval(gpuApproval)
				.gpuRefuseCount(gpuRefuseCount)
				.cpuRequest(cpuRequest + cpuApproval + cpuRefuseCount)
				.cpuApproval(cpuApproval)
				.cpuRefuseCount(cpuRefuseCount)
				.memRequest(memRequest + memApproval + memRefuseCount)
				.memApproval(memApproval)
				.memRefuseCount(memRefuseCount)
				.build());
		}

		return result;
	}

	private List<ResourceQuotaEntity> getResourceQuotaList(String startDate, String endDate) {
		LocalDateTime start = DataConverterUtil.dataFormatterBy16Str(startDate);
		LocalDateTime end = DataConverterUtil.dataFormatterBy16Str(endDate);

		return resourceQuotaCustomRepository.findResourceQuotaByPeriod(start, end);
	}

}
