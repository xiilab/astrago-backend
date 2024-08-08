package com.xiilab.modulek8sdb.volume.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;

import com.xiilab.modulecommon.enums.OutputVolumeYN;
import com.xiilab.modulek8sdb.common.entity.BaseEntity;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.common.enums.RepositoryDivision;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "TB_VOLUME")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DIVISION")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE TB_VOLUME tv SET tv.DELETE_YN = 'Y' WHERE tv.VOLUME_ID = ?")
public abstract class Volume extends BaseEntity {
	@Column(name = "DIVISION", insertable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	protected RepositoryDivision division;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "VOLUME_ID")
	private Long volumeId;
	@Column(name = "VOLUME_NAME")
	private String volumeName;
	@Column(name = "VOLUME_SIZE")
	private Long volumeSize;
	@Column(name = "DELETE_YN")
	@Enumerated(EnumType.STRING)
	private DeleteYN deleteYn = DeleteYN.N;
	@Column(name = "VOLUME_DEFAULT_MOUNT_PATH")
	private String volumeDefaultMountPath;

	@Column(name = "OUTPUT_VOLUME_YN")
	@Enumerated(EnumType.STRING)
	private OutputVolumeYN outputVolumeYN;

	@OneToMany(mappedBy = "volume")
	private List<VolumeWorkSpaceMappingEntity> volumeWorkSpaceMappingList = new ArrayList<>();
	@OneToMany(mappedBy = "volume")
	private List<VolumeWorkLoadMappingEntity> volumelWorkLoadMappingList = new ArrayList<>();
	@OneToMany(mappedBy = "volume")
	private List<VolumeLabelMappingEntity> volumeLabelMappingList = new ArrayList<>();

	@Transient
	private boolean isAvailable = false;

	public Volume(Long volumeId, String volumeName, String volumeDefaultMountPath, OutputVolumeYN outputVolumeYN) {
		this.volumeId = volumeId;
		this.volumeName = volumeName;
		this.volumeDefaultMountPath = volumeDefaultMountPath;
		this.outputVolumeYN = outputVolumeYN;
	}

	public boolean isAvailable() {
		return !this.getVolumeWorkSpaceMappingList().isEmpty();
	}

	public void setVolumeSize(long size) {
		this.volumeSize = size;
	}

	public void modifyVolumeName(String volumeName) {
		this.volumeName = volumeName;
	}

	public void modifyVolumeDefaultPath(String volumeDefaultMountPath) {
		this.volumeDefaultMountPath = volumeDefaultMountPath;
	}

	public abstract boolean isAstragoVolume();

	public abstract boolean isLocalVolume();
}
