package com.xiilab.modulek8sdb.smtp.dto;

import com.xiilab.modulek8sdb.smtp.entity.SmtpEntity;
import com.xiilab.modulek8sdb.smtp.enums.HostType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmtpDTO {
	private HostType hostType;
	private String userName;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RequestDTO extends SmtpDTO{
		private String password;
		private String host;
		private int port;

		public SmtpEntity toEntity() {
			return SmtpEntity.builder()
				.host(getHostType() == null ? host : getHostType().getHost())
				.port(getHostType() == null ? port : getHostType().getPort())
				.userName(getUserName())
				.password(getPassword())
				.sendCount(0L)
				.build();
		}
	}

	@Getter
	public static class ResponseDTO extends SmtpDTO{
		private long id;

		public ResponseDTO(SmtpEntity smtpEntity) {
			super(HostType.getHostType(smtpEntity.getHost()), smtpEntity.getUserName());
			this.id = smtpEntity.getId();
		}
	}
}
