package com.xiilab.servercore.pin.entity;

import com.xiilab.servercore.common.entity.BaseEntity;
import com.xiilab.servercore.pin.enumeration.PinType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
	@Column(name = "PIN_RESOURCE_ID")
	private String resourceId;

	public PinEntity(PinType type, String resourceId) {
		this.type = type;
		this.resourceId = resourceId;
	}
}
