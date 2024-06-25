package com.xiilab.modulek8sdb.statistics.repository;

import java.util.List;

import com.xiilab.modulek8sdb.statistics.dto.StatisticsDTO;

public interface StatisticsRepository {

	List<StatisticsDTO.UsageDTO> resourceUsageDTOList();

	List<StatisticsDTO.ResourceRequestDTO> resourceRequestDTOList();

	List<StatisticsDTO.CountDTO> getUserResourceRequestCount();

	List<StatisticsDTO.CountDTO> resourceQuotaApproveCount();

	List<StatisticsDTO.CountDTO> resourceQuotaRejectCount();

	List<StatisticsDTO.CountDTO> getCreateWorkloadCount();

	List<StatisticsDTO.CountDTO> getCreateCodeCount();

	List<StatisticsDTO.CountDTO> getCreateDatasetCount();

	List<StatisticsDTO.CountDTO> getCreateCredentialCount();
}
