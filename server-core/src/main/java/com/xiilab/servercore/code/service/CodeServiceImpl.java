package com.xiilab.servercore.code.service;

import static com.xiilab.modulecommon.enums.RepositoryType.*;
import static com.xiilab.modulecommon.util.DataConverterUtil.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.dto.RegexPatterns;
import com.xiilab.modulecommon.enums.CodeType;
import com.xiilab.modulecommon.enums.PageMode;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CodeErrorCode;
import com.xiilab.modulecommon.util.GitLabApi;
import com.xiilab.modulek8sdb.code.dto.CodeSearchCondition;
import com.xiilab.modulek8sdb.code.entity.CodeEntity;
import com.xiilab.modulek8sdb.code.repository.CodeCustomRepository;
import com.xiilab.modulek8sdb.code.repository.CodeRepository;
import com.xiilab.modulek8sdb.code.repository.CodeWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.credential.entity.CredentialEntity;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.code.dto.CodeReqDTO;
import com.xiilab.servercore.code.dto.CodeResDTO;
import com.xiilab.servercore.code.dto.ModifyCodeReqDTO;
import com.xiilab.servercore.common.utils.BitBucketApi;
import com.xiilab.servercore.common.utils.GithubApi;
import com.xiilab.servercore.credential.service.CredentialService;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CodeServiceImpl implements CodeService {
	@Value("${gitlab.token}")
	private String gitlabToken;
	private final CodeRepository codeRepository;
	private final CodeCustomRepository codeCustomRepository;
	private final CredentialService credentialService;
	private final CodeWorkLoadMappingRepository codeWorkLoadMappingRepository;

	public static String getBaseUrl(String url) {
		int endIndex = url.indexOf("/", "http://".length());
		if (endIndex == -1) {
			return url; // 슬래시가 없는 경우는 그대로 반환
		} else {
			return url.substring(0, endIndex);
		}
	}

	@Override
	@Transactional
	public CodeResDTO saveCode(CodeReqDTO codeReqDTO, UserDTO.UserInfo userInfoDTO) {
		// 깃허브 또는 깃랩 URL인지 검증
		// boolean isGitHubURL = Pattern.matches(RegexPatterns.GITHUB_URL_PATTERN, codeReqDTO.getCodeURL());
		// boolean isGitLabURL = Pattern.matches(RegexPatterns.GITLAB_URL_PATTERN, codeReqDTO.getCodeURL());

		//repositoryType에 따른 code 존재여부 체크
		checkCodeExist(codeReqDTO, userInfoDTO);

		// URL 검증
		// if (!isGitHubURL) {
		// 	throw new RestApiException(CodeErrorCode.UNSUPPORTED_REPOSITORY_ERROR_CODE);
		// }

		// 사용자 Credential 조회
		// repoType이 private 일때만 조회
		CredentialEntity credentialEntity = null;
		if (codeReqDTO.getCredentialId() != null && codeReqDTO.getRepositoryAuthType() == RepositoryAuthType.PRIVATE) {
			credentialEntity = credentialService.getCredentialEntity(codeReqDTO.getCredentialId());
		}

		isCodeURLValid(codeReqDTO.getCodeURL(),
			codeReqDTO.getRepositoryAuthType() == RepositoryAuthType.PRIVATE ? codeReqDTO.getCredentialId() : null,
			codeReqDTO.getCodeType());
		try {
			CodeEntity saveCode = codeRepository.save(
				CodeEntity.dtoConverter()
					.codeType(codeReqDTO.getCodeType())
					.codeURL(codeReqDTO.getCodeURL())
					.workspaceResourceName(codeReqDTO.getWorkspaceName())
					.credentialEntity(credentialEntity)
					.repositoryType(codeReqDTO.getRepositoryType())
					.codeDefaultMountPath(codeReqDTO.getDefaultPath())
					.cmd(codeReqDTO.getCmd())
					.codeArgs(codeReqDTO.getCodeArgs())
					.build());
			return new CodeResDTO(saveCode);
		} catch (IllegalArgumentException e) {
			throw new RestApiException(CodeErrorCode.FAILED_SAVE_USER_CODE);
		}
	}

	@Override
	public Page<CodeResDTO> getCodeList(String workspaceName, UserDTO.UserInfo userInfoDTO, Pageable pageable,
		CodeSearchCondition codeSearchCondition, PageMode pageMode) {
		Page<CodeEntity> codeEntityList;
		if (isAdminPage(pageMode)) {
			codeEntityList = getAdminCodeList(workspaceName, pageable, codeSearchCondition);
		} else {
			codeEntityList = getNonAdminCodeList(workspaceName, userInfoDTO, pageable, codeSearchCondition);
		}
		return codeEntityList.map(CodeResDTO::new);
	}

	@Override
	public CodeResDTO getCodeById(long id) {
		CodeEntity codeEntity = getCodeEntity(id);
		return new CodeResDTO(codeEntity);
	}

	@Override
	public void deleteCodeById(long id) {
		// 코드 Entity 조회
		CodeEntity codeEntity = getCodeEntity(id);
		// 코드 삭제
		codeRepository.deleteById(codeEntity.getId());
	}

	@Override
	public void deleteCodeWorkloadMapping(Long jobId) {
		codeWorkLoadMappingRepository.deleteByWorkloadId(jobId);
	}

	@Override
	public Boolean isCodeURLValid(String codeURL, Long credentialId, CodeType codeType) {
		// 깃허브 또는 깃랩 URL인지 검증
		boolean isGitHubURL = Pattern.matches(RegexPatterns.GITHUB_URL_PATTERN, codeURL);
		boolean isBitBucketURL = Pattern.matches(RegexPatterns.BITBUCKET_URL_PATTERN, codeURL);

		String token = "";
		String userName = null;
		if (credentialId != null) {
			CredentialEntity credentialEntity = credentialService.getCredentialEntity(credentialId);
			userName = credentialEntity != null? credentialEntity.getLoginId() : "";
			token = credentialEntity != null ? credentialEntity.getLoginPw() : "";
		}

		if (isGitHubURL && codeType == CodeType.GIT_HUB) {
			GithubApi githubApi = new GithubApi(token);
			String repoName = convertGitHubRepoUrlToRepoName(codeURL);
			String[] split = repoName.split("/");
			if (githubApi.isRepoConnected(split[0], split[1])) {
				return true;
			}
		} else if (isBitBucketURL && codeType == CodeType.BIT_BUCKET) {
			BitBucketApi bitBucketApi = new BitBucketApi(codeURL, userName, token);
			return bitBucketApi.isRepoConnected();
		} else {
			// GITLAB API 검증
			String gitlabUrl = getBaseUrl(codeURL);
			Pattern pattern = Pattern.compile(gitlabUrl + "/(.*?)/([^/.]+)(\\.git){1}");
			Matcher matcher = pattern.matcher(codeURL);
			if (matcher.find()) {
				GitLabApi gitLabApi = new GitLabApi(gitlabUrl, token);
				String namespace = matcher.group(1);
				String project = matcher.group(2);
				return gitLabApi.isRepoConnected(namespace, project);
			}
		}

		return false;
	}

	private CodeEntity getCodeEntity(long id) {
		return codeRepository.findById(id).orElseThrow(() -> new RestApiException(CodeErrorCode.CODE_NOT_FOUND));
	}

	private void checkCodeExist(CodeReqDTO codeReqDTO, UserDTO.UserInfo userInfoDTO) {
		RepositoryType repositoryType = codeReqDTO.getRepositoryType();
		if (repositoryType == USER) {
			List<CodeEntity> codeEntities = codeRepository.findByCodeURLAndRepositoryTypeAndRegUser_RegUserIdAndDeleteYn(
				codeReqDTO.getCodeURL(),
				USER,
				userInfoDTO.getId(),
				DeleteYN.N
			);
			if (!codeEntities.isEmpty()) {
				throw new RestApiException(CodeErrorCode.CODE_EXIST_ERROR);
			}
		} else if (repositoryType == WORKSPACE) {
			if (StringUtils.isEmpty(codeReqDTO.getWorkspaceName())) {
				throw new RestApiException(CodeErrorCode.CODE_INPUT_ERROR);
			}
			List<CodeEntity> codeEntities = codeRepository.findByWorkspaceResourceNameAndCodeURLAndRepositoryTypeAndDeleteYn(
				codeReqDTO.getWorkspaceName(),
				codeReqDTO.getCodeURL(),
				WORKSPACE,
				DeleteYN.N
			);
			if (!codeEntities.isEmpty()) {
				throw new RestApiException(CodeErrorCode.CODE_EXIST_ERROR);
			}
		}
	}

	private boolean isAdminPage(PageMode pageMode) {
		return pageMode == PageMode.ADMIN;
	}

	private Page<CodeEntity> getAdminCodeList(String workspaceName, Pageable pageable,
		CodeSearchCondition codeSearchCondition) {
		return codeCustomRepository.getCodeListByCondition(
			null,
			workspaceName,
			StringUtils.isEmpty(workspaceName) ? USER : WORKSPACE,
			pageable,
			codeSearchCondition
		);
	}

	private Page<CodeEntity> getNonAdminCodeList(String workspaceName, UserDTO.UserInfo userInfoDTO,
		Pageable pageable, CodeSearchCondition codeSearchCondition) {
		if (StringUtils.isEmpty(workspaceName)) {
			return codeCustomRepository.getCodeListByCondition(
				userInfoDTO.getId(),
				null,
				USER,
				pageable,
				codeSearchCondition);
		} else {
			return codeCustomRepository.getCodeListByCondition(
				null,
				workspaceName,
				WORKSPACE,
				pageable,
				codeSearchCondition);
		}
	}

	@Override
	@Transactional
	public void modifyCode(Long codeId, ModifyCodeReqDTO modifyCodeReqDTO) {
		CodeEntity codeEntity = getCodeEntity(codeId);

		CredentialEntity credentialEntity = null;
		if (modifyCodeReqDTO.getCredentialId() != null) {
			credentialEntity = credentialService.getCredentialEntity(modifyCodeReqDTO.getCredentialId());
		}

		//해당 소스코드 URL 검증
		isCodeURLValid(codeEntity.getCodeURL(), modifyCodeReqDTO.getRepositoryAuthType() == RepositoryAuthType.PRIVATE ?
			modifyCodeReqDTO.getCredentialId() : null, codeEntity.getCodeType());

		codeEntity.updateCodeInfo(
			modifyCodeReqDTO.getDefaultPath(),
			modifyCodeReqDTO.getCmd(),
			modifyCodeReqDTO.getCodeArgs(),
			credentialEntity);
	}
}
