package com.xiilab.servermonitor.report.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.ReportErrorCode;
import com.xiilab.modulek8sdb.report.entity.ReportReservationEntity;
import com.xiilab.modulek8sdb.report.report.ReservationRepository;
import com.xiilab.servermonitor.report.job.ReportJob;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportMonitorServiceImpl implements ReportMonitorService {
	private static final String ASTRA = "astra";
	private final ReservationRepository repository;
	private final Scheduler scheduler;

	@Override
	@Transactional
	public void reportOnOff(Long id, boolean enable){
		ReportReservationEntity report = getReportReservationEntityById(id);
		report.updateEnable(enable);

		try{
			// true 알림기능 ON
			if(enable){
				JobDataMap jobDataMap = createJobDataMap(report);
				JobDetail reservationJob = createReservationJob(report.getId(), jobDataMap);
				Trigger reservationTrigger = createTrigger(reservationJob, report);
				scheduler.scheduleJob(reservationJob, reservationTrigger);
			// False 알림기능 OFF
			}else {
				stopReportJob(report.getId());
			}
		}catch (SchedulerException e){
			e.toString();
		}

	}

	private void stopReportJob(long id) {
		try{
			Set<JobKey> astra = scheduler.getJobKeys(GroupMatcher.groupEquals(ASTRA));

			List<JobKey> list = astra.stream().filter(jobKey -> jobKey.getName().contains(String.valueOf(id))).toList();
			scheduler.deleteJobs(list);
		}catch (SchedulerException e){
			e.getMessage();
		}
	}


	private ReportReservationEntity getReportReservationEntityById(long id){
		return repository.findById(id).orElseThrow(() ->
			new RestApiException(ReportErrorCode.REPORT_NOT_FOUND));
	}

	private JobDetail createReservationJob(long id, JobDataMap jobDataMap){
		return JobBuilder.newJob(ReportJob.class)
			.withIdentity(String.valueOf(id), ASTRA)
			.usingJobData(jobDataMap)
			.build();
	}

	public JobDataMap createJobDataMap(ReportReservationEntity reportReservation){
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("reportId", reportReservation.getId());
		jobDataMap.put("reportType", reportReservation.getReportType());
		jobDataMap.put("sendCycle", reportReservation.getSendCycle());
		jobDataMap.put("startDate", reportReservation.getStartDate());
		jobDataMap.put("endDate", reportReservation.getEndDate());
		jobDataMap.put("userId", reportReservation.getRegUser().getRegUserId());
		jobDataMap.put("userName", reportReservation.getRegUser().getRegUserName());
		jobDataMap.put("userRealName", reportReservation.getRegUser().getRegUserRealName());

		return jobDataMap;
	}

	private Trigger createTrigger(JobDetail jobDetail, ReportReservationEntity report){
		return TriggerBuilder.newTrigger()
			.withSchedule(CronScheduleBuilder.cronSchedule(getCronExpression(report.getSendCycle(), report.getStartDate())))
			.startNow()
			.forJob(jobDetail)
			.endAt(Date.from(report.getEndDate().atZone(ZoneId.systemDefault()).toInstant()))
			.build();
	}
	private String getCronExpression(long period, LocalDateTime startDate) {
		switch ((int)period) {
			case 1:
				return String.format("0 %s %s 1/1 * ?", startDate.getMinute(), startDate.getHour());
			case 7:
				return String.format("0 %s %s ? * MON", startDate.getMinute(), startDate.getHour());
			case 30:
				return String.format("0 %s %s 1 * ?", startDate.getMinute(), startDate.getHour());
			default:
				throw new IllegalArgumentException("Invalid period: " + period);
		}

	}
}
