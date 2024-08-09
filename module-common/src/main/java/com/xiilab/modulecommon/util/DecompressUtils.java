package com.xiilab.modulecommon.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.util.ObjectUtils;

import com.xiilab.modulecommon.enums.CompressFileType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.UtilsErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DecompressUtils {

	/**
	 * 지정된 압축 형식으로 파일이나 디렉토리 목록을 압축 해제해 지정된 대상 경로에 저장
	 *
	 * @param targetPath      압축 해제할 압축 파일
	 * @param destinationPath 압축된 파일을 저장할 대상 경로 (null 입력시, 압축하려는 파일이 위치한 경로에 압축파일 저장)
	 */
	public static void saveDecompressFile(Path targetPath, Path destinationPath) {
		// 압축해제할 파일 없으면 throw
		if (ObjectUtils.isEmpty(targetPath)) {
			throw new RestApiException(UtilsErrorCode.NO_SELECTED_DECOMPRESS_FILE);
		}

		String extension = FileNameUtils.getExtension(targetPath);
		destinationPath = initializeDestPath(targetPath, destinationPath);

		if (CompressFileType.ZIP.getExtension().equals(extension)) {
			try (InputStream is = new BufferedInputStream(new FileInputStream(targetPath.toFile()));
				 ZipArchiveInputStream zais = new ArchiveStreamFactory().createArchiveInputStream(
					 CompressFileType.ZIP.getExtension(), is)) {
				decompressDirectoriesAndFiles(destinationPath, zais);
			} catch (IOException | ArchiveException e) {
				throw new RestApiException(UtilsErrorCode.FAILED_DECOMPRESS_ZIP_FILE);
			}
		} else if (CompressFileType.TAR.getExtension().equals(extension)) {
			try (InputStream is = new BufferedInputStream(new FileInputStream(targetPath.toFile()));
				 TarArchiveInputStream tais = new ArchiveStreamFactory().createArchiveInputStream(
					 CompressFileType.TAR.getExtension(), is)) {
				decompressDirectoriesAndFiles(destinationPath, tais);
			} catch (IOException | ArchiveException e) {
				throw new RestApiException(UtilsErrorCode.FAILED_DECOMPRESS_TAR_FILE);
			}
		} else {
			throw new RestApiException(UtilsErrorCode.UNKNOWN_DECOMPRESS_FILE_FORMAT);
		}
	}

	/**
	 * 압축파일 저장할 대상 경로 반환
	 *
	 * @param targetPath      압축 해제할 파일 또는 디렉토리 항목
	 * @param destinationPath
	 * @return
	 */
	private static Path initializeDestPath(Path targetPath, Path destinationPath) {
		// destinationPath 없으면, 압축할 파일과 동일한 경로 반환
		destinationPath = ObjectUtils.isEmpty(destinationPath) ?
			Path.of(targetPath.getParent().toString()) :
			destinationPath;

		// 상위 디렉토리 생성
		String targetFileName = FileNameUtils.getBaseName(targetPath);
		Path parentDirPath = Path.of(targetPath.getParent() + File.separator + targetFileName);

		int cnt = 1;
		while (parentDirPath.toFile().exists()) {
			parentDirPath = parentDirPath.resolveSibling(targetFileName + "_" + cnt);
			cnt++;
		}

		parentDirPath.toFile().mkdirs();
		return parentDirPath;
	}

	/**
	 * 압축해제할 파일 또는 폴더 outputStream에 추가
	 *
	 * @param destinationPath 저장될 경로
	 * @param ais             압축파일 inputstream
	 */
	private static <T extends ArchiveInputStream> void decompressDirectoriesAndFiles(Path destinationPath, T ais) {
		try {
			ArchiveEntry entry = ais.getNextEntry();
			do {
				if (ValidUtils.isCheckExtensionForMac(entry.getName())) {
					entry = ais.getNextEntry();
					continue;
				}

				createSaveDir(destinationPath.toFile().getPath(), entry.getName());
				File saveFile = new File(destinationPath.toFile(), entry.getName());

				if(!entry.isDirectory()){
					try (OutputStream os = new BufferedOutputStream(new FileOutputStream(saveFile))) {
						IOUtils.copy(ais, os);
					}
				}

				entry = ais.getNextEntry();
			} while (entry != null);
		} catch (IOException e) {
			throw new RestApiException(UtilsErrorCode.FAILED_DECOMPRESS_FILE);
		}
	}

	private static void createSaveDir(String destinationFilePath, String entryName) {
		File file = Path.of(destinationFilePath + File.separator + entryName).getParent().toFile();
		if (!file.exists()) {
			file.mkdirs();
		}
	}
}
