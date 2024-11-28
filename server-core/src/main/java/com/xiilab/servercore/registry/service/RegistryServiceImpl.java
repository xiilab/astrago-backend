package com.xiilab.servercore.registry.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.workload.repository.WorkloadRepository;
import com.xiilab.modulek8s.workload.service.WorkloadModuleService;
import com.xiilab.modulek8s.workload.vo.CommitImageJobVO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.registry.dto.CommitImageReqDTO;
import com.xiilab.servercore.registry.dto.RegistryImageDTO;
import com.xiilab.servercore.registry.dto.RegistryTagDTO;
import com.xiilab.servercore.registry.repository.RegistryRepository;

import io.fabric8.kubernetes.api.model.Pod;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistryServiceImpl implements RegistryService {
	private final RegistryRepository registryRepository;
	private final WorkloadRepository workloadRepository;
	private final WorkloadModuleService workloadModuleService;

	@Override
	public void commitImage(CommitImageReqDTO commitImageReqDTO, UserDTO.UserInfo userDTO) {
		//워크로드 정보를 조회
		Pod jobPod = workloadModuleService.getJobPod(commitImageReqDTO.getWorkspace(), commitImageReqDTO.getWorkload(),
			commitImageReqDTO.getWorkloadType());
		String nodeName = jobPod.getSpec().getNodeName();
		//user 고유의 repo가 존재하지 않을 경우 생성한다.
		validateProjectYN(userDTO.getUserId());
		//batchJob 생성
		workloadRepository.commitImage(new CommitImageJobVO(
			commitImageReqDTO.getWorkspace(),
			jobPod.getMetadata().getName(),
			nodeName,
			userDTO.getUserId(),
			commitImageReqDTO.getImageName(),
			commitImageReqDTO.getImageTag()
		));
	}

	@Override
	public List<RegistryImageDTO> getImageList(String searchCondition, int page, int pageSize,
		UserDTO.UserInfo userDTO) {
		//user 고유의 repo가 존재하지 않을 경우 생성한다.
		validateProjectYN(userDTO.getUserId());
		//해당 user의 image list를 조회한다.
		return registryRepository.getImageList(userDTO.getUserId(), searchCondition, page, pageSize);
	}

	@Override
	public RegistryImageDTO getImageInfo(String imageId, UserDTO.UserInfo userDTO) {
		List<RegistryTagDTO> imageTags = registryRepository.getImageTags(userDTO.getUserId(), imageId);
		//이미지 정보를 조회한다.
		return registryRepository.getImageInfo(userDTO.getUserId(), imageId);
	}

	@Override
	public List<RegistryTagDTO> getImageTagList(String imageName, UserDTO.UserInfo userDTO) {
		return registryRepository.getImageTags(userDTO.getUserId(), imageName);
	}

	@Override
	public void deleteImage(String imageId, UserDTO.UserInfo userDTO) {
		registryRepository.deleteImage(userDTO.getUserId(), imageId);
	}

	@Override
	public void deleteImageTag(String imageName, String imageTag, UserDTO.UserInfo userDTO) {
		registryRepository.deleteImageTag(userDTO.getUserId(), imageName, imageTag);
	}

	private void validateProjectYN(String projectName) {
		//프로젝트 존재하는지 여부 조회
		boolean projectYN = registryRepository.validateByProjectName(projectName);
		//존재하지 않는다면 프로젝트 생성
		if (!projectYN) {
			registryRepository.createProject(projectName, true);
		}
	}
}
