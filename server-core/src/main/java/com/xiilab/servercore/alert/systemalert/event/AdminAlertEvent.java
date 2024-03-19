package com.xiilab.servercore.alert.systemalert.event;

import com.xiilab.modulek8sdb.alert.systemalert.enumeration.AlertName;

public record AdminAlertEvent(AlertName alertName,
							  String senderId,
							  String senderUserName,
							  String senderUserRealName,
							  String mailTitle,
							  String title,
							  String message){
}
