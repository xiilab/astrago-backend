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

	public static String getUserFolderPath(String username) {
		return System.getProperty("user.home") + File.separator + ROOT_PATH + File.separator + username;
	}

	private static Path createFolders(Path path) throws IOException {
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

	public static void copyFile(Path sourcePath, Path targetPath) throws IOException {
		Files.copy(sourcePath, targetPath.resolve(sourcePath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
	}

	public static void copyDirectory(Path sourcePath, Path targetPath) throws IOException {
		Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				Path targetDir = targetPath.resolve(sourcePath.relativize(dir));
				if (Files.notExists(targetDir)) {
					Files.createDirectories(targetDir);
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.copy(file, targetPath.resolve(sourcePath.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	public static void deleteDirectory(String path){
		try{
			Path targetPath = Paths.get(path);
			Files.delete(targetPath);
		}catch (IOException e){
			throw new RestApiException(CommonErrorCode.FILE_DELETE_FAIL);
		}
	}
}
