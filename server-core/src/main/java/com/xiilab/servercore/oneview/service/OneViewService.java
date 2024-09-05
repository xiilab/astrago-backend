package com.xiilab.servercore.oneview.service;

import com.xiilab.modulecommon.enums.OneViewAccountConnectionStatus;
import com.xiilab.servercore.oneview.dto.OneViewReqDTO;

public interface OneViewService {
	void saveOneViewSetting(OneViewReqDTO.SaveOneViewSetting saveOneViewSettingDTO);
	OneViewAccountConnectionStatus getOneViewAccountConnectionStatus();
	String getOneViewSessionToken();
}
