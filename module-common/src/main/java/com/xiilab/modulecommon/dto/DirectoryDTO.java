package com.xiilab.modulecommon.dto;

import java.util.Collections;
import java.util.List;

import com.xiilab.modulecommon.enums.FileType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectoryDTO {
	private List<ChildrenDTO> children;
	private int directoryCnt;
	private int fileCnt;

	@Builder(builderClassName = "convertRawString", builderMethodName = "convertRawString")
	DirectoryDTO(List<String> rawStringList) {
		String result = rawStringList.get(0);
		if (result.contains("Syntax error") || result.contains("stat: cannot statx")) {
			children = Collections.emptyList();
			directoryCnt = 0;
			fileCnt = 0;
		} else {
			children = rawStringList.stream()
				.map(rawString -> ChildrenDTO.convertRawString().rawString(rawString).build())
				.toList();
			directoryCnt = (int)children.stream().filter(childrenDTO -> childrenDTO.type == FileType.D).count();
			fileCnt = (int)children.stream().filter(childrenDTO -> childrenDTO.type == FileType.F).count();
		}
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ChildrenDTO {
		private String name;
		private FileType type;
		private String path;
		private String size;
		private String fileCount;

		@Builder(builderClassName = "convertRawString", builderMethodName = "convertRawString")
		ChildrenDTO(String rawString) {
			//에러체크
			if (rawString.contains("Syntax error") || rawString.contains("stat: cannot statx")) {
				throw new IllegalArgumentException("해당 경로가 올바르지 않거나, 빈 디렉토리입니다.");
			}
			String[] fileArray = rawString.split(",");
			this.name = getFileName(fileArray[0]);
			this.type = fileArray[1].equals("directory") ? FileType.D : FileType.F;
			this.path = fileArray[0];
			this.size = fileArray[2];
		}

		public String getFileName(String rawPath) {
			String[] split = rawPath.split("/");
			return split[split.length - 1];
		}

		public void updateFileCount(String fileCount) {
			this.fileCount = fileCount;
		}
	}

	@Getter
	@AllArgsConstructor
	public static class FilePreviewInfoDTO {
		private ChildrenDTO fileInfo;
		private byte[] file;
	}
}
