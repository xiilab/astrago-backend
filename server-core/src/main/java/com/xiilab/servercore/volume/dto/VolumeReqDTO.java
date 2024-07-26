package com.xiilab.servercore.volume.dto;

import java.util.List;

import com.xiilab.modulecommon.enums.CompressFileType;
import com.xiilab.modulecommon.enums.VolumeAccessType;

import lombok.Getter;

@Getter
public class VolumeReqDTO {

	@Getter
	public static class FilePath {
		private String path;
	}

	@Getter
	public static class FilePaths {
		private String[] paths;
	}

	@Getter
	public static class Compress {
		private List<String> filePaths;
		private CompressFileType compressFileType;
	}

	@Getter
	public abstract static class Edit {
		protected String volumeName;
		protected String defaultPath;
		protected VolumeAccessType volumeAccessType;

		@Getter
		public static class CreateAstragoVolume extends Edit {
			private Long storageId;
		}

		@Getter
		public static class CreateLocalVolume extends Edit {
			private String ip;
			private String storagePath;
		}

		@Getter
		public static class ModifyVolume extends Edit {
		}
	}
}
