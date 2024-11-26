package com.xiilab.servercore.registry.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.registry.dto.CommitImageReqDTO;
import com.xiilab.servercore.registry.dto.RegistryImageDTO;
import com.xiilab.servercore.registry.dto.RegistryTagDTO;

@Service
public interface RegistryService {
	//image commit
	void commitImage(CommitImageReqDTO commitImageReqDTO, UserDTO.UserInfo userDTO);

	//image list 조회
	List<RegistryImageDTO> getImageList(String searchCondition, int page, int pageSize, UserDTO.UserInfo userDTO);

	//image 상세 조회
	RegistryImageDTO getImageInfo(String imageId, UserDTO.UserInfo userDTO);

	List<RegistryTagDTO> getImageTagList(String imageName, UserDTO.UserInfo userDTO);

	//image 삭제
	void deleteImage(String imageId, UserDTO.UserInfo userDTO);

	//image version 삭제
	void deleteImageTag(String imageName, String imageTag, UserDTO.UserInfo userDTO);
}
