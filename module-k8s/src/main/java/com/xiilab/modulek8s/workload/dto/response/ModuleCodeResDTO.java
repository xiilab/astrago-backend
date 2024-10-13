package com.xiilab.modulek8s.workload.dto.response;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.dto.RegexPatterns;
import com.xiilab.modulecommon.enums.CodeType;
import com.xiilab.modulecommon.enums.GitEnvType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.RepositoryType;

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import lombok.Getter;

@Getter
public class ModuleCodeResDTO {
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
	private String command;

	public ModuleCodeResDTO(List<? extends KubernetesResource> resources) {
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

	private void convertFromEnvVars(List<? extends KubernetesResource> envs) {
		for (KubernetesResource envVar : envs) {
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
					if (Pattern.matches(RegexPatterns.GITHUB_URL_PATTERN, envVarValue)) {
						this.codeType = CodeType.GIT_HUB;
					} else if(Pattern.matches(RegexPatterns.GITLAB_URL_PATTERN, envVarValue)){
						this.codeType = CodeType.GIT_LAB;
					} else if (Pattern.matches(RegexPatterns.BITBUCKET_URL_PATTERN, envVarValue)) {
						this.codeType = CodeType.BIT_BUCKET;
					}
					break;
				case GIT_SYNC_BRANCH:
					this.branch = envVarValue;
					break;
				case GIT_SYNC_MOUNT_PATH:
					this.mountPath = envVarValue;
					break;
				case SOURCE_CODE_ID:	// 공유 코드 아니면 SOURCE_CODE_ID 환경변수 없음
					this.sourceCodeId = Long.valueOf(envVarValue);
					// this.repositoryType = RepositoryType.WORKSPACE;
					break;
				case CREDENTIAL_ID: // private repository 아니면 CREDENTIAL_ID 환경변수 없음
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
				case COMMAND:
					this.command = envVarValue;
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
