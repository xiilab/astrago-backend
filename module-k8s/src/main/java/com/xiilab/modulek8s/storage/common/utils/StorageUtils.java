package com.xiilab.modulek8s.storage.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.NodeErrorCode;
import com.xiilab.modulecommon.exception.errorcode.StorageErrorCode;

public abstract class StorageUtils {

	protected String runShellCommand(String command) throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder();

		if (command.contains("git")) {
			processBuilder.command("git", "-C", ".", "clone", "-b", "v1.5.1", command);
		} else if (command.contains("install.sh")) {
			processBuilder.command("bash", command);
		} else {
			processBuilder.command("sh", "-c", command);
		}

		Process process = processBuilder.start();
		int exitCode = process.waitFor();

		StringBuilder output = new StringBuilder();

		if (exitCode == 0) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					output.append(line).append(System.lineSeparator());
				}
			}
		} else {
			throw new RestApiException(StorageErrorCode.STORAGE_AFTER_AGAIN_INSTALL_IBM);
		}
		return output.toString();
	}

	public static String runShellCommand(String[] command) {
		StringBuilder output = new StringBuilder();
		try {
			// ProcessBuilder로 커맨드 실행
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			processBuilder.redirectErrorStream(true);  // 표준 에러와 표준 출력을 통합
			Process process = processBuilder.start();

			// 명령 실행 결과 읽기
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line).append("\n");
			}

			process.waitFor();  // 프로세스 종료 대기
		} catch (Exception e) {
			e.printStackTrace();
			return "Error executing curl command: " + e.getMessage();
		}

		return getVolumId(output.toString());
	}


	private static String getVolumId(String str){
		// 정규 표현식을 사용하여 "id" 값을 추출합니다.
		Pattern pattern = Pattern.compile("\"id\":\"(fs_\\d+)\"");
		Matcher matcher = pattern.matcher(str);

		if (matcher.find()) {
			String id = matcher.group(1); // 첫 번째 그룹을 가져옵니다.
			return id;
		} else {
			throw new RestApiException(StorageErrorCode.DELL_STORAGE_VOLUME_NOT_FOUND);
		}
	}


}

