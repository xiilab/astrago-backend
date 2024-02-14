package com.xiilab.moduleuser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserHistoryDTO {
	private String userId;
	private String userName;
	private String groupId;
	private String groupName;
	private long wsCreateCount;
	private long wsFailCount;
	private long wlCreateCount;
	private long wlFailCount;
	private long imageCreateCount;
	private long imageFailCount;
	private long datasetCreateCount;
	private long datasetFailCount;
	private long codeCreateCount;
	private long codeFailCount;
}
