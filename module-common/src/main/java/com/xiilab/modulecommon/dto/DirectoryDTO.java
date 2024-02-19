package com.xiilab.modulecommon.dto;

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
		children = rawStringList.stream()
			.map(rawString -> ChildrenDTO.convertRawString().rawString(rawString).build())
			.toList();
		directoryCnt = (int)children.stream().filter(childrenDTO -> childrenDTO.type == FileType.D).count();
		fileCnt = (int)children.stream().filter(childrenDTO -> childrenDTO.type == FileType.F).count();
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
			if (rawString.contains("Syntax error") || rawString.contains("stat: cannot statx"))
				throw new IllegalArgumentException("경로를 다시 확인해주세요");
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
}
