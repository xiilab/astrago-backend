package com.xiilab.servercore.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.dto.DirectoryDTO;
import com.xiilab.modulecommon.enums.FileType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class CoreFileUtils {
	private static final long KILOBYTE = 1024;
	private static final long MEGABYTE = KILOBYTE * 1024;
	private static final long GIGABYTE = MEGABYTE * 1024;

	public static String getFileName(String filePath) {
		File file = new File(filePath);
		if (file.isFile()) {
			return file.getName();
		} else {
			throw new RestApiException(CommonErrorCode.FILE_NOT_FOUND);
		}
	}

	public static String splitFileName(String filePath) {
		String[] split = filePath.split("/");
		return split[split.length - 1];
	}

	public static MediaType getMediaTypeForFileName(String fileName) {
		String[] parts = fileName.split("\\.");
		String fileExtension = parts[parts.length - 1].toLowerCase();

		return switch (fileExtension) {
			case "txt" -> MediaType.TEXT_PLAIN;
			case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
			case "png" -> MediaType.IMAGE_PNG;
			case "pdf" -> MediaType.APPLICATION_PDF;
			case "zip" -> MediaType.parseMediaType("application/zip");
			// 추가적인 파일 형식에 대한 처리를 여기에 추가할 수 있습니다.
			default -> MediaType.APPLICATION_OCTET_STREAM;
		};
	}

	public static void deleteFileOrDirectory(String path) {
		File fileOrDirectory = new File(path);
		String target = fileOrDirectory.getName();
		try {
			if (fileOrDirectory.exists()) { // 파일 또는 디렉토리가 존재하는지 확인
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
		} catch (SecurityException e) {
			log.error("file delete exception : ", e);
			throw new RestApiException(CommonErrorCode.FILE_PERMISSION_DENIED, target);
		}
	}

	public static long datasetUploadFiles(String path, List<MultipartFile> files) {
		long size = 0;
		for (MultipartFile file : files) {
			String filePath = path + File.separator + file.getOriginalFilename().replace(" ", "_");
			File saveFile = new File(filePath);
			try {
				file.transferTo(saveFile);
				size += file.getSize();
			} catch (IOException e) {
				log.error("io exception : " + e);
				throw new RestApiException(CommonErrorCode.FILE_SAVE_FAIL);
			}
		}
		return size;
	}

	public static Long saveInputStreamToFile(String path, String fileName, InputStream inputStream) throws IOException {
		// 파일명
		String filePath = path + File.separator + fileName.replace(" ", "_");
		File file = new File(filePath);

		// 파일 저장
		FileUtils.copyInputStreamToFile(inputStream, file);

		return file.length();
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
		if (bytes >= GIGABYTE) {
			return String.format("%.2f GB", (double)bytes / GIGABYTE);
		} else if (bytes >= MEGABYTE) {
			return String.format("%.2f MB", (double)bytes / MEGABYTE);
		} else if (bytes >= KILOBYTE) {
			return String.format("%.2f KB", (double)bytes / KILOBYTE);
		} else {
			return bytes + " Bytes";
		}
	}

	public static File convertInputStreamToFile(MultipartFile file) throws IOException {
		String spacePath = getSpacePath(file.getOriginalFilename());
		File tempFile = new File(spacePath);
		boolean newFile = tempFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(tempFile);
		fos.write(file.getBytes());
		fos.close();
		return tempFile;
	}

	public static String getSpacePath(String path) {
		String[] split = path.split("/");
		String fileName = split[split.length - 1];
		if (fileName.contains(" ")) {
			split[split.length - 1] = fileName.replaceAll(" ", "_");
		}
		return String.join("/", split);
	}
}
