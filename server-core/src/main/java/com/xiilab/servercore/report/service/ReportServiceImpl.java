package com.xiilab.servercore.report.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.ReportErrorCode;
import com.xiilab.modulecommon.util.DataConverterUtil;
import com.xiilab.modulek8sdb.report.dto.ReportReservationDTO;
import com.xiilab.modulek8sdb.report.entity.ReportReservationEntity;
import com.xiilab.modulek8sdb.report.report.ReservationRepository;
import com.xiilab.modulek8sdb.report.report.ReservationRepositoryCustom;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.moduleuser.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{

	private final ReservationRepository repository;
	private final ReservationRepositoryCustom repositoryCustom;
	private final UserService userService;

	@Override
	@Transactional
	public ReportReservationDTO.ResponseDTO saveReportReservation(ReportReservationDTO.RequestDTO reservationDTO) {
		try{
			// 예약 기간 및 주기 확인
			if(sendCycleCheck(reservationDTO)){
				throw new RestApiException(ReportErrorCode.REPORT_SAVE_FAIL_OVER_CYCLE);
			}
			// DTO -> ENTITY Convert
			ReportReservationEntity reportReservationEntity = reservationDTO.convertEntity();
			// Report 수신자 추가
			reportReservationEntity.addUser(reservationDTO.getUserIdList().stream().map(userId ->{
				UserInfo userInfo = userService.getUserInfoById(userId);
				return ReportReservationDTO.UserDTO.builder()
					.userId(userInfo.getId())
					.email(userInfo.getEmail())
					.userName(userInfo.getUserName())
					.firstName(userInfo.getFirstName())
					.lastName(userInfo.getLastName())
					.build();
			}).toList());
			// Report 저장
			ReportReservationEntity saveEntity = repository.save(reportReservationEntity);
			// 저장된 Report Entity -> DTO 변환
			return ReportReservationDTO.ResponseDTO.toDTOBuilder().reportReservation(saveEntity).build();
		}catch (IllegalArgumentException e){
			throw new RestApiException(ReportErrorCode.REPORT_SAVE_FAIL);
		}
	}

	@Override
	@Transactional
	public void deleteReportReservation(long id) {
		try{
			// 등록된 Report 예약 삭제
			repository.deleteById(id);
		}catch (IllegalArgumentException e){
			throw new RestApiException(ReportErrorCode.REPORT_DELETE_FAIL);
		}
	}

	@Override
	public ReportReservationDTO.ResponseDTO getReportReservationById(long id) {
		ReportReservationEntity reportReservationEntityById = getReportReservationEntityById(id);

		return ReportReservationDTO.ResponseDTO.toDTOBuilder().reportReservation(reportReservationEntityById).build();
	}

	@Override
	@Transactional
	public void updateReportReservationById(long id, ReportReservationDTO.RequestDTO reservationDTO) {
		if(sendCycleCheck(reservationDTO)){
			throw new RestApiException(ReportErrorCode.REPORT_SAVE_FAIL_OVER_CYCLE);
		}
		ReportReservationEntity reportReservation = getReportReservationEntityById(id);

		reportReservation.updateReport(reservationDTO);

		if(Objects.nonNull(reservationDTO.getUserIdList())){
			reportReservation.getReservationUserEntities().clear();
			reportReservation.addUser(reservationDTO.getUserIdList().stream().map(userId ->{
				UserInfo userInfo = userService.getUserInfoById(userId);
				return ReportReservationDTO.UserDTO.builder()
					.userId(userInfo.getId())
					.email(userInfo.getEmail())
					.userName(userInfo.getUserName())
					.firstName(userInfo.getFirstName())
					.lastName(userInfo.getLastName())
					.build();
			}).toList());
		}
	}

	@Override
	public Page<ReportReservationDTO.ResponseDTO> getReportReservationList(Pageable pageable, UserInfoDTO userInfoDTO) {
		PageRequest pageRequest = null;
		if (pageable != null && !ObjectUtils.isEmpty(pageable.getPageNumber()) && !ObjectUtils.isEmpty(
			pageable.getPageSize())) {
			pageRequest = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
		}

		Page<ReportReservationEntity> reservationList = repositoryCustom.getReportReservationList(userInfoDTO.getId(), pageRequest);

		return reservationList.map(reportReservation ->
			ReportReservationDTO.ResponseDTO.toDTOBuilder().reportReservation(reportReservation).build());
	}

	@Override
	public Page<ReportReservationDTO.ReceiveDTO> getReportReceiveList(Pageable pageable, UserInfoDTO userInfoDTO) {
		PageRequest pageRequest = null;
		if (pageable != null && !ObjectUtils.isEmpty(pageable.getPageNumber()) && !ObjectUtils.isEmpty(
			pageable.getPageSize())) {
			pageRequest = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
		}

		return repositoryCustom.getReportReceiveList(userInfoDTO.getId(), pageRequest);
	}

	@Override
	public ReportReservationDTO.DetailDTO getReportReceiveListById(long id, UserInfoDTO userInfoDTO) {

		ReportReservationEntity reportReservationEntity = repositoryCustom.getReportReceiveListById(id, userInfoDTO.getId());
		return ReportReservationDTO.DetailDTO.builder()
			.reportType(reportReservationEntity.getReportType())
			.reportName(reportReservationEntity.getName())
			.explanation(reportReservationEntity.getExplanation())
			.historyDTOList(
				reportReservationEntity.getReportReservationHistoryEntityList()
					.stream().map(history ->
						ReportReservationDTO.HistoryDTO.builder()
							.transferDate(DataConverterUtil.getCurrentTime(history.getTransferDate()))
							.userName(history.getUserName())
							.firstName(history.getFirstName())
							.lastName(history.getLastName())
							.userEmail(history.getEmail())
							.result(history.isResult())
							.build()
					).toList())
			.build();
	}

	private ReportReservationEntity getReportReservationEntityById(long id){
		return repository.findById(id).orElseThrow(() ->
			new RestApiException(ReportErrorCode.REPORT_NOT_FOUND));
	}

	private boolean sendCycleCheck(ReportReservationDTO.RequestDTO requestDTO){
		boolean result = false;
		LocalDateTime startDate = DataConverterUtil.dataFormatterByStr(requestDTO.getStartDate());
		LocalDateTime endDate = DataConverterUtil.dataFormatterByStr(requestDTO.getEndDate());

		long betweenDay = ChronoUnit.DAYS.between(startDate, endDate);

		if(betweenDay < requestDTO.getSendCycle()){
			result = true;
		}
		return result;
	}
}
