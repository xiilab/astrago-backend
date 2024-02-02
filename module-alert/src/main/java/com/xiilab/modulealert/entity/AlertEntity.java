package com.xiilab.modulealert.entity;

import java.time.LocalDateTime;

import com.xiilab.modulealert.enumeration.AlertType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_ALERT")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class AlertEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ALERT_ID")
	private Long id;
	@Column(name = "RECIPIENT")
	private String recipientId;
	@Column(name = "SENDER")
	private String senderId;
	@Column(name = "ALERT_TYPE")
	private AlertType alertType;
	@Column(name = "ALERT_MESSAGE")
	private String message;
	@Column(name = "ALERT_DATE")
	private LocalDateTime alertDate;
	@Column(name = "READ_YN")
	private Boolean readYN;

	public void readAlert(){
		this.readYN = true;
	}
}