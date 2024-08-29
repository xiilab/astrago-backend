package com.xiilab.modulek8sdb.plugin.dto;

import com.xiilab.modulek8sdb.plugin.entity.PluginEntity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
		@NotNull(message = "arrayId은 필수 값입니다.")
		@Size(min = 10, message = "arrayId는 Dell System View에서 확인이 가능합니다.")
		private String arrayId;
		@NotNull(message = "endpoint은 필수 값입니다.")
		private String endpoint;
		@NotNull(message = "username은 필수 값입니다.")
		private String username;
		@NotNull(message = "password은 필수 값입니다.")
		private String password;
	}
}
