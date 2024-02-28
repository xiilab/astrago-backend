package com.xiilab.modulealert.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.xiilab.modulealert.dto.AlertManagerDTO;
import com.xiilab.modulealert.entity.AlertManagerEntity;
import com.xiilab.modulealert.enumeration.CategoryType;
import com.xiilab.modulealert.repository.AlertManagerRepository;
import com.xiilab.modulemonitor.service.K8sAlertService;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertManagerServiceImpl implements AlertManagerService{
	private final AlertManagerRepository alertManagerRepository;
	private final UserService userService;
	private final K8sAlertService k8sAlertService;

	@Override
	public AlertManagerDTO.ResponseDTO saveAlertManager(AlertManagerDTO.RequestDTO requestDTO){

		AlertManagerEntity alertManagerEntity = requestDTO.convertEntity();

		alertManagerEntity.addCategory(requestDTO.getCategoryDTOList());

		alertManagerEntity.addUser(requestDTO.getUserIdList().stream().map(userId ->{
			UserInfo userInfo = userService.getUserInfoById(userId);
			return AlertManagerDTO.UserDTO.builder()
				.id(userInfo.getId())
				.email(userInfo.getEmail())
				.userName(userInfo.getUserName())
				.firstName(userInfo.getFirstName())
				.lastName(userInfo.getLastName())
				.build();
		}).toList());

		alertManagerEntity.addNode(requestDTO.getNodeDTOList());

		AlertManagerEntity saveManager = alertManagerRepository.save(alertManagerEntity);

		AlertManagerDTO.ResponseDTO responseDTO = AlertManagerDTO.ResponseDTO.toDTOBuilder().alertManager(saveManager).build();

		List<String> alertExpr = creatExpr(responseDTO);

		k8sAlertService.createPrometheusRule(saveManager.getId(), alertExpr);

		return responseDTO;
	}

	// 알림 삭제


	// 알림 상세


	// 알림 수정

	// 알림 목록 조회


	/**
	 * Promql PrometheusRule에 생성될 Alert query List
	 * @param responseDTO 해당 Rule에서 사용될 alertManagerDTO
	 * @return 생성된 PrometheusRule List
	 */
	public List<String> creatExpr(AlertManagerDTO.ResponseDTO responseDTO){
		List<String> exprList = new ArrayList<>();
		// Monitor Alert Node별 Expr 생성
		for (AlertManagerDTO.NodeDTO nodeDTO : responseDTO.getNodeDTOList()) {
			if(Objects.nonNull(responseDTO.getCategoryDTOList())) {
				// NodePromql List 생성
				responseDTO.getCategoryDTOList().forEach(categoryDTO -> {
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
}
