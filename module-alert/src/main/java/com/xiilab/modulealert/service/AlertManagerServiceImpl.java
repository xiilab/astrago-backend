package com.xiilab.modulealert.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.xiilab.modulealert.dto.AlertManagerDTO;
import com.xiilab.modulealert.dto.AlertManagerReceiveDTO;
import com.xiilab.modulealert.dto.SystemAlertDTO;
import com.xiilab.modulealert.entity.AlertManagerEntity;
import com.xiilab.modulealert.entity.AlertManagerReceiveEntity;
import com.xiilab.modulealert.enumeration.AlertManagerCategoryType;
import com.xiilab.modulealert.enumeration.SystemAlertMessage;
import com.xiilab.modulealert.enumeration.SystemAlertType;
import com.xiilab.modulealert.repository.AlertManagerReceiveRepository;
import com.xiilab.modulealert.repository.AlertManagerRepository;
import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.modulecommon.util.DataConverterUtil;
import com.xiilab.modulemonitor.service.K8sAlertService;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.moduleuser.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertManagerServiceImpl implements AlertManagerService{
	private final AlertManagerRepository alertManagerRepository;
	private final AlertManagerReceiveRepository alertManagerReceiveRepository;
	private final SystemAlertService systemAlertService;
	private final UserService userService;
	private final K8sAlertService k8sAlertService;
	private final EmailService emailService;

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

		List<String> alertExpr = creatExpr(requestDTO.getNodeDTOList(), requestDTO.getCategoryDTOList());

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
			// 등록된 AlertManager 삭제
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
		alertManagerEntity.getAlertManagerCategoryEntityList().clear();
		alertManagerEntity.addCategory(requestDTO.getCategoryDTOList());
		// node
		alertManagerEntity.getAlertManagerNodeEntityList().clear();
		alertManagerEntity.addNode(requestDTO.getNodeDTOList());
		// user
		alertManagerEntity.getAlertManagerUserEntityList().clear();
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
		alertManagerEntity.getAlertList().clear();
		List<String> expr = creatExpr(requestDTO.getNodeDTOList(), requestDTO.getCategoryDTOList());
		// k8s prometheusRule update
		k8sAlertService.updatePrometheusRule(alertManagerEntity.getId(), expr);
	}

	@Override
	public List<AlertManagerDTO.ResponseDTO> getAlertManagerList(){
		List<AlertManagerEntity> allAlertManagerList = alertManagerRepository.findAll();
		return allAlertManagerList.stream().map(alertManagerEntity ->
				AlertManagerDTO.ResponseDTO.toDTOBuilder().alertManager(alertManagerEntity).build()
			).toList();
	}

	@Override
	public void receiveAlert(String alertData){
		String currentTime = "";
		// JSON 파서 생성
		JSONParser parser = new JSONParser();
		// JSON 파싱
		try {
			Object obj = parser.parse(alertData);
			JSONObject jsonObject = (JSONObject) obj;
			JSONArray alerts = (JSONArray) jsonObject.get("alerts");
			for (Object alertObj : alerts) {
				JSONObject alert = (JSONObject)alertObj;
				JSONObject labels = (JSONObject)alert.get("labels");
				JSONObject annotations = (JSONObject)alert.get("annotations");
				// 발생시간
				String startsAt = alert.get("startsAt").toString();
				LocalDateTime localDateTime = LocalDateTime.parse(startsAt, DateTimeFormatter.ISO_DATE_TIME);
				currentTime = DataConverterUtil.getCurrentTime(localDateTime);
				// alertname, ruleName, nodeName이 존재하는
				if(labels.get("alertname") != null && labels.get("ruleName") != null && labels.get("nodeName") != null){
					// 알림 분류 내용
					String categoryType = labels.get("alertname").toString();
					// 해당 ID 조회
					long id = Long.parseLong(labels.get("ruleName").toString().split("-")[1]);
					AlertManagerEntity alertManagerEntity = getAlertManagerEntityById(id);

					// 임계값 소수점 2자까지
					double value = Double.parseDouble(annotations.get("value").toString());
					String val = String.format("%.2f", value);
					// 발생한 node Name
					String nodeName = labels.get("nodeName").toString();

					alertManagerReceiveRepository.save(
						AlertManagerReceiveEntity.builder()
							.threshold(val)
							.alertName(alertManagerEntity.getAlertName())
							.categoryType(AlertManagerCategoryType.valueOf(categoryType))
							.nodeName(nodeName)
							.realTime(localDateTime)
							.currentTime(currentTime)
							.alertManager(alertManagerEntity)
							.build());
				}
				// 저장된 AlertList 조회
				Map<Long, List<AlertManagerReceiveDTO.ReceiveDTO>> alertReceiveDTOList = getAlertReceiveDTOList(currentTime);

				// 알림 전송
				for(Map.Entry<Long, List<AlertManagerReceiveDTO.ReceiveDTO>> alertReceive : alertReceiveDTOList.entrySet()) {
					AlertManagerDTO.ResponseDTO findAlertManagerDTO = getAlertManagerById(alertReceive.getKey());

					// 사용자 수신 설정에 따른 Email, System 분기
					if(findAlertManagerDTO.isEmailYN()){
						emailService.sendEmail(findAlertManagerDTO, alertReceive.getValue());
					}
					if(findAlertManagerDTO.isSystemYN()){
						for(AlertManagerReceiveDTO.ReceiveDTO alertManagerReceiveDTO : alertReceive.getValue()){
							findAlertManagerDTO.getUserDTOList().forEach(userDTO ->
								systemAlertService.sendAlert(SystemAlertDTO.builder()
									.title("노드 장애 알림")
									.recipientId(userDTO.getUserId())
									.systemAlertType(SystemAlertType.ALERT_MANAGER)
									.message(String.format(SystemAlertMessage.NODE_ERROR.getMessage(), alertManagerReceiveDTO.getNodeName()))
									.senderId("SYSTEM")
									.build())
							);
						}
					}
				}

			}
		} catch (ParseException e) {
			throw new RestApiException(CommonErrorCode.ALERT_MANAGER_JSON_PARSE_ERROR);
		}

	}

	@Override
	public List<AlertManagerReceiveDTO.ResponseDTO> getAlertManagerReceiveList(UserInfoDTO userInfoDTO){

		List<AlertManagerReceiveEntity> alertManagerReceiveEntityList = alertManagerReceiveRepository.findByAlertManagerAlertManagerUserEntityListUserId(userInfoDTO.getId());

		return alertManagerReceiveEntityList.stream().map(alertManagerReceiveEntity ->
				AlertManagerReceiveDTO.ResponseDTO.responseDTOBuilder()
					.alertManagerReceiveEntity(alertManagerReceiveEntity)
					.alertManagerEntity(alertManagerReceiveEntity.getAlertManager())
					.build()).toList();
	}
	@Override
	public void enableAlertManagerById(long id, boolean enable){
		if(enable){
			if(k8sAlertService.validationCheck(id)){
				AlertManagerEntity alertManagerEntity = getAlertManagerEntityById(id);
				alertManagerEntity.updateAlertEnable(enable);
				AlertManagerDTO.ResponseDTO findAlert = AlertManagerDTO.ResponseDTO.toDTOBuilder().alertManager(alertManagerEntity).build();
				List<String> expr = creatExpr(findAlert.getNodeDTOList(), findAlert.getCategoryDTOList());
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
	/**
	 * Promql PrometheusRule에 생성될 Alert query List
	 *
	 * @return 생성된 PrometheusRule List
	 */
	public List<String> creatExpr(List<AlertManagerDTO.NodeDTO> nodeDTOList, List<AlertManagerDTO.CategoryDTO> categoryDTOList){
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

	private void validationCheck(AlertManagerDTO.RequestDTO requestDTO){
		// Email 수신 true인 경우 사용자 정보 없으면 안됨
		if(requestDTO.isEmailYN() && CollectionUtils.isEmpty(requestDTO.getUserIdList())){
			throw new RestApiException(CommonErrorCode.ALERT_MANAGER_NOTFOUND);
		}
	}

	private AlertManagerEntity getAlertManagerEntityById(long id){
		return alertManagerRepository.findById(id).orElseThrow(() ->
			new RestApiException(CommonErrorCode.ALERT_MANAGER_NOTFOUND));
	}
	/**
	 * 해당시간 발생된 알림을 ID 별로 List<AlertManagerReciveDTO> 만들어주는 메소드
	 * @param currentTime 조회할 시간
	 * @return
	 */
	public Map<Long, List<AlertManagerReceiveDTO.ReceiveDTO>> getAlertReceiveDTOList(String currentTime){
		// 해당시간 발생된 AlertEntity List 조회
		List<AlertManagerReceiveEntity> alertList = alertManagerReceiveRepository.findAlertEntityByCurrentTime(currentTime).orElseThrow(() ->
			new RestApiException(CommonErrorCode.ALERT_MANAGER_ADD_RULE_FAIL));
		// AlertManager ID 별로 AlertDTO Map 생성
		List<AlertManagerReceiveDTO.ReceiveDTO> list = alertList.stream().map(alertManagerReceiveEntity ->
			AlertManagerReceiveDTO.ReceiveDTO.receiveDTOBuilder()
				.alertManagerReceiveEntity(alertManagerReceiveEntity)
				.build()).toList();
		return list.stream().collect(Collectors.collectingAndThen(
				Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(alertManagerReceiveDTO ->
					// 조회된 AlertList에서 NodeName, CategoryType, monitorID 중복 제거
					alertManagerReceiveDTO.getNodeName() + alertManagerReceiveDTO.getCategoryType() + alertManagerReceiveDTO.getAlertManagerId()))),
				ArrayList::new ))
			.stream().collect(Collectors.groupingBy(AlertManagerReceiveDTO::getAlertManagerId));
	}

}
