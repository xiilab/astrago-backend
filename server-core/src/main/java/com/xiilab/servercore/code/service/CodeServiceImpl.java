package com.xiilab.servercore.code.service;

import static com.xiilab.modulecommon.util.DataConverterUtil.*;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CodeErrorCode;
import com.xiilab.modulecommon.util.GithubApi;
import com.xiilab.modulek8sdb.code.dto.CodeReqDTO;
import com.xiilab.modulek8sdb.code.dto.CodeResDTO;
import com.xiilab.modulek8sdb.code.entity.CodeEntity;
import com.xiilab.modulek8sdb.code.repository.CodeRepository;
import com.xiilab.modulek8sdb.credential.entity.CredentialEntity;
import com.xiilab.servercore.credential.service.CredentialService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CodeServiceImpl implements CodeService {
	private final CodeRepository codeRepository;
	private final CredentialService credentialService;

	@Override
	@Transactional
	public CodeResDTO saveCode(CodeReqDTO codeReqDTO) {
		// 사용자 Credential 조회
		CredentialEntity credentialEntity = null;
		CodeEntity saveCode = null;
		try {
			// 사용자 Git 조회
			GithubApi githubApi;
			if (codeReqDTO.getCredentialId() != 0) {
				// Credential을 사용하는 경우
				credentialEntity = credentialService.getCredentialEntity(codeReqDTO.getCredentialId());
				githubApi = new GithubApi(credentialEntity.getLoginPw());
			} else {
				// Credential을 사용하지 않는 경우
				githubApi = new GithubApi("");
			}
			// 사용자 URL 체크
			boolean urlCheck = !githubApi.getBranchList(getRepoByUrl(codeReqDTO.getCodeURL())).isEmpty();

			if (urlCheck) {
				saveCode = codeRepository.save(
					CodeEntity.dtoConverter().codeReqDTO(codeReqDTO).credentialEntity(credentialEntity).build());
			}
		} catch (RuntimeException e) {
			throw new RestApiException(CodeErrorCode.CODE_INVALID_TOKEN_OR_URL);
		}
		return new CodeResDTO(saveCode);
	}

	@Override
	public List<CodeResDTO> getCodeList(String workspaceName) {
		List<CodeEntity> codeEntityList = codeRepository.getAlertEntitiesByWorkspaceResourceName(workspaceName);
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
