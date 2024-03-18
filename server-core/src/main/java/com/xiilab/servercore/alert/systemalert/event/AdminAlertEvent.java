package com.xiilab.servercore.alert.systemalert.event;

import com.xiilab.modulek8sdb.alert.systemalert.enumeration.AlertName;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.AlertRole;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.SystemAlertMessage;

public record AdminAlertEvent(AlertRole alertRole, AlertName alertName, SystemAlertMessage systemAlertMessage){
}
