package com.xiilab.servercore.alert.alertmanager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.modulek8s.alertmanager.service.K8sAlertService;
import com.xiilab.modulek8sdb.alert.alertmanager.dto.AlertManagerDTO;
import com.xiilab.modulek8sdb.alert.alertmanager.enumeration.AlertManagerCategoryType;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AlertManagerFacadeServiceImpl implements AlertManagerFacadeService{
	private final AlertManagerService alertManagerService;
	private final K8sAlertService k8sAlertService;

	@Override
	public AlertManagerDTO.ResponseDTO saveAlertManager(AlertManagerDTO.RequestDTO requestDTO) {
		// alertManager DB 저장
		AlertManagerDTO.ResponseDTO responseDTO = alertManagerService.saveAlertManager(requestDTO);

		// alertExpr 생성
		List<String> alertExpr = creatExpr(requestDTO.getNodeDTOList(), requestDTO.getCategoryDTOList());

		try{
			// K8s Prometheus Rule 등록
			k8sAlertService.createPrometheusRule(responseDTO.getId(), alertExpr);

		}catch (K8sException e){
			alertManagerService.deleteAlertManagerById(responseDTO.getId());
			throw new K8sException(CommonErrorCode.ALERT_MANAGER_NOTFOUND_ROLE);
		}
		return responseDTO;
	}

	@Override
	public void deleteAlertManagerById(long id) {
		// promethrus 삭제
		k8sAlertService.deletePrometheusRule(id);
		// db 삭제
		alertManagerService.deleteAlertManagerById(id);
	}

	@Override
	public void updateAlertManagerById(long id, AlertManagerDTO.RequestDTO requestDTO) {
		// expr 생성
		List<String> expr = creatExpr(requestDTO.getNodeDTOList(), requestDTO.getCategoryDTOList());
		// DB 업데이트
		alertManagerService.updateAlertManagerById(id, requestDTO);
		// k8s prometheusRule update
		k8sAlertService.updatePrometheusRule(id, expr);
	}

	@Override
	public void enableAlertManagerById(long id, boolean enable) {
		AlertManagerDTO.ResponseDTO requestDTO = alertManagerService.enableAlertManagerById(id, enable);
		if(enable){
			if(k8sAlertService.validationCheck(id)){
				List<String> expr = creatExpr(requestDTO.getNodeDTOList(), requestDTO.getCategoryDTOList());
				// K8s Prometheus Rule 등록
				k8sAlertService.createPrometheusRule(id, expr);
			}else{
				throw new RestApiException(CommonErrorCode.ALERT_MANAGER_RULE_READY);
			}
		}else {
			// 등록된 Prometheus 삭제
			k8sAlertService.deletePrometheusRule(id);
		}
	}



	private List<String> creatExpr(List<AlertManagerDTO.NodeDTO> nodeDTOList, List<AlertManagerDTO.CategoryDTO> categoryDTOList){
		List<String> exprList = new ArrayList<>();
		// Alert Node별 Expr 생성
		for (AlertManagerDTO.NodeDTO nodeDTO : nodeDTOList) {
			if(Objects.nonNull(categoryDTOList)) {
				// NodePromql List 생성
				categoryDTOList.forEach(categoryDTO -> {
					String promql = "";
					switch (categoryDTO.getCategoryType()) {
						// 특정 노드의 GPU 온도를 평균 내어 설정한 값과 비교하여 알림을 발생시킵니다
						case GPU_TEMP:
							promql = AlertManagerCategoryType.GPU_TEMP.getQuery();
							break;
						// 특정 노드의 GPU 사용량 평균 내어 설정한 값과 비교하여 알림을 발생시킵니다
						case GPU_USAGE:
							promql = AlertManagerCategoryType.GPU_USAGE.getQuery();
							break;
						// 특정 노드의 GPU 메모리 사용량 평균 내어 설정한 값과 비교하여 알림을 발생시킵니다
						case GPU_MEMORY:
							promql = AlertManagerCategoryType.GPU_MEMORY.getQuery();
							break;
						// 특정 노드의 CPU 사용량 평균 내어 설정한 값과 비교하여 알림을 발생시킵니다
						case CPU_USAGE:
							promql = AlertManagerCategoryType.CPU_USAGE.getQuery();
							break;
						// 특정 노드의 MEMORY 사용량 평균 내어 설정한 값과 비교하여 알림을 발생시킵니다
						case MEMORY_USAGE:
							promql = AlertManagerCategoryType.MEMORY_USAGE.getQuery();
							break;
						// 특정 노드의 DISK 사용량 평균 내어 설정한 값과 비교하여 알림을 발생시킵니다
						case DISK_USAGE:
							promql = AlertManagerCategoryType.DISK_USAGE.getQuery();
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
