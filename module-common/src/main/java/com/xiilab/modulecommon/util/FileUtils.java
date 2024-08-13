package com.xiilab.modulecommon.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.dto.FileInfoDTO;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 해당 클래스는 AstraGo에서 관리하는 파일에 관련된 utils입니다.
 */
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class FileUtils {
	private static final String ROOT_PATH = "astrago";
	private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

	public static String getUserFolderPath(String username) {
		return System.getProperty("user.home") + File.separator + ROOT_PATH + File.separator + username;
	}

	public static Path createFolders(Path path) throws IOException {
		return Files.createDirectories(path);
	}

	public static String getUserLogPath(String username, String workloadName) {
		String userFolderPath = getUserFolderPath(username);
		return userFolderPath + File.separator + workloadName + ".log";
	}

	public static void saveLogFile(String logContent, String jobName, String username) throws IOException {
		Path userFolders = createFolders(Path.of(getUserFolderPath(username)));
		Files.writeString(Path.of(getUserLogPath(username, jobName)), logContent, StandardOpenOption.CREATE);
	}

	public static FileInfoDTO copyFile(Path sourcePath, Path targetPath) throws IOException {
		Path targetFile = targetPath.resolve(sourcePath.getFileName());
		Files.copy(sourcePath, targetFile, StandardCopyOption.REPLACE_EXISTING);
		return FileInfoDTO.builder()
			.fileName(targetFile.getFileName().toString())
			.size(String.valueOf(Files.size(targetFile)))
			.build();
	}

	public static void deleteDirectory(String path) {
		try {
			Path targetPath = Paths.get(path);
			Files.delete(targetPath);
		} catch (IOException e) {
			throw new RestApiException(CommonErrorCode.FILE_DELETE_FAIL);
		}
	}
	public static void deleteAllDirectory(String path) {
		try (Stream<Path> walk = Files.walk(Paths.get(path))) {
			walk.sorted(Comparator.reverseOrder())
				.forEach(p -> {
					try {
						Files.delete(p);
					} catch (IOException e) {
						log.error(e.toString());
						throw new RestApiException(CommonErrorCode.FILE_DELETE_FAIL);
					}
				});
		} catch (IOException e) {
			log.error(e.toString());
			throw new RestApiException(CommonErrorCode.FILE_DELETE_FAIL);
		}
	}

	public static List<FileInfoDTO> uploadFiles(String path, List<MultipartFile> files) {
		List<FileInfoDTO> fileList = null;
		try {
			Path targetPath = Paths.get(path);
			// 파일 업로드
			for (MultipartFile file : files) {
				// 각 파일의 원본 이름에서 공백을 언더스코어로 대체
				String fileName = file.getOriginalFilename().replace(" ", "_");
				// 지정된 경로에 파일을 복사
				Files.copy(file.getInputStream(), targetPath.resolve(fileName));
				fileList.add(FileInfoDTO.builder()
					.fileName(fileName)
					.size(String.valueOf(file.getSize()))
					.build());
			}
			return fileList;
		} catch (IOException e) {
			// 파일 업로드 실패시 예외 처리
			throw new RestApiException(CommonErrorCode.FILE_UPLOAD_FAIL);
		}
	}
}
