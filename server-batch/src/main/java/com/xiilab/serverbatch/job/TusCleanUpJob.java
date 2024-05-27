package com.xiilab.serverbatch.job;

import java.io.IOException;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.TusErrorCode;

import lombok.RequiredArgsConstructor;
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
			tusFileUploadService.cleanup();
			log.info("TUS expiration time exceeded file deletion completed");
		} catch (IOException e) {
			throw new RestApiException(TusErrorCode.CLEANUP_FAILED_MESSAGE);
		}
	}
}
