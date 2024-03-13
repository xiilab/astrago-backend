package com.xiilab.modulek8sdb.version.entity;

import java.time.LocalDateTime;

import com.xiilab.modulek8sdb.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_COMPATIBLE_FRAMEWORK_VERSION")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CompatibleFrameworkVersionEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "COMPATIBLE_FRAMEWORK_VERSION_ID")
	private Long compatibleFrameworkVersionId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FRAMEWORK_VERSION_ID")
	private FrameWorkVersionEntity frameWorkVersionEntity;

	@Column(name = "REG_DATE")
	private LocalDateTime regDate;
	@Column(name = "MOD_DATE")
	private LocalDateTime modeDate;

	@Builder
	public CompatibleFrameworkVersionEntity(FrameWorkVersionEntity frameWorkVersionEntity) {
		this.frameWorkVersionEntity = frameWorkVersionEntity;
		this.regDate = LocalDateTime.now();
		this.modeDate = LocalDateTime.now();
	}
}
