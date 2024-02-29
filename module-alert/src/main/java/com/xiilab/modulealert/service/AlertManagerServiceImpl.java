package com.xiilab.modulealert.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.xiilab.modulealert.dto.AlertManagerDTO;
import com.xiilab.modulealert.entity.AlertManagerEntity;
import com.xiilab.modulealert.enumeration.CategoryType;
import com.xiilab.modulealert.repository.AlertManagerRepository;
import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.modulemonitor.service.K8sAlertService;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertManagerServiceImpl implements AlertManagerService{
	private final AlertManagerRepository alertManagerRepository;
	private final UserService userService;
	private final K8sAlertService k8sAlertService;

	@Override
	@Transactional
	public AlertManagerDTO.ResponseDTO saveAlertManager(AlertManagerDTO.RequestDTO requestDTO){

		AlertManagerEntity alertManagerEntity = requestDTO.convertEntity();

		alertManagerEntity.addCategory(requestDTO.getCategoryDTOList());

		alertManagerEntity.addUser(requestDTO.getUserIdList().stream().map(userId ->{
			UserInfo userInfo = userService.getUserInfoById(userId);

			return AlertManagerDTO.UserDTO.builder()
				.userId(userInfo.getId())
				.email(userInfo.getEmail())
				.userName(userInfo.getUserName())
				.firstName(userInfo.getFirstName())
				.lastName(userInfo.getLastName())
				.build();
		}).toList());

		alertManagerEntity.addNode(requestDTO.getNodeDTOList());

		AlertManagerEntity saveManager = alertManagerRepository.save(alertManagerEntity);

		List<String> alertExpr = creatExpr(requestDTO);
		try{
			// K8s Prometheus Rule 등록
			k8sAlertService.createPrometheusRule(saveManager.getId(), alertExpr);

		}catch (K8sException e){
			alertManagerRepository.deleteById(alertManagerEntity.getId());
			throw new K8sException(CommonErrorCode.ALERT_MANAGER_NOTFOUND_ROLE);
		}

		return AlertManagerDTO.ResponseDTO.toDTOBuilder().alertManager(saveManager).build();
	}

	@Override
	@Transactional
	public void deleteAlertManagerById(long id){
		try{
			// 등록된 monitor 삭제
			alertManagerRepository.deleteById(id);
			// 등록된 Prometheus 삭제
			k8sAlertService.deletePrometheusRule(id);
		}catch (IllegalArgumentException e){
			throw new RestApiException(CommonErrorCode.ALERT_MANAGER_DELETE_FAIL);
		}
	}

	@Override
	public AlertManagerDTO.ResponseDTO getAlertManagerById(long id){
		AlertManagerEntity alertManagerEntity = getAlertManagerEntityById(id);

		return AlertManagerDTO.ResponseDTO.toDTOBuilder().alertManager(alertManagerEntity).build();
	}
	// 알림 수정
	@Override
	@Transactional
	public void updateAlertManagerById(long id, AlertManagerDTO.RequestDTO requestDTO){
		validationCheck(requestDTO);
		AlertManagerEntity alertManagerEntity = getAlertManagerEntityById(id);

		alertManagerEntity.updateAlertManager(requestDTO);
		// category
		alertManagerEntity.getAlertCategoryEntityList().clear();
		alertManagerEntity.addCategory(requestDTO.getCategoryDTOList());
		// node
		alertManagerEntity.getAlertNodeEntityList().clear();
		alertManagerEntity.addNode(requestDTO.getNodeDTOList());
		// user
		alertManagerEntity.getAlertUserEntityList().clear();
		alertManagerEntity.addUser(requestDTO.getUserIdList().stream().map(userId ->{
			UserInfo userInfo = userService.getUserInfoById(userId);
			return AlertManagerDTO.UserDTO.builder()
				.userId(userInfo.getId())
				.email(userInfo.getEmail())
				.userName(userInfo.getUserName())
				.firstName(userInfo.getFirstName())
				.lastName(userInfo.getLastName())
				.build();
		}).toList());

		// k8s prometheusRule update
		k8sAlertService.updatePrometheusRule(alertManagerEntity.getId(), creatExpr(requestDTO));
	}

	@Override
	public List<AlertManagerDTO.ResponseDTO> getAlertManagerList(){
		List<AlertManagerEntity> allAlertManagerList = alertManagerRepository.findAll();
		return allAlertManagerList.stream().map(alertManagerEntity ->
				AlertManagerDTO.ResponseDTO.toDTOBuilder().alertManager(alertManagerEntity).build()
			).toList();
	}


	/**
	 * Promql PrometheusRule에 생성될 Alert query List
	 * @param requestDTO 해당 Rule에서 사용될 alertManagerDTO
	 * @return 생성된 PrometheusRule List
	 */
	public List<String> creatExpr(AlertManagerDTO.RequestDTO requestDTO){
		List<String> exprList = new ArrayList<>();
		// Alert Node별 Expr 생성
		for (AlertManagerDTO.NodeDTO nodeDTO : requestDTO.getNodeDTOList()) {
			if(Objects.nonNull(requestDTO.getCategoryDTOList())) {
				// NodePromql List 생성
				requestDTO.getCategoryDTOList().forEach(categoryDTO -> {
					String promql = "";
					switch (categoryDTO.getCategoryType()) {
						// 특정 노드의 GPU 온도를 평균 내어 설정한 값과 비교하여 알림을 발생시킵니다
						case GPU_TEMP:
							promql = CategoryType.GPU_TEMP.getQuery();
							break;
						// 특정 노드의 GPU 사용량 평균 내어 설정한 값과 비교하여 알림을 발생시킵니다
						case GPU_USAGE:
							promql = CategoryType.GPU_USAGE.getQuery();
							break;
						// 특정 노드의 GPU 메모리 사용량 평균 내어 설정한 값과 비교하여 알림을 발생시킵니다
						case GPU_MEMORY:
							promql = CategoryType.GPU_MEMORY.getQuery();
							break;
						// 특정 노드의 CPU 사용량 평균 내어 설정한 값과 비교하여 알림을 발생시킵니다
						case CPU_USAGE:
							promql = CategoryType.CPU_USAGE.getQuery();
							break;
						// 특정 노드의 MEMORY 사용량 평균 내어 설정한 값과 비교하여 알림을 발생시킵니다
						case MEMORY_USAGE:
							promql = CategoryType.MEMORY_USAGE.getQuery();
							break;
						// 특정 노드의 DISK 사용량 평균 내어 설정한 값과 비교하여 알림을 발생시킵니다
						case DISK_USAGE:
							promql = CategoryType.DISK_USAGE.getQuery();
							break;
					}
					// query List 생성
					exprList.add(String.format(promql, nodeDTO.getNodeName(), categoryDTO.getOperator(), categoryDTO.getMaximum())
						+ "~" + categoryDTO.getDurationTime() + "m~" + categoryDTO.getCategoryType() + "~" + nodeDTO.getNodeName());

				});
			}
		}
		return exprList;
	}

	private void validationCheck(AlertManagerDTO.RequestDTO requestDTO){
		// Email 수신 true인 경우 사용자 정보 없으면 안됨
		if(requestDTO.isEmailYN() && CollectionUtils.isEmpty(requestDTO.getUserIdList())){
			throw new RestApiException(CommonErrorCode.AlERT_MANAGER_NOTFOUND);
		}
	}

	private AlertManagerEntity getAlertManagerEntityById(long id){
		return alertManagerRepository.findById(id).orElseThrow(() ->
			new RestApiException(CommonErrorCode.AlERT_MANAGER_NOTFOUND));
	}
}
