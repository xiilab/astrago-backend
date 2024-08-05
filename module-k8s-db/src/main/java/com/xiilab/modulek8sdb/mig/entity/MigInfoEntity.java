package com.xiilab.modulek8sdb.mig.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_MIG_INFO")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class MigInfoEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MIG_INFO_ID")
	private Long id;
	@ElementCollection
	private List<Integer> gpuIndexes;
	@Column(name = "MIG_ENABLE")
	private boolean migEnable;
	@Builder.Default
	@ElementCollection
	@CollectionTable(name = "TB_MIG_PROFILE", joinColumns = @JoinColumn(name = "MIG_INFO_ID"))
	@MapKeyColumn(name = "PROFILE_TYPE")
	@Column(name = "PROFILE_COUNT")
	private Map<String, Integer> profile = new HashMap<>();
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "NODE_ID")
	private NodeEntity nodeEntity;
}
