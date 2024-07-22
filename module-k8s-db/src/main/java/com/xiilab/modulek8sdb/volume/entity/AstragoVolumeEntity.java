package com.xiilab.modulek8sdb.volume.entity;

import com.xiilab.modulek8sdb.storage.entity.StorageEntity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_ASTRAGO_VOLUME")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("ASTRAGO")
@Getter
public class AstragoVolumeEntity extends Volume {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STORAGE_ID")
	private StorageEntity storageEntity;

	@Column(name = "VOLUME_PATH")
	private String volumePath;
	@Column(name = "SAVE_DIRECTORY_NAME")
	private String saveDirectoryName;

	@Builder
	public AstragoVolumeEntity(Long volumeId, String volumeName, StorageEntity storageEntity, String defaultPath) {
		super(volumeId, volumeName, defaultPath);
		this.storageEntity = storageEntity;
	}

	public void setVolumePath(String datasetPath){
		this.volumePath = datasetPath;
	}

	public void setSaveDirectoryName(String saveDirectoryName){
		this.saveDirectoryName = saveDirectoryName;
	}
	@Override
	public boolean isAstragoVolume() {
		return true;
	}

	@Override
	public boolean isLocalVolume() {
		return false;
	}
}
