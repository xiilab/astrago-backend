package com.xiilab.modulecommon.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 해당 클래스는 AstraGo에서 관리하는 파일에 관련된 utils입니다.
 */
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class FileUtils {
	private static final String ROOT_PATH = "/astrago";

	private static String getUserFolderPath(String username) {
		return System.getProperty("user.home") + File.separator + ROOT_PATH + File.separator + username;
	}

	public static String getUserLogFolderPath(String username) {
		return getUserFolderPath(username) + File.separator + "log";
	}

	private static Path createFolders(Path path) throws IOException {
		return Files.createDirectories(path);
	}

	public static void saveLogFile(String logContent, String jobName, String username) throws IOException {
		Path folderPath = createFolders(Path.of(getUserLogFolderPath(username)));
		Path filePath = Path.of(folderPath + File.separator + jobName + ".log");
		Files.writeString(filePath, logContent, StandardOpenOption.CREATE);
	}
}
