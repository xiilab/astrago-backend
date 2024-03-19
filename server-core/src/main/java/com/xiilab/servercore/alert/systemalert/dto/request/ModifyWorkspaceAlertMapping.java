package com.xiilab.servercore.alert.systemalert.dto.request;

import com.xiilab.modulecommon.enums.AlertSendType;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.AlertStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModifyWorkspaceAlertMapping {
	private AlertSendType alertSendType;
	private AlertStatus alertStatus;
}
