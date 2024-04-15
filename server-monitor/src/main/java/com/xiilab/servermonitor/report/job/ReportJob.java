package com.xiilab.servermonitor.report.job;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.xiilab.modulecommon.dto.MailDTO;
import com.xiilab.modulecommon.dto.ReportType;
import com.xiilab.modulecommon.enums.MailAttribute;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.ReportErrorCode;
import com.xiilab.modulecommon.service.MailService;
import com.xiilab.modulek8sdb.report.entity.ReportReservationEntity;
import com.xiilab.modulek8sdb.report.entity.ReportReservationHistoryEntity;
import com.xiilab.modulek8sdb.report.entity.ReportReservationUserEntity;
import com.xiilab.modulek8sdb.report.report.ReportReservationHistoryRepository;
import com.xiilab.modulek8sdb.report.report.ReservationRepository;
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
	private final MailService mailService;
	@Value("${frontend.url}")
	private String frontendUrl;
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
		// 검색 조건 startDate ~ endDate
		LocalDateTime endDate = LocalDateTime.now().minusDays(1);
		LocalDateTime startDate = getStartDate(ReportType.valueOf(jobDataMap.get("reportType").toString()), endDate);

		// (발송일 - 예약종료일) < 주기
		if(Math.abs(Duration.between(endDate, reservationEndDate).toDays()) < sendCycle){
			reportMonitorService.reportOnOff((long)jobDataMap.get("reportId").hashCode(), false);
			// 종료
			return;
		}
		String start = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String end = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String pdfLink = createPDFLink(reportReservation, start, end);

		sendMail(reportReservation, start, end, pdfLink);
	}


	private LocalDateTime getStartDate(ReportType reportType, LocalDateTime endDate){
		return switch (reportType){
			case WEEKLY_CLUSTER, WEEKLY_SYSTEM -> endDate.minusWeeks(1);
			case MONTHLY_CLUSTER, MONTHLY_SYSTEM -> endDate.minusMonths(1);
		};
	}

	private ReportReservationEntity getReportReservationEntityById(long id){
		return repository.findById(id).orElseThrow(() ->
			new RestApiException(ReportErrorCode.REPORT_NOT_FOUND));
	}

	private String createPDFLink(ReportReservationEntity reportReservation, String start, String end){
		return String.format("%s/preview/report/result?reportTypeResult=%s&startDate=%s&endDate=%s",
			frontendUrl, reportReservation.getReportType(), start, end);
	}

	private void sendMail(ReportReservationEntity reportReservation, String start, String end, String pdfLink){
		MailAttribute mail = MailAttribute.REPORT;
		// 수신자에게 발송
		for(ReportReservationUserEntity user : reportReservation.getReservationUserEntities()){
			ReportReservationHistoryEntity saveHistory = historyRepository.save(ReportReservationHistoryEntity.builder()
				.email(user.getEmail())
				.userName(user.getUserName())
				.transferDate(LocalDateTime.now())
				.firstName(user.getFirstName())
				.lastName(user.getLastName())
				.report(reportReservation)
				.result(true)
				.build());
			try{
				mailService.sendMail(MailDTO.builder()
					.subject(String.format(mail.getSubject(), reportReservation.getReportType().getName()))
					.title(mail.getTitle())
					.subTitle(String.format(mail.getSubTitle(), reportReservation.getReportType().getName(), LocalDateTime.now(), start + end))
					.contentTitle(mail.getContentTitle())
					.contents(List.of(MailDTO.Content.builder().col1("링크 : ").col2(pdfLink).build()))
					.footer(mail.getFooter())
					.receiverEmail(user.getEmail())
					.build());
			}catch (Exception e){
				saveHistory.falseResult();
			}
		}
	}
}