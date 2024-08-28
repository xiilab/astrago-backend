package com.xiilab.modulek8sdb.plugin.entity;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;

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

@Entity(name = "TB_PLUGIN")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class PluginEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long pluginId;
	@Column(name = "NAME")
	private String name;
	@Column(name = "VERSION")
	private String version;
	@Column(name = "STORAGE_TYPE")
	@Enumerated(EnumType.STRING)
	private StorageType storageType;
	@Column(name = "INSTALL_YN")
	@Enumerated(EnumType.STRING)
	private DeleteYN installYN;

	public void setInstallYN(DeleteYN installYN) {
		this.installYN = installYN;
	}
}