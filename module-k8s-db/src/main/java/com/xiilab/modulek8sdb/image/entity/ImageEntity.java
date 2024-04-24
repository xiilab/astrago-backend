package com.xiilab.modulek8sdb.image.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;

import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.common.entity.BaseEntity;
import com.xiilab.modulek8sdb.common.entity.RegUser;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulecommon.enums.ImageType;

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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_IMAGE")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "IMAGE_TYPE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE TB_IMAGE ti SET ti.DELETE_YN = 'Y' WHERE ti.IMAGE_ID = ?")
@Getter
public abstract class ImageEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "IMAGE_ID")
	private Long id;

	@Column(name = "IMAGE_NAME_HUB")
	private String imageNameHub;

	@Column(name = "IMAGE_NAME_HARBOR")
	private String imageNameHarbor;

	@Column(name = "REPOSITORY_AUTH_TYPE")
	@Enumerated(EnumType.STRING)
	private RepositoryAuthType repositoryAuthType;

	@Enumerated(EnumType.STRING)
	@Column(name = "IMAGE_TYPE", insertable = false, updatable = false)
	private ImageType imageType;

	@Enumerated(EnumType.STRING)
	@Column(name = "WORKLOAD_TYPE")
	private WorkloadType workloadType;

	@Column(name = "DELETE_YN")
	@Enumerated(EnumType.STRING)
	private DeleteYN deleteYN = DeleteYN.N;

	protected ImageEntity(String imageNameHub, RepositoryAuthType repositoryAuthType, ImageType imageType,
		WorkloadType workloadType) {
		this.imageNameHub = imageNameHub;
		this.repositoryAuthType = repositoryAuthType;
		this.imageType = imageType;
		this.workloadType = workloadType;
	}

	protected ImageEntity(RegUser regUser, String imageNameHub, RepositoryAuthType repositoryAuthType, ImageType imageType,
		WorkloadType workloadType) {
		super.regDate = LocalDateTime.now();
		super.regUser = regUser;
		this.imageNameHub = imageNameHub;
		this.repositoryAuthType = repositoryAuthType;
		this.imageType = imageType;
		this.workloadType = workloadType;
	}

	public abstract boolean isBuiltInImage();
	public abstract boolean isCustomImage();
	public abstract boolean isHubImage();
}
