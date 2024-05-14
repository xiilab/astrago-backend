package com.xiilab.modulecommon.alert.event;

import com.xiilab.modulecommon.alert.enums.AlertName;
import com.xiilab.modulecommon.vo.PageNaviParam;

public record AlertManagerEvent(AlertName alertName,
								String sendUserId,
								String mailTitle,
								String title,
								String message,
								PageNaviParam pageNaviParam){
}
