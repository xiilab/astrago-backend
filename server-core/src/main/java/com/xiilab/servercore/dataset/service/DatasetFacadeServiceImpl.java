package com.xiilab.servercore.dataset.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.facade.dto.CreateLocalDatasetDTO;
import com.xiilab.modulek8s.facade.dto.CreateLocalDatasetResDTO;
import com.xiilab.modulek8s.facade.dto.ModifyLocalDatasetDeploymentDTO;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.moduleuser.dto.AuthType;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.dataset.dto.DatasetDTO;
import com.xiilab.servercore.dataset.entity.AstragoDatasetEntity;
import com.xiilab.servercore.dataset.entity.Dataset;
import com.xiilab.servercore.dataset.entity.LocalDatasetEntity;
import com.xiilab.servercore.storage.entity.StorageEntity;
import com.xiilab.servercore.storage.service.StorageService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DatasetFacadeServiceImpl implements DatasetFacadeService{
	@Value("${astrago.namespace}")
	private String namespace;
	@Value("${astrago.dataset.dockerImage.name}")
	private String dockerImage;
	@Value("${astrago.dataset.dockerImage.hostPath}")
	private String hostPath;
	private final DatasetService datasetService;
	private final StorageService storageService;
	private final WorkloadModuleFacadeService workloadModuleFacadeService;
	@Override
	@Transactional
	public void insertAstragoDataset(DatasetDTO.CreateAstragoDataset createDatasetDTO, List<MultipartFile> files) {
		//storage 조회
		StorageEntity storageEntity = storageService.findById(createDatasetDTO.getStorageId());

		//dataset 저장
		AstragoDatasetEntity astragoDataset = AstragoDatasetEntity.builder()
			.datasetName(createDatasetDTO.getDatasetName())
			.storageEntity(storageEntity)
			.build();

		datasetService.insertAstragoDataset(astragoDataset, files);
	}

	@Override
	public DatasetDTO.ResDatasetWithStorage getDataset(Long datasetId) {
		DatasetDTO.ResDatasetWithStorage datasetWithStorage = datasetService.getDatasetWithStorage(datasetId);
		Long id = datasetWithStorage.getDatasetId();
		List<WorkloadResDTO.UsingDatasetDTO> usingDatasetDTOS = workloadModuleFacadeService.workloadsUsingDataset(id);
		datasetWithStorage.addUsingDatasets(usingDatasetDTOS);
		return datasetWithStorage;
	}

	@Override
	@Transactional
	public void insertLocalDataset(DatasetDTO.CreateLocalDataset createDatasetDTO) {
		CreateLocalDatasetDTO createDto = CreateLocalDatasetDTO.builder()
			.namespace(namespace)
			.datasetName(createDatasetDTO.getDatasetName())
			.ip(createDatasetDTO.getIp())
			.storagePath(createDatasetDTO.getStoragePath())
			.dockerImage(dockerImage)
			.hostPath(hostPath)
			.build();
		//1. nginx deployment, pvc, pv, svc 생성
		CreateLocalDatasetResDTO createLocalDatasetResDTO = workloadModuleFacadeService.createLocalDataset(createDto);
		//2. 디비 인서트
		LocalDatasetEntity localDatasetEntity = LocalDatasetEntity.builder()
			.datasetName(createDatasetDTO.getDatasetName())
			.ip(createDatasetDTO.getIp())
			.storageType(StorageType.NFS)
			.storagePath(createDatasetDTO.getStoragePath())
			.dns(createLocalDatasetResDTO.getDns())
			.deploymentName(createLocalDatasetResDTO.getDeploymentName())
			.build();
		datasetService.insertLocalDataset(localDatasetEntity);
	}

	@Override
	@Transactional
	public void modifyDataset(DatasetDTO.ModifyDatset modifyDataset, Long datasetId, UserInfoDTO userInfoDTO) {
		//division 확인 후 astrago 데이터 셋이면 디비만 변경
		Dataset dataset = datasetService.findById(datasetId);
		if(userInfoDTO.getAuth() == AuthType.ROLE_ADMIN ||
			(userInfoDTO.getAuth() == AuthType.ROLE_USER && userInfoDTO.getId().equals(dataset.getRegUser().getRegUserId()))
		){
			//local 데이터 셋이면 디비 + deployment label 변경
			if(dataset.isLocalDataset()){
				LocalDatasetEntity localDatasetEntity = (LocalDatasetEntity)dataset;
				ModifyLocalDatasetDeploymentDTO modifyLocalDatasetDeploymentDTO = ModifyLocalDatasetDeploymentDTO
					.builder()
					.deploymentName(localDatasetEntity.getDeploymentName())
					.modifyDatasetName(modifyDataset.getDatasetName())
					.namespace(namespace)
					.build();
				workloadModuleFacadeService.modifyLocalDatasetDeployment(modifyLocalDatasetDeploymentDTO);
			}
			datasetService.modifyDataset(modifyDataset, datasetId);
		}else{
			throw new RuntimeException("데이터 셋 수정 권한이 없습니다.");
		}
	}
}
