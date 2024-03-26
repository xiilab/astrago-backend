package com.xiilab.modulecommon.alert.event;

import com.xiilab.modulecommon.alert.enums.AlertName;
import com.xiilab.modulecommon.alert.enums.AlertRole;

public record WorkspaceUserAlertEvent(AlertRole alertRole,
									  AlertName alertName,
									  String mailTitle,
									  String title,
									  String message,
									  String workspaceResourceName){
}
