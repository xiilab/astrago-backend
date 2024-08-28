package com.xiilab.modulek8sdb.plugin.dto;

import com.xiilab.modulek8sdb.plugin.entity.PluginEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PluginDTO {
	protected String name;
	protected String version;

	@Getter
	@SuperBuilder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ResponseDTO extends PluginDTO {
		private long id;
		private String installYN;
		@Builder(builderClassName = "toDTOBuilder", builderMethodName = "toDTOBuilder")
		public ResponseDTO(PluginEntity plugin){
			this.id = plugin.getPluginId();
			this.name = plugin.getName();
			this.version = plugin.getVersion();
			this.installYN = plugin.getInstallYN().name();
		}
	}

	@Getter
	@SuperBuilder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DellUnityDTO {
		private String arrayId;
		private String username;
		private String password;
		private String endpoint;
	}
}
