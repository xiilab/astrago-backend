package com.xiilab.servercore.tus.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.TusErrorCode;
import com.xiilab.servercore.common.utils.CoreFileUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;

@Slf4j
@Service
@RequiredArgsConstructor
public class TusService {
	private final TusFileUploadService tusFileUploadService;

	public void tusUpload(HttpServletRequest request, HttpServletResponse response) {
		try {
			// 업로드
			tusFileUploadService.process(request, response);

			// 현재 업로드 정보
			UploadInfo uploadInfo = tusFileUploadService.getUploadInfo(request.getRequestURI());

			// 완료 된 경우 파일 저장
			if (uploadInfo != null && !uploadInfo.isUploadInProgress()) {
				// "metadata"로 넘어온 원본 파일명 추출
				String filename = Optional.ofNullable(uploadInfo.getMetadata().get("filename"))
					.orElseThrow(() -> new RestApiException(TusErrorCode.FILE_NAME_ERROR_MESSAGE));


				// 파일 저장
				CoreFileUtils.saveInputStreamToFile(uploadInfo.getMetadata().get("path"), filename,
					tusFileUploadService.getUploadedBytes(request.getRequestURI()));

				// 임시 파일 삭제
				tusFileUploadService.deleteUpload(request.getRequestURI());
			}
		} catch (IOException | TusException e) {
			log.error("exception was occurred. message={}", e.getMessage(), e);
			throw new RestApiException(TusErrorCode.UPLOAD_FAILED_MESSAGE);
		}
	}
}
