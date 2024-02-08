package com.xiilab.servercore.model.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.model.entity.Model;

public interface ModelRepositoryCustom {
	Page<Model> findByAuthorityWithPaging(PageRequest pageRequest, UserInfoDTO userInfoDTO);

	Model getModelWithStorage(Long modelId);
	List<Model> findByAuthority(UserInfoDTO userInfoDTO);
}
