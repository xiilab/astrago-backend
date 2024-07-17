package com.xiilab.modulek8sdb.storage.entity;

import org.hibernate.annotations.SQLDelete;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8sdb.common.entity.BaseEntity;
import com.xiilab.modulek8sdb.common.enums.DefaultStorageYN;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_STORAGE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE TB_STORAGE ts SET ts.DELETE_YN = 'Y' WHERE ts.STORAGE_ID = ?")
@Getter
public class StorageEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "STORAGE_ID")
	private Long storageId;
	@Column(name = "STORAGE_NAME")
	private String storageName;
	@Column(name = "STORAGE_REQUEST_VOLUME")
	private int requestVolume;
	@Column(name = "DESCRIPTION")
	private String description;
	@Column(name = "STORAGE_TYPE")
	@Enumerated(EnumType.STRING)
	private StorageType storageType;
	@Column(name = "STORAGE_IP")
	private String ip;
	@Column(name = "STORAGE_PATH")
	private String storagePath;
	@Column(name = "HOST_PATH")
	private String hostPath;
	@Column(name = "ASTRAGO_DEPLOYMENT_NAME")
	private String astragoDeploymentName;
	@Column(name = "NAME_SPACE")
	private String namespace;
	@Column(name = "VOLUME_NAME")
	private String volumeName;
	@Column(name = "PV_NAME")
	private String pvName;
	@Column(name = "PVC_NAME")
	private String pvcName;
	@Column(name = "DELETE_YN")
	@Enumerated(EnumType.STRING)
	private DeleteYN deleteYN = DeleteYN.N;
	@Column(name = "SECRET_NAME")
	private String secretName;

	@Enumerated(EnumType.STRING)
	@Column(name = "DEFAULT_STORAGE_YN")
	private DefaultStorageYN defaultStorageYN = DefaultStorageYN.N;
	@Builder
	public StorageEntity(String storageName, int requestVolume, String description, StorageType storageType, String ip,
		String storagePath, String hostPath, String astragoDeploymentName, String namespace, String pvName,
		String pvcName, String volumeName, String secretName, DefaultStorageYN defaultStorageYN) {
		this.storageName = storageName;
		this.requestVolume = requestVolume;
		this.description = description;
		this.storageType = storageType;
		this.ip = ip;
		this.storagePath = storagePath;
		this.hostPath = hostPath;
		this.astragoDeploymentName = astragoDeploymentName;
		this.namespace = namespace;
		this.volumeName = volumeName;
		this.pvName = pvName;
		this.pvcName = pvcName;
		this.secretName = secretName;
		this.defaultStorageYN = defaultStorageYN;
	}

	public void changeStorageName(String storageName) {
		this.storageName = storageName;
	}
}
