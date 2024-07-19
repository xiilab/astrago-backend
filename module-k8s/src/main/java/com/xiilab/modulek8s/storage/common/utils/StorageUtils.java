package com.xiilab.modulek8s.storage.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.xiilab.modulecommon.exception.RestApiException;
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
}
