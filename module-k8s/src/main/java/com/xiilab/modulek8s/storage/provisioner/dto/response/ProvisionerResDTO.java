package com.xiilab.modulek8s.storage.provisioner.dto.response;

import com.xiilab.modulek8s.common.enumeration.ProvisionerStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ProvisionerResDTO {
	private String provisionerMetaName;
	private String provisionerName;
	private int storageCnt;
	private ProvisionerStatus status;

	@Builder
	public ProvisionerResDTO(String provisionerMetaName, String provisionerName,
		ProvisionerStatus status, int storageCnt) {
		this.provisionerMetaName = provisionerMetaName;
		this.provisionerName = provisionerName;
		this.status = status;
		this.storageCnt = storageCnt;
	}
}
