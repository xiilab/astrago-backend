package com.xiilab.modulek8sdb.model.entity;


import com.xiilab.modulecommon.enums.StorageType;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_LOCAL_MODEL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("LOCAL")
@Getter
public class LocalModelEntity extends Model {
	@Column(name = "STORAGE_IP")
	private String ip;
	@Column(name = "STORAGE_TYPE")
	@Enumerated(EnumType.STRING)
	private StorageType storageType;

	@Column(name = "STORAGE_PATH")
	private String storagePath;

	@Column(name = "DNS")
	private String dns;
	@Column(name = "DEPLOYMENT_NAME")
	private String deploymentName;
	@Column(name = "SVC_NAME")
	private String svcName;
	@Column(name = "PV_NAME")
	private String pvName;
	@Column(name = "PVC_NAME")
	private String pvcName;

	@Builder
	public LocalModelEntity(Long modelId, String modelName,String ip, StorageType storageType, String storagePath, String dns, String deploymentName
		, String svcName, String pvName, String pvcName, String defaultPath) {
		super(modelId, modelName, defaultPath);
		this.ip = ip;
		this.storageType = storageType;
		this.storagePath = storagePath;
		this.dns = dns;
		this.deploymentName = deploymentName;
		this.svcName = svcName;
		this.pvcName = pvcName;
		this.pvName = pvName;
	}

	@Override
	public boolean isAstragoModel() {
		return false;
	}

	@Override
	public boolean isLocalModel() {
		return true;
	}
}
