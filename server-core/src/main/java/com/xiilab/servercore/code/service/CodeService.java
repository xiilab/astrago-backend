package com.xiilab.servercore.code.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xiilab.modulecommon.enums.CodeType;
import com.xiilab.modulecommon.enums.PageMode;
import com.xiilab.modulek8sdb.code.dto.CodeSearchCondition;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.code.dto.CodeReqDTO;
import com.xiilab.servercore.code.dto.CodeResDTO;
import com.xiilab.servercore.code.dto.ModifyCodeReqDTO;

public interface CodeService {
	CodeResDTO saveCode(CodeReqDTO codeReqDTO, UserDTO.UserInfo userInfoDTO);

	Boolean isCodeURLValid(String codeURL, Long credentialId, CodeType codeType);

	Page<CodeResDTO> getCodeList(String workspaceName, UserDTO.UserInfo userInfoDTO, Pageable pageable,
		CodeSearchCondition codeSearchCondition, PageMode pageMode);

	CodeResDTO getCodeById(long id);

	void deleteCodeById(long id);

	void deleteCodeWorkloadMapping(Long jobId);

	void modifyCode(Long codeId, ModifyCodeReqDTO modifyCodeReqDTO);
}
