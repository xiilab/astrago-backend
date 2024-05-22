package com.xiilab.modulecommon.alert.event;

import com.xiilab.modulecommon.alert.enums.AlertName;
import com.xiilab.modulecommon.dto.MailDTO;

public record UserAlertEvent(AlertName alertName,
							 String mailTitle,
							 String title,
							 String message,
							 String userId,
							 MailDTO mailDTO) {
}
