package com.xiilab.modulek8s.common.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.xiilab.modulecommon.dto.RegexPatterns;
import com.xiilab.modulecommon.enums.CodeType;
import com.xiilab.modulecommon.enums.GitEnvType;
import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulecommon.enums.WorkloadType;

import io.fabric8.kubernetes.api.model.EnvVar;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class K8SResourceMetadataDTO {
	private String uid;
	private String workloadName;
	private String workloadResourceName;
	private String workspaceName;
	private String workspaceResourceName;
	private String description;
	private WorkloadType workloadType;
	private ImageType imageType;
	private Long imageId;
	private String imageName;
	private Long imageCredentialId;
	private LocalDateTime createdAt;
	private LocalDateTime deletedAt;
	private String creatorId;
	private String creatorUserName;
	private String creatorFullName;
	private Float cpuReq;
	private Integer gpuReq;
	private Float memReq;
	private String datasetIds;
	private String modelIds;
	private String codeIds;
	private List<K8SResourceMetadataDTO.Env> envs;          // env 정의
	private List<K8SResourceMetadataDTO.Port> ports;        // port 정의
	private List<K8SResourceMetadataDTO.Code> codes;        // port 정의
	private Map<Long, String> datasetMountPathMap;    // dataset - mount path 맵
	private Map<Long, String> modelMountPathMap; 		// model - mount path 맵
	private Map<String, Map<String, String>> codeMountPathMap; 		// model - mount path 맵
	private String workingDir;
	private String command;                      // 워크로드 명령
	private Map<String,String> parameter;
	private String ide;

	@Getter
	@AllArgsConstructor
	public static class Env {
		private String name;
		private String value;
	}

	@Getter
	@AllArgsConstructor
	public static class Port {
		private String name;
		private Integer port;
	}

	@Getter
	@AllArgsConstructor
	public static class Code {
		private Long sourceCodeId;	// 소스코드 아이디, 필수값 X
		private String repositoryUrl;
		private String branch;
		private CodeType codeType;
		private String mountPath;
		private RepositoryAuthType repositoryAuthType = RepositoryAuthType.PUBLIC;
		private RepositoryType repositoryType = RepositoryType.USER;
		private Long credentialId;
		private String credentialUserName;
		private String credentialPassword;

		public Code(List<EnvVar> envs) {
			for (EnvVar envVar : envs) {
				String envVarName = envVar.getName();
				GitEnvType gitEnvType = GitEnvType.valueOf(envVarName);

				switch (gitEnvType) {
					case GIT_SYNC_REPO:
						this.repositoryUrl = envVar.getValue();
						if (Pattern.matches(RegexPatterns.GITHUB_URL_PATTERN, envVar.getValue())) {
							this.codeType = CodeType.GIT_HUB;
						} else {
							this.codeType = CodeType.GIT_LAB;
						}
						break;
					case GIT_SYNC_BRANCH:
						this.branch = envVar.getValue();
						break;
					case GIT_SYNC_ROOT:
						this.mountPath = envVar.getValue().substring(0, envVar.getValue().lastIndexOf("/"));
						break;
					case SOURCE_CODE_ID:	// 공유 코드 아니면 환경변수 없음
						this.sourceCodeId = Long.valueOf(envVar.getValue());
						this.repositoryType = RepositoryType.WORKSPACE;
						break;
					case CREDENTIAL_ID: // private repository 아니면 환경변수 없음
						this.credentialId = Long.valueOf(envVar.getValue());
						this.repositoryAuthType = RepositoryAuthType.PRIVATE;
						break;
					case GIT_SYNC_USERNAME:
						this.credentialUserName = envVar.getValue();
						break;
					case GIT_SYNC_PASSWORD:
						this.credentialPassword = envVar.getValue();
						break;
					default:
						break;
				}
			}
		}
	}
}
