package com.xiilab.modulealert.dto;

import java.time.LocalDateTime;

import com.xiilab.modulealert.entity.AlertEntity;
import com.xiilab.modulealert.enumeration.AlertType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDTO {
	private String recipientId; // 받는 사람 ID
	private String senderId; // 보내는 사람 ID
	private AlertType alertType; // 발생된 알림 타입
	private String message; // 알림 내용
	private LocalDateTime alertDate; // 알림 일자
	private boolean readYN; // 알림 읽음 여부

	@Getter
	@SuperBuilder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ResponseDTO extends AlertDTO{
		private long id; // Alert Index

		public static ResponseDTO convertResponseDTO(AlertEntity alertEntity){
			return  ResponseDTO.builder()
				.id(alertEntity.getId())
				.recipientId(alertEntity.getRecipientId())
				.senderId(alertEntity.getSenderId())
				.alertType(alertEntity.getAlertType())
				.message(alertEntity.getMessage())
				.alertDate(alertEntity.getAlertDate())
				.readYN(alertEntity.isReadYN())
				.build();
		}
	}
	public static AlertEntity convertEntity(AlertDTO alertDTO){
		return AlertEntity.builder()
			.recipientId(alertDTO.getRecipientId())
			.senderId(alertDTO.getSenderId())
			.alertType(alertDTO.getAlertType())
			.message(alertDTO.getMessage())
			.alertDate(alertDTO.getAlertDate())
			.readYN(alertDTO.isReadYN())
			.build();
	}


}