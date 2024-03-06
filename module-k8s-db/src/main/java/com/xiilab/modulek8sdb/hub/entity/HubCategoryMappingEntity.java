package com.xiilab.modulek8sdb.hub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_HUB_CATEGORY_MAPPING")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class HubCategoryMappingEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HUB_CATEGORY_MAPPING_ID")
	private Long hubCategoryMappingId;

	@ManyToOne
	@JoinColumn(name = "HUB_ID")
	private HubEntity hubEntity;

	@ManyToOne
	@JoinColumn(name = "HUB_CATEGORY_ID")
	private HubCategoryEntity hubCategoryEntity;
}
