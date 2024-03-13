package com.xiilab.modulek8sdb.version.entity;

import java.awt.*;
import java.util.Objects;

import com.xiilab.modulek8sdb.common.entity.BaseEntity;
import com.xiilab.modulek8sdb.version.enums.FrameWorkType;

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
import lombok.Setter;

@Entity
@Table(name = "TB_FRAMEWORK_VERSION")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FrameWorkVersionEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "FRAMEWORK_VERSION_ID")
	private Long frameworkVersionId;
	@Column(name = "FRAMEWORK_VERSION")
	private String frameworkVersion;
	@Column(name = "CUDA_VERSION")
	private String cudaVersion;
	@Column(name = "FRAMEWORK_TYPE")
	@Enumerated(EnumType.STRING)
	private FrameWorkType frameworkType;

	@Builder
	public FrameWorkVersionEntity( String frameworkVersion, String cudaVersion,
		FrameWorkType frameworkType) {
		this.frameworkVersion = frameworkVersion;
		this.cudaVersion = cudaVersion;
		this.frameworkType = frameworkType;
	}
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof FrameWorkVersionEntity))
			return false;
		FrameWorkVersionEntity other = (FrameWorkVersionEntity)o;
		return this.frameworkVersionId == other.getFrameworkVersionId() &&
			Objects.equals(frameworkVersion, other.getFrameworkVersion());
	}
	@Override
	public int hashCode() {
		return Objects.hash(frameworkVersionId, frameworkVersion);
	}
}
