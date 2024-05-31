package com.xiilab.servercore.workload.dto.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

import org.springframework.util.ObjectUtils;

import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.common.dto.AgeDTO;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
public class WorkloadSummaryDTO {
	private String uid;                          // 워크로드 고유 ID
	private String name;                         // 사용자가 입력한 워크로드의 이름
	private String resourceName;                 // 워크로드 실제 이름
	private String creatorId;                    // 생성자 ID
	private String creatorUserName;              // 생성자 username(unique)
	private String creatorFullName;              // 생성자 fullName(unique)
	private String workspaceName;                // 워크스페이스 이름
	private String workspaceResourceName;        // 워크스페이스 리소스 이름
	private WorkloadType type;                   // 워크로드 타입
	private LocalDateTime createdAt;             // 워크로드 생성일시
	private WorkloadStatus status;               // 워크로드 status
	private boolean isPinYN;                     // PIN YN
	private AgeDTO age;                          // 워크로드 경과시간
	private int remainTime;                      // 잔여시간
	private ImageType imageType;
	private boolean canBeDeleted;
	private Map<String, String> parameter;
	private String estimatedRemainingTime;
	@Setter
	private String startTime;    // 파드 실행시간

	public WorkloadSummaryDTO(WorkloadEntity workload) {
		this.uid = workload.getUid();
		this.name = workload.getName();
		this.resourceName = workload.getResourceName();
		this.creatorId = workload.getCreatorId();
		this.creatorUserName = workload.getCreatorName();
		this.creatorFullName = workload.getCreatorRealName();
		this.workspaceName = workload.getWorkspaceName();
		this.workspaceResourceName = workload.getWorkspaceResourceName();
		this.type = workload.getWorkloadType();
		this.createdAt = workload.getCreatedAt();
		this.status = workload.getWorkloadStatus();
		this.age = workload.getCreatedAt() != null ? new AgeDTO(workload.getCreatedAt()) : null;
		this.remainTime = workload.getRemainTime();
		this.imageType = !ObjectUtils.isEmpty(workload.getImage()) ? workload.getImage().getImageType() : null;
		this.canBeDeleted = workload.isCanBeDeleted();
		this.estimatedRemainingTime = workload.getRemainTime() != 0 ? LocalDateTime.now()
			.plusSeconds(workload.getRemainTime())
			.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
		this.startTime = workload.getStartTime() != null ?
			workload.getStartTime().format((DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))) : null;
	}

	public void updateCanBeDeleted(String creator, Set<String> ownerWorkspace) {
		if (this.creatorId.equals(creator) || ownerWorkspace.contains(this.workspaceResourceName)) {
			this.canBeDeleted = true;
		}
	}

	public void updatePinYN(boolean isPinYN) {
		this.isPinYN = isPinYN;
	}
}
