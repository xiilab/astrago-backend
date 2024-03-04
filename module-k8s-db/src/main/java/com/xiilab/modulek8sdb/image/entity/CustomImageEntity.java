package com.xiilab.modulek8sdb.image.entity;

import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.credential.entity.CredentialEntity;

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
@Table(name = "TB_CUSTOM_IMAGE")
@DiscriminatorValue("CUSTOM")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomImageEntity extends ImageEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CREDENTIAL_ID")
	private CredentialEntity credentialEntity;

	@Builder
	public CustomImageEntity(String imageName, RepositoryAuthType repositoryAuthType,
		ImageType imageType, WorkloadType workloadType, CredentialEntity credentialEntity) {
		super(imageName, repositoryAuthType, imageType, workloadType);
		this.credentialEntity = credentialEntity;
	}

	public CustomImageEntity(CredentialEntity credentialEntity) {
		this.credentialEntity = credentialEntity;
	}

	@Override
	public boolean isBuiltInImage() {
		return false;
	}

	@Override
	public boolean isCustomImage() {
		return true;
	}

	@Override
	public boolean isHubImage() {
		return false;
	}
}
