package com.xiilab.servermonitor.report.job;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.xiilab.modulecommon.dto.ReportType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.modulek8sdb.report.entity.ReportReservationEntity;
import com.xiilab.modulek8sdb.report.entity.ReportReservationHistoryEntity;
import com.xiilab.modulek8sdb.report.entity.ReportReservationUserEntity;
import com.xiilab.modulek8sdb.report.report.ReportReservationHistoryRepository;
import com.xiilab.modulek8sdb.report.report.ReservationRepository;
import com.xiilab.servermonitor.report.service.ReportFacadeService;
import com.xiilab.servermonitor.report.service.ReportMonitorService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@RequiredArgsConstructor
public class ReportJob extends QuartzJobBean {

	private final ReservationRepository repository;
	private final ReportReservationHistoryRepository historyRepository;
	private final ReportMonitorService reportMonitorService;
	private final ReportFacadeService reportFacadeService;
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
	@Override
	@Transactional
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobDataMap = context.getMergedJobDataMap();
		// 에약 Entity
		ReportReservationEntity reportReservation = getReportReservationEntityById(
			jobDataMap.get("reportId").hashCode());
		// 발송 주기
		long sendCycle= jobDataMap.get("sendCycle").hashCode();
		// 예약 종료일
		LocalDateTime reservationEndDate = LocalDateTime.parse(jobDataMap.get("endDate").toString(), formatter);
		// startDate ~ endDate
		LocalDateTime endDate = LocalDateTime.now().minusDays(1);
		LocalDateTime startDate = getStartDate(ReportType.valueOf(jobDataMap.get("reportType").toString()), endDate);

		// (발송일 - 예약종료일) < 주기
		if(Math.abs(Duration.between(endDate, reservationEndDate).toDays()) < sendCycle){
			reportMonitorService.reportOnOff((long)jobDataMap.get("reportId").hashCode(), false);
		}

		// TODO 리포트 PDF 생성
		createReport();

		// 수신자에게 발송
		for(ReportReservationUserEntity user : reportReservation.getReservationUserEntities()){
			ReportReservationHistoryEntity saveHistory = historyRepository.save(ReportReservationHistoryEntity.builder()
				.email(user.getEmail())
				.userName(user.getUserName())
				.transferDate(endDate)
				.report(reportReservation)
				.result(true)
				.build());
			try{
				//TODO 추후 PDF HTML 양식 받으면 메일 전송 로직 추가
			}catch (Exception e){
				saveHistory.falseResult();
				e.getMessage();
			}
		}
	}


	private LocalDateTime getStartDate(ReportType reportType, LocalDateTime endDate){
		return switch (reportType){
			case WEEKLY_CLUSTER, WEEKLY_SYSTEM -> endDate.minusWeeks(1);
			case MONTHLY_CLUSTER, MONTHLY_SYSTEM -> endDate.minusMonths(1);
		};
	}



	private String createReport(){

		return null;
	}

	private ReportReservationEntity getReportReservationEntityById(long id){
		return repository.findById(id).orElseThrow(() ->
			new RestApiException(CommonErrorCode.REPORT_NOT_FOUND));
	}
}
