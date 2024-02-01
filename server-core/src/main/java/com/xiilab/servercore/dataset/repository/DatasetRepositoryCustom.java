package com.xiilab.servercore.dataset.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.dataset.entity.Dataset;

public interface DatasetRepositoryCustom {
	Page<Dataset> findByAuthority(PageRequest pageRequest, UserInfoDTO userInfoDTO);

	Dataset getDatasetWithStorage(Long datasetId);
}
