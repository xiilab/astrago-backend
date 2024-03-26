package com.xiilab.modulek8s.workload.dto.response;

import java.util.List;
import java.util.regex.Pattern;

import com.xiilab.modulecommon.dto.RegexPatterns;
import com.xiilab.modulecommon.enums.CodeType;
import com.xiilab.modulecommon.enums.GitEnvType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.RepositoryType;

import io.fabric8.kubernetes.api.model.EnvVar;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ModuleCodeResDTO {
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

	public ModuleCodeResDTO(List<EnvVar> envs) {
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
					this.mountPath = envVar.getValue();
					break;
				case SOURCE_CODE_ID:	// 공유 코드 아니면 SOURCE_CODE_ID 환경변수 없음
					this.sourceCodeId = Long.valueOf(envVar.getValue());
					this.repositoryType = RepositoryType.WORKSPACE;
					break;
				case CREDENTIAL_ID: // private repository 아니면 CREDENTIAL_ID 환경변수 없음
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

	// public static ModuleCodeResDTO of(List<EnvVar> env) {
	// 	for (EnvVar envVar : env) {
	// 		if (envVar.getName().equals("GIT_SYNC_REPO")) {
	// 		}
	// 	}
	// }
}
