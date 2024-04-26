package com.xiilab.modulecommon.util;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.xiilab.modulecommon.enums.CompressFileType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.UtilsErrorCode;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipOutputStream;

@Slf4j
public class CompressUtils {

	/**
	 * 지정된 압축 형식을 사용하여 파일이나 디렉토리 목록을 압축하여 지정된 대상 경로에 저장
	 *
	 * @param targetPaths      압축할 파일 또는 디렉토리 목록
	 * @param destinationPath  압축된 파일을 저장할 대상 경로
	 * @param compressFileType 사용할 압축 형식 (ZIP, TAR)
	 * @return 압축이 성공하면 true, 그렇지 않으면 false를 반환
	 */
	public static boolean compress(List<Path> targetPaths, Path destinationPath, CompressFileType compressFileType) {
		// 압축할 파일 없으면 throw
		if (CollectionUtils.isEmpty(targetPaths)) {
			throw new RestApiException(UtilsErrorCode.NO_SELECTED_COMPRESS_FILE);
		}

		Path destPath = initializeDestPath(targetPaths, destinationPath, compressFileType);

		if (compressFileType == CompressFileType.ZIP) {
			try (ZipArchiveOutputStream zipOut = new ZipArchiveOutputStream(new FileOutputStream(destPath.toFile()))) {
				compressFilesAndDirectories(targetPaths, zipOut);
			} catch (IOException e) {
				throw new RestApiException(UtilsErrorCode.FAILED_COMPRESS_ZIP_FILE);
			}
		} else if (compressFileType == CompressFileType.TAR) {
			try (TarArchiveOutputStream tarOut = new TarArchiveOutputStream(new FileOutputStream(destPath.toFile()))) {
				compressFilesAndDirectories(targetPaths, tarOut);
			} catch (IOException e) {
				throw new RestApiException(UtilsErrorCode.FAILED_COMPRESS_TAR_FILE);
			}
		}

		return true;
	}

	/**
	 * 압축파일 저장할 대상 경로 반환
	 *
	 * @param targetPaths      압축할 파일 또는 디렉토리 목록
	 * @param destinationPath  압축된 파일을 저장할 대상 경로
	 * @param compressFileType 사용할 압축 형식 (예: ZIP, TAR)
	 * @return 압축 파일 저장 경로 + 파일명
	 */
	private static Path initializeDestPath(List<Path> targetPaths, Path destinationPath,
		CompressFileType compressFileType) {
		// 저장할 압축 파일명 반환
		// 단일 파일 압축시 "압축할 파일명", 여러개의 파일 압축시 "archive"로 반환
		String saveCompressFileName = targetPaths.size() == 1 ?
			getSaveFileName(FileNameUtils.getBaseName(targetPaths.get(0)), compressFileType) :
			getSaveFileName("archive", compressFileType);

		// destinationPath 없으면, 압축할 파일명과 동일한 루트경로 반환
		if (ObjectUtils.isEmpty(destinationPath)) {
			destinationPath = Path.of(targetPaths.get(0).getParent().toString() + File.separator + saveCompressFileName);
		}

		return destinationPath;
	}

	/**
	 * 원본 파일 이름과 압축 형식을 기반으로 압축된 파일 이름을 반환
	 *
	 * @param fileName         원본 파일 이름
	 * @param compressFileType 사용할 압축 형식 (예: ZIP, TAR)
	 * @return 압축된 파일 이름
	 */
	private static String getSaveFileName(String fileName, CompressFileType compressFileType) {
		if (compressFileType == CompressFileType.ZIP) {
			fileName += "." + CompressFileType.ZIP.getExtension();
		} else if (compressFileType == CompressFileType.TAR) {
			fileName += "." + CompressFileType.TAR.getExtension();
		} else {
			throw new RestApiException(UtilsErrorCode.NOT_SUPPORT_COMPRESS_TYPE);
		}

		return fileName;
	}

	/**
	 * 압축할 파일 또는 폴더 outputStream에 추가
	 *
	 * @param targetPaths 압축할 파일 목록
	 * @param os outputStream 타입
	 */
	private static <T extends ArchiveOutputStream<?>> void compressFilesAndDirectories(List<Path> targetPaths, T os) {
		for (Path targetPath : targetPaths) {
			if (Files.exists(targetPath)) {
				if (targetPath.toFile().isDirectory()) {
					addFolder("", targetPath, os);
				} else if (targetPath.toFile().isFile()) {
					addFile("", targetPath, os);
				}
			} else {
				throw new RestApiException(UtilsErrorCode.NOT_FOUND_COMPRESS_FILE);
			}
		}
	}

	/**
	 * 폴더 및 폴더의 하위항목 내용을 zipOutputStream에 추가
	 *
	 * @param entryPath       폴더의 부모 경로
	 * @param targetFolderPath 추가할 폴더의 경로
	 * @param os           ZIP 출력 스트림
	 */
	private static <T extends ArchiveOutputStream<?>> void addFolder(String entryPath, Path targetFolderPath, T os) {
		File folder = targetFolderPath.toFile();
		entryPath = entryPath + folder.getName() + File.separator;
		putArchiveEntry(entryPath, targetFolderPath.toFile().length(), os);

		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				addFolder(entryPath, Path.of(file.getAbsolutePath()), os);
			} else if (file.isFile()) {
				addFile(entryPath, Path.of(file.getAbsolutePath()), os);
			}
		}
	}

	/**
	 * 파일을 zipOutputStream에 추가
	 *
	 * @param entryPath 부모 경로
	 * @param targetFilePath   추가할 파일의 경로
	 * @param os     ZIP 출력 스트림
	 */
	private static <T extends ArchiveOutputStream<?>> void addFile(String entryPath, Path targetFilePath, T os) {
		File targetFile = targetFilePath.toFile();
		try (FileInputStream fis = new FileInputStream(targetFile)) {
			if (targetFile.getName().contains(".DS_Store")) {
				return ;
			}

			putArchiveEntry(entryPath + targetFile.getName(), targetFilePath.toFile().length(), os);
			IOUtils.copy(fis, os);
			os.closeArchiveEntry();
		} catch (IOException e) {
			throw new RestApiException(UtilsErrorCode.FAILED_COMPRESS_ADD_FILE);
		}
	}

	private static <T extends ArchiveOutputStream<?>> void putArchiveEntry(String filePath, long fileSize, T os) {
		try {
			if (os instanceof ZipArchiveOutputStream) {
				ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(filePath);
				zipArchiveEntry.setSize(fileSize);
				((ZipArchiveOutputStream) os).putArchiveEntry(zipArchiveEntry);
			} else if (os instanceof TarArchiveOutputStream) {
				TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(filePath);
				tarArchiveEntry.setSize(fileSize);
				((TarArchiveOutputStream) os).putArchiveEntry(tarArchiveEntry);
			}
		} catch (IOException e) {
			throw new RestApiException(UtilsErrorCode.FAILED_COMPRESS_ADD_FILE);
		}
	}
}
