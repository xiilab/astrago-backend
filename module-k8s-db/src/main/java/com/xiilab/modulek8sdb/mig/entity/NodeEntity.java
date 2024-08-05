package com.xiilab.modulek8sdb.mig.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_NODE")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class NodeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "NODE_ID")
	private Long id;
	@Column(name = "NODE_NAME", unique = true)
	private String nodeName;
	@Builder.Default
	@OneToMany(mappedBy = "nodeEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<MigInfoEntity> migInfos = new ArrayList<>();

	public void addMigInfo(List<MigInfoEntity> migInfoEntity) {
		this.migInfos.addAll(migInfoEntity);
	}

	public void updateMigInfo(List<MigInfoEntity> migInfoEntities) {

		this.migInfos.forEach(migInfoEntity -> {
			migInfoEntity.getProfile().clear();
			migInfoEntity.getGpuIndexes().clear();
		});

		this.migInfos.clear();
		this.migInfos.addAll(migInfoEntities);
	}
}
