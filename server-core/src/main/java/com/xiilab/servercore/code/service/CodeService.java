package com.xiilab.servercore.code.service;

import java.util.List;

import com.xiilab.servercore.code.dto.CodeReqDTO;
import com.xiilab.servercore.code.dto.CodeResDTO;

public interface CodeService {
	CodeResDTO saveCode(CodeReqDTO codeReqDTO);
	List<CodeResDTO> getCodeList(String workspaceName);
	CodeResDTO getCodeById(long id);
	void deleteCodeById(long id);
}
