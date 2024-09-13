package com.xiilab.servercore.oneview.service;

import com.xiilab.modulecommon.enums.OneViewAccountConnectionStatus;
import com.xiilab.servercore.oneview.dto.OneViewReqDTO;
import com.xiilab.servercore.oneview.dto.OneViewResDTO;

public interface OneViewService {
	OneViewResDTO.FindOneViewSetting getOneViewSetting();
	void saveOneViewSetting(OneViewReqDTO.SaveOneViewSetting saveOneViewSettingDTO);
	OneViewAccountConnectionStatus getOneViewAccountConnectionStatus();
	String getOneViewSessionToken();
}
