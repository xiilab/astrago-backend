package com.xiilab.serverbatch.job;

import java.io.IOException;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.TusErrorCode;

import lombok.extern.slf4j.Slf4j;
import me.desair.tus.server.TusFileUploadService;

@Slf4j
@Component
public class TusCleanUpJob extends QuartzJobBean {
	@Autowired
	private TusFileUploadService tusFileUploadService;

	@Override
	protected void executeInternal(JobExecutionContext context) {
		try {
			log.info("TUS clean up job start....");
			tusFileUploadService.cleanup();
			log.info("TUS clean up job end....");
		} catch (IOException e) {
			throw new RestApiException(TusErrorCode.CLEANUP_FAILED_MESSAGE);
		}
	}
}
