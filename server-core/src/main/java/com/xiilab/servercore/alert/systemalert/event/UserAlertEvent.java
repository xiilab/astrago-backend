package com.xiilab.servercore.alert.systemalert.event;

import com.xiilab.modulek8sdb.alert.systemalert.enumeration.AlertName;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.AlertRole;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.SystemAlertMessage;

public record UserAlertEvent(AlertRole alertRole,
							 AlertName alertName,
							 String mailTitle,
							 String title,
							 String message,
							 String workspaceResourceName){
}
