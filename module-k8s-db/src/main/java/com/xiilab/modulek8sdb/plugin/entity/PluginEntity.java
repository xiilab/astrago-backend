package com.xiilab.modulek8sdb.plugin.entity;

import java.time.LocalDateTime;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.plugin.dto.PluginDTO;

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
	@Column(name = "DELL_USER_NAME")
	private String dellUserName;
	@Column(name = "DELL_PASSWORD")
	private String dellPassword;
	@Column(name = "DELL_ENDPOINT")
	private String dellEndpoint;
	@Column(name = "INSTALL_YN")
	@Enumerated(EnumType.STRING)
	private DeleteYN installYN;
	@Column(name = "REG_USER_ID")
	private String regUserId;
	@Column(name = "REG_USER_NAME")
	private String regUserName;
	@Column(name = "REG_DATE")
	private LocalDateTime regDate;
	public void setInstallYN(DeleteYN installYN, PluginDTO pluginDTO) {
		this.installYN = installYN;
		if(DeleteYN.Y.equals(installYN)) {
			this.regDate = LocalDateTime.now();
		}else {
			this.regDate = null;
		}
		this.regUserId = pluginDTO.getRegUserId();
		this.regUserName = pluginDTO.getRegUserName();
		this.regDate = LocalDateTime.now();
		this.dellUserName = pluginDTO.getDellUserName();
		this.dellPassword = pluginDTO.getDellPassword();
		this.dellEndpoint = pluginDTO.getDellEndpoint();
	}
}