package com.xiilab.servercore.dataset.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.dataset.entity.Dataset;

public interface DatasetRepositoryCustom {
	Page<Dataset> findByAuthorityWithPaging(PageRequest pageRequest, UserInfoDTO userInfoDTO);

	Dataset getDatasetWithStorage(Long datasetId);
	List<Dataset> findByAuthority(UserInfoDTO userInfoDTO);
}
