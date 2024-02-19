package com.xiilab.servercore.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.modulecommon.enums.FileType;
import com.xiilab.modulecommon.dto.DirectoryDTO;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CoreFileUtils {
	static final long kilobyte = 1024;
	static final long megabyte = kilobyte * 1024;
	static final long gigabyte = megabyte * 1024;
	public static String getFileName(String filePath) {
		File file = new File(filePath);
		if (file.isFile()) {
			return file.getName();
		} else {
			throw new RestApiException(CommonErrorCode.FILE_NOT_FOUND);
		}
	}

	public static MediaType getMediaTypeForFileName(String fileName) {
		String[] parts = fileName.split("\\.");
		String fileExtension = parts[parts.length - 1].toLowerCase();

		switch (fileExtension) {
			case "txt":
				return MediaType.TEXT_PLAIN;
			case "jpg":
			case "jpeg":
				return MediaType.IMAGE_JPEG;
			case "png":
				return MediaType.IMAGE_PNG;
			case "pdf":
				return MediaType.APPLICATION_PDF;
			case "zip":
				return MediaType.parseMediaType("application/zip");
			// 추가적인 파일 형식에 대한 처리를 여기에 추가할 수 있습니다.
			default:
				return MediaType.APPLICATION_OCTET_STREAM;
		}
	}

	public static void deleteFileOrDirectory(String path) {
		File fileOrDirectory = new File(path);
		if (fileOrDirectory.isFile()) {
			fileOrDirectory.delete(); // 파일이면 삭제
		} else {
			// 디렉토리면 하위 파일 및 디렉토리를 모두 삭제
			File[] files = fileOrDirectory.listFiles();
			if (files != null) {
				for (File file : files) {
					deleteFileOrDirectory(file.getPath());
				}
			}
			// 마지막으로 빈 디렉토리 삭제
			fileOrDirectory.delete();
		}
	}
	public static long datasetUploadFiles(String path, List<MultipartFile> files) {
		long size = 0;
		for (MultipartFile file : files) {
			String filePath = path + File.separator + file.getOriginalFilename();
			File saveFile = new File(filePath);
			try {
				file.transferTo(saveFile);
				size += file.getSize();
			} catch (IOException e) {
				throw new RestApiException(CommonErrorCode.FILE_SAVE_FAIL);
			}
		}
		return size;
	}
	public static DirectoryDTO getAstragoFiles(String path) {
		List<DirectoryDTO.ChildrenDTO> children = new ArrayList<>();
		File directory = new File(path);
		int directoryCnt = 0;
		int fileCnt = 0;
		// 디렉토리 또는 파일이 존재하는지 확인
		if (directory.exists()) {
			// 디렉토리 내의 파일 및 디렉토리 목록 조회
			File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					String fullPath = file.getParent() + File.separator + file.getName();
					if (file.isDirectory()) {
						DirectoryDTO.ChildrenDTO dirChild = null;
						try {
							dirChild = DirectoryDTO.ChildrenDTO.builder()
								.name(file.getName())
								.type(FileType.D)
								.path(fullPath)
								.size(CoreFileUtils.formatFileSize(file.length()))
								.fileCount(Files.list(Path.of(fullPath)).count() + " FILES")
								.build();
						} catch (IOException e) {
							throw new RestApiException(CommonErrorCode.FILE_INFO_LOOKUP_FAIL);
						}
						directoryCnt += 1;
						children.add(dirChild);
					} else if (file.isFile()) {
						DirectoryDTO.ChildrenDTO fileChild = DirectoryDTO.ChildrenDTO.builder()
							.name(file.getName())
							.type(FileType.F)
							.path(fullPath)
							.size(CoreFileUtils.formatFileSize(file.length()))
							.fileCount(null)
							.build();
						fileCnt += 1;
						children.add(fileChild);
					}
				}
			}
		}
		return DirectoryDTO.builder().children(children).directoryCnt(directoryCnt).fileCnt(fileCnt).build();
	}

	public static String getFileExtension(Path filePath) {
		String fileName = filePath.getFileName().toString();
		int dotIndex = fileName.lastIndexOf('.');

		if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
			return fileName.substring(dotIndex + 1);
		} else {
			return null; // 확장자가 없는 경우
		}
	}

	public static byte[] zipDirectory(Path directoryPath) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
			Files.walk(directoryPath)
				.filter(path -> !Files.isDirectory(path))
				.forEach(path -> {
					try {
						String entryName = directoryPath.relativize(path).toString();
						ZipEntry zipEntry = new ZipEntry(entryName);
						zipOutputStream.putNextEntry(zipEntry);
						Files.copy(path, zipOutputStream);
						zipOutputStream.closeEntry();
					} catch (IOException e) {
						throw new RestApiException(CommonErrorCode.FILE_INFO_LOOKUP_FAIL);
					}
				});
		}
		return byteArrayOutputStream.toByteArray();
	}

	public static String formatFileSize(long bytes) {
		if (bytes >= gigabyte) {
			return String.format("%.2f GB", (double) bytes / gigabyte);
		} else if (bytes >= megabyte) {
			return String.format("%.2f MB", (double) bytes / megabyte);
		} else if (bytes >= kilobyte) {
			return String.format("%.2f KB", (double) bytes / kilobyte);
		} else {
			return bytes + " Bytes";
		}
	}
}
