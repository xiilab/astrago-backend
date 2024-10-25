package com.xiilab.modulecommon.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.FileUploadErrorCode;

@Component
public class FileUploadService {
	@Value("${editor.root-file-upload-path}")
	private String rootFilePath;

	/**
	 * byte 배열의 데이터를 지정된 경로에 파일로 저장
	 *
	 * @param midPath 파일을 저장할 중간 디렉토리 경로(루트 경로 생략 O) ex) /uploadImg/1/1
	 * @param fileName 원본 파일명
	 * @param bytes 저장할 파일의 내용을 담은 byte 배열
	 * @return 파일 저장된 전체 경로 반환
	 * */
	public String saveBytesToFile(String midPath, String fileName, byte[] bytes) {
		try {
			// 파일명
			String saveFileFullPath = createSaveFileFullPath(midPath, fileName);
			// 파일 저장
			FileUtils.writeByteArrayToFile(new File(saveFileFullPath), bytes);
			return saveFileFullPath;
		} catch (IOException e) {
			throw new RestApiException(FileUploadErrorCode.FAILED_SAVE_FILE);
		}
	}

	/**
	 * MultipartFile 데이터를 지정된 경로에 파일로 저장
	 *
	 * @param midPath 파일을 저장할 중간 디렉토리 경로(루트 경로 생략) ex) /uploadImg/1/1
	 * @param multipartFile 업로드할 파일
	 * @return 파일 저장된 전체 경로 반환
	 * */
	public String saveMultipartFileToFile(String midPath, MultipartFile multipartFile) {
		try {
			// 파일 저장경로
			String saveFileFullPath = createSaveFileFullPath(midPath, multipartFile.getOriginalFilename());
			// 파일 저장
			multipartFile.transferTo(new File(saveFileFullPath));
			return saveFileFullPath;
		} catch (IOException e) {
			throw new RestApiException(FileUploadErrorCode.FAILED_SAVE_FILE);
		}
	}

	/**
	 * MultipartFile 데이터를 지정된 경로에 파일로 저장
	 *
	 * @param midPath 파일을 저장할 중간 디렉토리 경로(루트 경로 생략) ex) /uploadImg/1/1
	 * @param saveFileName 저장한 파일명
	 * @return 파일 저장된 전체 경로 반환
	 * */
	public byte[] getFileBytes(String midPath, String saveFileName) {
		try {
			// 파일 저장경로
			Path path = Paths.get(getRootFilePath() + File.separator + midPath + File.separator + saveFileName);
			return Files.readAllBytes(path);
		} catch (IOException e) {
			throw new RestApiException(FileUploadErrorCode.FAILED_READ_FILE);
		}
	}

	/**
	 * OS별 루트 디렉토리 경로 반환
	 */
	public String getRootFilePath() {
		return System.getProperty("os.name").toLowerCase().contains("win") ?
			System.getenv("USERPROFILE") + File.separator + "Desktop" : rootFilePath;
	}

	private String createSaveFilename(String fileName) {
		return FilenameUtils.getBaseName(fileName) + "_" + UUID.randomUUID() + "."
			+ LocalDate.now() + "." + FilenameUtils.getExtension(fileName);
	}

	private String createSaveFileFullPath(String midPath, String fileName) {
		String saveFilename = createSaveFilename(fileName);
		// 디렉토리 생성
		File directory = new File(getRootFilePath() + midPath);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		return getRootFilePath() + midPath + File.separator + saveFilename;
	}
}
