package com.xiilab.servercore.alert.alertmanager.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.modulecommon.util.DataConverterUtil;
import com.xiilab.modulek8sdb.alert.alertmanager.dto.AlertManagerDTO;
import com.xiilab.modulek8sdb.alert.alertmanager.dto.AlertManagerReceiveDTO;
import com.xiilab.modulek8sdb.alert.alertmanager.entity.AlertManagerEntity;
import com.xiilab.modulek8sdb.alert.alertmanager.entity.AlertManagerNodeEntity;
import com.xiilab.modulek8sdb.alert.alertmanager.entity.AlertManagerReceiveEntity;
import com.xiilab.modulek8sdb.alert.alertmanager.enumeration.AlertManagerCategoryType;
import com.xiilab.modulek8sdb.alert.alertmanager.repository.AlertManagerReceiveRepository;
import com.xiilab.modulek8sdb.alert.alertmanager.repository.AlertManagerRepoCustom;
import com.xiilab.modulek8sdb.alert.alertmanager.repository.AlertManagerRepository;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.AlertName;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.SystemAlertMessage;
import com.xiilab.modulecommon.alert.enums.AlertName;
import com.xiilab.modulecommon.alert.enums.SystemAlertMessage;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.moduleuser.service.UserService;
import com.xiilab.modulecommon.alert.event.AdminAlertEvent;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertManagerServiceImpl implements AlertManagerService{

	private final AlertManagerReceiveRepository receiveRepository;
	private final AlertManagerRepository repository;
	private final AlertManagerRepoCustom alertManagerRepoCustom;
	private final UserService userService;
	private final ApplicationEventPublisher eventPublisher;
	@Override
	@Transactional
	public AlertManagerDTO.ResponseDTO saveAlertManager(AlertManagerDTO.RequestDTO requestDTO) {
		validationCheck(requestDTO);
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

		AlertManagerEntity saveManager = repository.save(alertManagerEntity);

		return AlertManagerDTO.ResponseDTO.toDTOBuilder().alertManager(saveManager).build();
	}

	@Override
	public void deleteAlertManagerById(long id) {
		try{
			// 등록된 AlertManager 삭제
			repository.deleteById(id);
		}catch (IllegalArgumentException e){
			throw new RestApiException(CommonErrorCode.ALERT_MANAGER_DELETE_FAIL);
		}
	}

	@Override
	public AlertManagerDTO.ResponseDTO getAlertManagerById(long id) {
		AlertManagerEntity alertManagerEntity = getAlertManagerEntityById(id);

		return AlertManagerDTO.ResponseDTO.toDTOBuilder().alertManager(alertManagerEntity).build();
	}

	@Override
	@Transactional
	public void updateAlertManagerById(long id, AlertManagerDTO.RequestDTO requestDTO) {
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
	}

	@Override
	public List<AlertManagerDTO.ResponseDTO> getAlertManagerList() {
		List<AlertManagerEntity> allAlertManagerList = repository.findAll();
		return allAlertManagerList.stream().map(alertManagerEntity ->
			AlertManagerDTO.ResponseDTO.toDTOBuilder().alertManager(alertManagerEntity).build()
		).toList();
	}

	@Override
	@Transactional
	public void receiveAlert(String alertData){
		String currentTime = "";
		// JSON 파서 생성
		org.json.simple.parser.JSONParser parser = new JSONParser();
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

					AlertManagerEntity alertManagerEntity = repository.findById(id).orElse(null);

					// 임계값 소수점 2자까지
					double value = Double.parseDouble(annotations.get("value").toString());
					String val = String.format("%.2f", value);
					// 발생한 node Name
					String nodeName = labels.get("nodeName").toString();

					if(alertManagerEntity != null){

						String nodeIp = alertManagerEntity.getAlertManagerNodeEntityList().stream()
							.filter(alertManagerNodeEntity -> alertManagerNodeEntity.getNodeName().equals(nodeName))
							.map(AlertManagerNodeEntity::getNodeIp).findFirst().orElse("");

						receiveRepository.save(
							AlertManagerReceiveEntity.builder()
								.threshold(val)
								.alertName(alertManagerEntity.getAlertName())
								.categoryType(AlertManagerCategoryType.valueOf(categoryType))
								.nodeName(nodeName)
								.realTime(localDateTime)
								.nodeIp(nodeIp)
								.currentTime(currentTime)
								.alertManager(alertManagerEntity)
								.result(true)
								.build());
					}
				}
				// 저장된 AlertList 조회
				Map<Long, List<AlertManagerReceiveDTO.ReceiveDTO>> alertReceiveDTOList = getAlertReceiveDTOList(currentTime);

				// 알림 전송
				for(Map.Entry<Long, List<AlertManagerReceiveDTO.ReceiveDTO>> alertReceive : alertReceiveDTOList.entrySet()) {

					AlertManagerDTO.ResponseDTO findAlertManagerDTO = getAlertManagerById(alertReceive.getKey());

					// // 사용자 수신 설정에 따른 Email, System 분기
					SystemAlertMessage nodeError = SystemAlertMessage.NODE_ERROR;
					String mailTitle = nodeError.getMailTitle();
					String title = nodeError.getTitle();
					for(AlertManagerReceiveDTO.ReceiveDTO alertManagerReceiveDTO : alertReceive.getValue()){
						String message = String.format(nodeError.getMessage(), alertManagerReceiveDTO.getNodeName());
						// 노드 장애 알림 발송
						eventPublisher.publishEvent(new AdminAlertEvent(AlertName.ADMIN_NODE_ERROR, null, null, null, mailTitle, title, message));
					}
					// if(findAlertManagerDTO.isEmailYN()){
					// 	emailService.sendEmail(findAlertManagerDTO, alertReceive.getValue());
					// }
					// if(findAlertManagerDTO.isSystemYN()){
					// 	for(AlertManagerReceiveDTO.ReceiveDTO alertManagerReceiveDTO : alertReceive.getValue()){
					// 		findAlertManagerDTO.getUserDTOList().forEach(userDTO ->
					// 			systemAlertService.sendAlert(SystemAlertDTO.builder()
					// 				.title("노드 장애 알림")
					// 				.recipientId(userDTO.getUserId())
					// 				.systemAlertType(SystemAlertType.ALERT_MANAGER)
					// 				.message(String.format(SystemAlertMessage.NODE_ERROR.getMessage(), alertManagerReceiveDTO.getNodeName()))
					// 				.senderId("SYSTEM")
					// 				.build())
					// 		);
					// 	}
					// }
				}

			}
		} catch (ParseException e) {
			throw new RestApiException(CommonErrorCode.ALERT_MANAGER_JSON_PARSE_ERROR);
		}

	}

	@Override
	public Page<AlertManagerReceiveDTO.ResponseDTO> getAlertManagerReceiveList(String categoryType, String startDate,
		String endDate, String search, UserInfoDTO userInfoDTO, Pageable pageable) {
		PageRequest pageRequest = null;
		if (pageable != null && !ObjectUtils.isEmpty(pageable.getPageNumber()) && !ObjectUtils.isEmpty(
			pageable.getPageSize())) {
			pageRequest = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
		}
		Page<AlertManagerReceiveEntity> alertManagerReceiveList = alertManagerRepoCustom.getAlertManagerReceiveList(categoryType, search,
			DataConverterUtil.dataFormatterByStr(startDate), DataConverterUtil.dataFormatterByStr(endDate), userInfoDTO.getId(), pageRequest);

		return alertManagerReceiveList.map(alertManagerReceiveEntity ->
				AlertManagerReceiveDTO.ResponseDTO.responseDTOBuilder()
					.alertManagerReceiveEntity(alertManagerReceiveEntity)
					.alertManagerEntity(alertManagerReceiveEntity.getAlertManager())
					.build());
	}

	@Override
	public AlertManagerReceiveDTO.ResponseDTO getAlertManagerReceiveByReceiveId(long id) {
		AlertManagerReceiveEntity alertManagerReceiveEntity = receiveRepository.findById(id).orElseThrow(() ->
			new RestApiException(CommonErrorCode.ALERT_MANAGER_NOTFOUND));

		return AlertManagerReceiveDTO.ResponseDTO.responseDTOBuilder()
			.alertManagerReceiveEntity(alertManagerReceiveEntity)
			.alertManagerEntity(alertManagerReceiveEntity.getAlertManager())
			.build();
	}

	@Override
	@Transactional
	public AlertManagerDTO.ResponseDTO enableAlertManagerById(long id, boolean enable) {
		AlertManagerEntity alertManagerEntity = getAlertManagerEntityById(id);
		alertManagerEntity.updateAlertEnable(enable);

		return AlertManagerDTO.ResponseDTO.toDTOBuilder().alertManager(alertManagerEntity).build();
	}

	private void validationCheck(AlertManagerDTO.RequestDTO requestDTO){
		// Email 수신 true인 경우 사용자 정보 없으면 안됨
		if(requestDTO.isEmailYN() && requestDTO.isSystemYN() && CollectionUtils.isEmpty(requestDTO.getUserIdList())){
			throw new RestApiException(CommonErrorCode.ALERT_MANAGER_EMAIL_ERROR);
		}
	}

	private AlertManagerEntity getAlertManagerEntityById(long id){
		return repository.findById(id).orElseThrow(() ->
			new RestApiException(CommonErrorCode.ALERT_MANAGER_NOTFOUND));
	}

	/**
	 * 해당시간 발생된 알림을 ID 별로 List<AlertManagerReciveDTO> 만들어주는 메소드
	 * @param currentTime 조회할 시간
	 * @return
	 */
	private Map<Long, List<AlertManagerReceiveDTO.ReceiveDTO>> getAlertReceiveDTOList(String currentTime){
		// 해당시간 발생된 AlertEntity List 조회
		List<AlertManagerReceiveEntity> alertList = receiveRepository.findAlertEntityByCurrentTime(currentTime).orElseThrow(() ->
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
