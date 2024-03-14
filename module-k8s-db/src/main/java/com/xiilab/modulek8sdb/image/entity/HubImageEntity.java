package com.xiilab.modulek8sdb.image.entity;

import java.util.ArrayList;
import java.util.List;

import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.hub.entity.HubEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "TB_HUB_IMAGE")
@DiscriminatorValue("HUB")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HubImageEntity extends ImageEntity {
	@OneToMany(mappedBy = "hubImageEntity", fetch = FetchType.LAZY)
	private List<HubEntity> hubEntities = new ArrayList<>();

	@Builder
	public HubImageEntity(String imageName, RepositoryAuthType repositoryAuthType,
		ImageType imageType, WorkloadType workloadType) {
		super(imageName, repositoryAuthType, imageType, workloadType);
	}

	@Override
	public boolean isBuiltInImage() {
		return false;
	}

	@Override
	public boolean isCustomImage() {
		return false;
	}

	@Override
	public boolean isHubImage() {
		return true;
	}
}
