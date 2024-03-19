package com.xiilab.modulecommon.alert.event;

import com.xiilab.modulecommon.alert.enums.AlertName;

public record AdminAlertEvent(AlertName alertName,
							  String senderId,
							  String senderUserName,
							  String senderUserRealName,
							  String mailTitle,
							  String title,
							  String message){
}
