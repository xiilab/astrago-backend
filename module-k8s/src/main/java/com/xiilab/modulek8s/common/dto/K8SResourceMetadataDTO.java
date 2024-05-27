package com.xiilab.modulek8s.common.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.containers.Ports;
import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.dto.RegexPatterns;
import com.xiilab.modulecommon.enums.CodeType;
import com.xiilab.modulecommon.enums.GitEnvType;
import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulecommon.enums.WorkloadType;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.KubernetesResource;
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

		public Env(Object object) {
			if (object instanceof EnvVar env) {
				this.name = env.getName();
				this.value = env.getValue();
			} else if (object instanceof org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.initcontainers.Env env) {
				this.name = env.getName();
				this.value = env.getValue();
			} else if (object instanceof org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.containers.Env env) {
				this.name = env.getName();
				this.value = env.getValue();
			}
		}
	}

	@Getter
	@AllArgsConstructor
	public static class Port {
		private String name;
		private Integer port;

		public Port(Object object) {
			if (object instanceof Port portInstance) {
				this.name = portInstance.getName();
				this.port = portInstance.getPort();
			} else if (object instanceof Ports portInstance) {
				this.name = portInstance.getName();
				this.port = portInstance.getContainerPort();
			} else if (object instanceof org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.initcontainers.Ports portInstance) {
				this.name = portInstance.getName();
				this.port = portInstance.getContainerPort();
			} else {
				this.name = ((ContainerPort)object).getName();
				this.port = ((ContainerPort)object).getContainerPort();
			}
		}
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
		private RepositoryType repositoryType;
		private Long credentialId;
		private String credentialUserName;
		private String credentialPassword;

		public Code(List<? extends KubernetesResource> resources) {
			if (!CollectionUtils.isEmpty(resources)) {
				KubernetesResource kubernetesResource = resources.get(0);
				if (kubernetesResource instanceof EnvVar) {
					convertFromEnvVars(resources);
				} else if (kubernetesResource instanceof org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.containers.Env) {
					convertFromEnvVars(resources);
				} else if (kubernetesResource instanceof org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.initcontainers.Env) {
					convertFromEnvVars(resources);
				}
			}
		}

		private void convertFromEnvVars(List<?> envVars) {
			for (Object envVar : envVars) {
				Map<String, String> stringStringMap = convertEnvString(envVar);

				if (CollectionUtils.isEmpty(stringStringMap)) {
					continue;
				}

				String envVarName = stringStringMap.get("key");
				String envVarValue = stringStringMap.get("value");

				GitEnvType gitEnvType = GitEnvType.valueOf(envVarName);
				switch (gitEnvType) {
					case GIT_SYNC_REPO:
						this.repositoryUrl = envVarValue;
						this.codeType =
							Pattern.matches(RegexPatterns.GITHUB_URL_PATTERN, envVarValue) ? CodeType.GIT_HUB :
								CodeType.GIT_LAB;
						break;
					case GIT_SYNC_BRANCH:
						this.branch = envVarValue;
						break;
					case GIT_SYNC_ROOT:
						this.mountPath = envVarValue.substring(0, envVarValue.lastIndexOf("/"));
						break;
					case SOURCE_CODE_ID:    // 공유 코드 아니면 환경변수 없음
						this.sourceCodeId = Long.valueOf(envVarValue);
						// this.repositoryType = RepositoryType.WORKSPACE;
						break;
					case CREDENTIAL_ID: // private repository 아니면 환경변수 없음
						this.credentialId = Long.valueOf(envVarValue);
						this.repositoryAuthType = RepositoryAuthType.PRIVATE;
						break;
					case GIT_SYNC_USERNAME:
						this.credentialUserName = envVarValue;
						break;
					case GIT_SYNC_PASSWORD:
						this.credentialPassword = envVarValue;
						break;
					case REPOSITORY_TYPE:
						this.repositoryType = RepositoryType.valueOf(envVarValue);
						break;
					default:
						break;
				}
			}
		}

		private Map<String, String> convertEnvString(Object object) {
			if (object instanceof EnvVar env) {
				return Map.of("key", env.getName(), "value", env.getValue() == null ? "" : env.getValue());
			} else if (object instanceof org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.containers.Env env) {
				return Map.of("key", env.getName(), "value", env.getValue() == null ? "" : env.getValue());
			} else if (object instanceof org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.initcontainers.Env env) {
				return Map.of("key", env.getName(), "value", env.getValue() == null ? "" : env.getValue());
			} else {
				return Map.of();
			}
		}
	}
}
