package com.xiilab.moduleuser.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_USER_HISTORY")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class UserHistoryEntity{
	@Id
	@Column(name = "USER_ID")
	private String userId;
	@Column(name = "GROUP_ID")
	private String groupId;
	@Column(name = "WORKSPACE_CREATE_COUNT")
	private long wsCreateCount;
	@Column(name = "WORKSPACE_FAIL_COUNT")
	private long wsFailCount;
	@Column(name = "WORKLOAD_CREATE_COUNT")
	private long wlCreateCount;
	@Column(name = "WORKLOAD_FAIL_COUNT")
	private long wlFailCount;
	@Column(name = "IMAGE_CREATE_COUNT")
	private long imageCreateCount;
	@Column(name = "DATASET_CREATE_COUNT")
	private long datasetCreateCount;
	@Column(name = "CODE_CREATE_COUNT")
	private long codeCreateCount;
	@Column(name = "CREATE_DATE")
	private LocalDateTime createDate;

	public void updateGroupId(String groupId){
		this.groupId = groupId;
	}
	public void increaseUserWlCount(){
		this.wlCreateCount++;
	}
	public void increaseUserWlFailCount(){
		this.wlFailCount++;
	}
	public void increaseUserWsCount(){
		this.wsCreateCount++;
	}
	public void increaseUserWsFailCount(){
		this.wsFailCount++;
	}
	public void increaseUserImageCount(){
		this.imageCreateCount++;
	}
	public void increaseUserDatasetCount(){
		this.datasetCreateCount++;
	}
	public void increaseUserCodeCount(){
		this.codeCreateCount++;
	}
}
