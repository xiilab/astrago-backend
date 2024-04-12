package com.xiilab.servercore.code.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.code.dto.CodeReqDTO;
import com.xiilab.servercore.code.dto.CodeResDTO;
import com.xiilab.servercore.code.dto.ModifyCodeReqDTO;

public interface CodeService {
	CodeResDTO saveCode(CodeReqDTO codeReqDTO, UserInfoDTO userInfoDTO);

	Boolean isCodeURLValid(String codeURL, Long credentialId);

	Page<CodeResDTO> getCodeList(String workspaceName, UserInfoDTO userInfoDTO, Pageable pageable);

	CodeResDTO getCodeById(long id);

	void deleteCodeById(long id);

	void deleteCodeWorkloadMapping(Long jobId);

	void modifyCode(Long codeId, ModifyCodeReqDTO modifyCodeReqDTO);
}
