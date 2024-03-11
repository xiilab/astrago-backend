package com.xiilab.modulek8sdb.alert.systemalert.dto;

import java.time.LocalDateTime;

import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.SystemAlertType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SystemAlertDTO {
	private String recipientId; // 받는 사람 ID
	private String senderId; // 보내는 사람 ID
	private SystemAlertType systemAlertType; // 발생된 알림 타입
	private String title; // 알림 제목
	private String message; // 알림 내용
	private LocalDateTime alertDate; // 알림 일자
	private boolean readYN; // 알림 읽음 여부

	public static SystemAlertEntity convertEntity(SystemAlertDTO systemAlertDTO){
		return SystemAlertEntity.builder()
			.recipientId(systemAlertDTO.getRecipientId())
			.senderId(systemAlertDTO.getSenderId())
			.systemAlertType(systemAlertDTO.getSystemAlertType())
			.message(systemAlertDTO.getMessage())
			.alertDate(systemAlertDTO.getAlertDate())
			.readYN(systemAlertDTO.isReadYN())
			.build();
	}

	@Getter
	@SuperBuilder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ResponseDTO extends SystemAlertDTO {
		private long id; // Alert Index

		public static ResponseDTO convertResponseDTO(SystemAlertEntity systemAlertEntity){
			return  ResponseDTO.builder()
				.id(systemAlertEntity.getId())
				.recipientId(systemAlertEntity.getRecipientId())
				.senderId(systemAlertEntity.getSenderId())
				.systemAlertType(systemAlertEntity.getSystemAlertType())
				.message(systemAlertEntity.getMessage())
				.alertDate(systemAlertEntity.getAlertDate())
				.readYN(systemAlertEntity.isReadYN())
				.build();
		}
	}


}