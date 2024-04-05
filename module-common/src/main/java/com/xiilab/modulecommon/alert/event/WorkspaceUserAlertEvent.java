package com.xiilab.modulecommon.alert.event;

import com.xiilab.modulecommon.alert.enums.AlertName;
import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.vo.PageNaviParam;

public record WorkspaceUserAlertEvent(AlertRole alertRole,
									  AlertName alertName,
									  String sendUserId,
									  String recipientUserId,
									  String mailTitle,
									  String title,
									  String message,
									  String workspaceResourceName,
									  PageNaviParam pageNaviParam){
}
