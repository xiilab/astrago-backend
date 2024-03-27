package com.xiilab.servercore.code.service;

import static com.xiilab.modulecommon.enums.RepositoryType.*;
import static com.xiilab.modulecommon.util.DataConverterUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.dto.RegexPatterns;
import com.xiilab.modulecommon.enums.CodeType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CodeErrorCode;
import com.xiilab.modulecommon.exception.errorcode.WorkloadErrorCode;
import com.xiilab.modulecommon.util.GithubApi;
import com.xiilab.modulek8sdb.code.entity.CodeEntity;
import com.xiilab.modulek8sdb.code.repository.CodeRepository;
import com.xiilab.modulek8sdb.code.repository.CodeWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.credential.entity.CredentialEntity;
import com.xiilab.servercore.code.dto.CodeReqDTO;
import com.xiilab.servercore.code.dto.CodeResDTO;
import com.xiilab.servercore.credential.service.CredentialService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CodeServiceImpl implements CodeService {
	private final CodeRepository codeRepository;
	private final CredentialService credentialService;
	private final CodeWorkLoadMappingRepository codeWorkLoadMappingRepository;

	@Override
	@Transactional
	public CodeResDTO saveCode(CodeReqDTO codeReqDTO) {
		// 깃허브 또는 깃랩 URL인지 검증
		boolean isGitHubURL = Pattern.matches(RegexPatterns.GITHUB_URL_PATTERN, codeReqDTO.getCodeURL());
		boolean isGitLabURL = Pattern.matches(RegexPatterns.GITLAB_URL_PATTERN, codeReqDTO.getCodeURL());

		// URL 검증
		if (!isGitHubURL && !isGitLabURL) {
			throw new RestApiException(CodeErrorCode.UNSUPPORTED_REPOSITORY_ERROR_CODE);
		}

		// 사용자 Credential 조회
		String token = "";
		CredentialEntity credentialEntity = null;
		if (codeReqDTO.getRepositoryAuthType() == RepositoryAuthType.PRIVATE && codeReqDTO.getCredentialId() != null && codeReqDTO.getCredentialId() != 0) {
			credentialEntity = Optional.ofNullable(credentialService.getCredentialEntity(codeReqDTO.getCredentialId()))
					.orElseThrow(() -> new RestApiException(WorkloadErrorCode.FAILED_LOAD_CODE_CREDENTIAL_INFO));
			token = credentialEntity != null ? credentialEntity.getLoginPw() : "";
		}

		// 연결 가능한지 확인
		CodeType codeType = null;
		if (isGitHubURL) {
			codeType = CodeType.GIT_HUB;
			GithubApi githubApi = new GithubApi(token);
			githubApi.isRepoConnected(getRepoByUrl(codeReqDTO.getCodeURL()));
		} else if (isGitLabURL) {
			codeType = CodeType.GIT_LAB;
			// GITLAB API 검증
		}

		try {
			CodeEntity saveCode = codeRepository.save(
				CodeEntity.dtoConverter()
					.codeType(codeType)
					.codeURL(codeReqDTO.getCodeURL())
					.workspaceResourceName(codeReqDTO.getWorkspaceName())
					.credentialEntity(credentialEntity)
					.repositoryType(WORKSPACE)
					.build());
			return new CodeResDTO(saveCode);
		} catch (IllegalArgumentException e) {
			throw new RestApiException(CodeErrorCode.FAILED_SAVE_USER_CODE);
		}
	}

	@Override
	@Transactional
	public List<CodeResDTO> saveCodes(List<CodeReqDTO> codeReqDTOs) {
		List<CodeResDTO> savedCodes = new ArrayList<>();

		for (CodeReqDTO codeReqDTO : codeReqDTOs) {
			CodeResDTO codeResDTO = saveCode(codeReqDTO);
			savedCodes.add(codeResDTO);
		}

		return savedCodes;
	}

	@Override
	public Boolean isCodeURLValid(String codeURL, Long credentialId) {
		// 깃허브 또는 깃랩 URL인지 검증
		boolean isGitHubURL = Pattern.matches(RegexPatterns.GITHUB_URL_PATTERN, codeURL);
		boolean isGitLabURL = Pattern.matches(RegexPatterns.GITLAB_URL_PATTERN, codeURL);

		// URL 검증
		if (!isGitHubURL && !isGitLabURL) {
			throw new RestApiException(CodeErrorCode.UNSUPPORTED_REPOSITORY_ERROR_CODE);
		}

		String token = "";
		if (credentialId != 0) {
			CredentialEntity credentialEntity = credentialService.getCredentialEntity(credentialId);
			token = credentialEntity != null ? credentialEntity.getLoginPw() : "";
		}

		if (isGitHubURL) {
			GithubApi githubApi = new GithubApi(token);
			if (githubApi.isRepoConnected(getRepoByUrl(codeURL))) {
				return true;
			}
		} else if (isGitLabURL) {
			// GITLAB API 검증
		}

		return true;
	}

	@Override
	public List<CodeResDTO> getCodeList(String workspaceName) {
		List<CodeEntity> codeEntityList = codeRepository.getCodeEntitiesByWorkspaceResourceNameAndRepositoryTypeAndDeleteYnEquals(workspaceName, WORKSPACE, DeleteYN.N);

		return codeEntityList.stream().map(CodeResDTO::new).toList();
	}

	@Override
	public CodeResDTO getCodeById(long id) {
		CodeEntity codeEntity = getCodeEntity(id);
		return new CodeResDTO(codeEntity);
	}

	@Override
	public void deleteCodeById(long id) {
		// 코드 Entity 조회
		getCodeEntity(id);
		// 코드 삭제
		codeRepository.deleteById(id);
	}

	private CodeEntity getCodeEntity(long id) {
		return codeRepository.findById(id).orElseThrow(() -> new RestApiException(CodeErrorCode.CODE_NOT_FOUND));
	}

}
