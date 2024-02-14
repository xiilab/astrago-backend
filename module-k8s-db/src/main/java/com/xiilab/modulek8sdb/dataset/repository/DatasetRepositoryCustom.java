package com.xiilab.modulek8sdb.dataset.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.xiilab.modulek8sdb.dataset.entity.Dataset;
import com.xiilab.moduleuser.dto.UserInfoDTO;

public interface DatasetRepositoryCustom {
	Page<Dataset> findByAuthorityWithPaging(PageRequest pageRequest, UserInfoDTO userInfoDTO);

	Dataset getDatasetWithStorage(Long datasetId);
	List<Dataset> findByAuthority(UserInfoDTO userInfoDTO);
}
