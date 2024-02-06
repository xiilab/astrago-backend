package com.xiilab.servercore.common.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.servercore.common.enums.FileType;
import com.xiilab.servercore.dataset.dto.DirectoryDTO;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CoreFileUtils {

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
	public static void datasetUploadFiles(String path, List<MultipartFile> files) {
		for (MultipartFile file : files) {
			String filePath = path + File.separator + file.getOriginalFilename();
			File saveFile = new File(filePath);
			try {
				file.transferTo(saveFile);
			} catch (IOException e) {
				throw new RestApiException(CommonErrorCode.FILE_SAVE_FAIL);
			}
		}
	}
	public static DirectoryDTO getFileList(String path) {
		List<DirectoryDTO.ChildrenDTO> children = new ArrayList<>();
		File directory = new File(path);

		// 디렉토리 또는 파일이 존재하는지 확인
		if (directory.exists()) {
			// 디렉토리 내의 파일 및 디렉토리 목록 조회
			File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					String fullPath = file.getParent() + File.separator + file.getName();
					if (file.isDirectory()) {
						DirectoryDTO.ChildrenDTO dirChild = DirectoryDTO.ChildrenDTO.builder()
							.name(file.getName())
							.type(FileType.D)
							.path(fullPath)
							.build();
						children.add(dirChild);
					} else if (file.isFile()) {
						DirectoryDTO.ChildrenDTO fileChild = DirectoryDTO.ChildrenDTO.builder()
							.name(file.getName())
							.type(FileType.F)
							.path(fullPath)
							.build();
						children.add(fileChild);
					}
				}
			}
		}
		return DirectoryDTO.builder().children(children).build();
	}
}
