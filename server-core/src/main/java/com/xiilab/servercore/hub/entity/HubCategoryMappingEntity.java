package com.xiilab.servercore.hub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity(name = "TB_HUB_CATEGORY_MAPPING")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class HubCategoryMappingEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HUB_CATEGORY_MAPPING_ID")
	private Long hubCategoryMappingId;

	@ManyToOne
	private HubEntity hubEntity;

	@ManyToOne
	private HubCategoryEntity hubCategoryEntity;
}
