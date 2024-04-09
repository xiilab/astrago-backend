package com.xiilab.servercore.code.service;

import java.util.List;

import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.code.dto.CodeReqDTO;
import com.xiilab.servercore.code.dto.CodeResDTO;
import com.xiilab.servercore.code.dto.ModifyCodeReqDTO;

public interface CodeService {
	CodeResDTO saveCode(CodeReqDTO codeReqDTO);
	List<CodeResDTO> saveCodes(List<CodeReqDTO> codeReqDTO);
	Boolean isCodeURLValid(String codeURL, Long credentialId);
	List<CodeResDTO> getCodeList(String workspaceName, UserInfoDTO userInfoDTO);
	CodeResDTO getCodeById(long id);
	void deleteCodeById(long id);

	void deleteCodeWorkloadMapping(Long jobId);

	void modifyCode(Long codeId, ModifyCodeReqDTO modifyCodeReqDTO);
}
