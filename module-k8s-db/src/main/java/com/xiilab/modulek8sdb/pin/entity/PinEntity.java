package com.xiilab.modulek8sdb.pin.entity;

import com.xiilab.modulek8sdb.common.entity.BaseEntity;
import com.xiilab.modulek8sdb.common.entity.RegUser;
import com.xiilab.modulek8sdb.pin.enumeration.PinType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_PIN")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PinEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PIN_ID")
	private Long id;
	@Enumerated(value = EnumType.STRING)
	@Column(name = "PIN_TYPE")
	private PinType type;
	@Column(name = "PIN_RESOURCE_NAME")
	private String resourceName;

	public PinEntity(PinType type, String resourceName) {
		this.type = type;
		this.resourceName = resourceName;
	}

	@Builder
	public PinEntity(PinType type, String resourceName, String regUserId, String regUserName, String regUserRealName) {
		this.type = type;
		this.resourceName = resourceName;
		super.regUser = new RegUser(regUserId, regUserName, regUserRealName);
	}
}
