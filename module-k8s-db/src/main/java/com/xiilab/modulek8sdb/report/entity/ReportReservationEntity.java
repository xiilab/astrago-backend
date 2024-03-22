package com.xiilab.modulek8sdb.report.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.xiilab.modulecommon.dto.ReportType;
import com.xiilab.modulecommon.util.DataConverterUtil;
import com.xiilab.modulek8sdb.common.entity.BaseEntity;
import com.xiilab.modulek8sdb.report.dto.ReportReservationDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity(name = "TB_REPORT_RESERVATION")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ReportReservationEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	@Enumerated(EnumType.STRING)
	private ReportType reportType;
	private String name;
	private String explanation;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private long sendCycle;
	private boolean enable;
	private boolean isScheduler;
	@Builder.Default
	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<ReportReservationUserEntity> reservationUserEntities = new ArrayList<>();
	@Builder.Default
	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<ReportReservationHistoryEntity> reportReservationHistoryEntityList = new ArrayList<>();



	public void addUser(List<ReportReservationDTO.UserDTO>  userDTOList){
		if(Objects.nonNull(userDTOList)){
			List<ReportReservationUserEntity> userEntityList = userDTOList.stream().map(userDTO ->
				ReportReservationUserEntity.builder()
					.userId(userDTO.getUserId())
					.userName(userDTO.getUserName())
					.firstName(userDTO.getFirstName())
					.lastName(userDTO.getLastName())
					.email(userDTO.getEmail())
					.report(this).build()).toList();
			this.reservationUserEntities.addAll(userEntityList);
		}
	}

	public void updateReport(ReportReservationDTO requestDTO){
		this.explanation = requestDTO.getExplanation();
		this.name = requestDTO.getReportName();
		this.endDate = DataConverterUtil.dataFormatterByStr(requestDTO.getEndDate());
		this.startDate = DataConverterUtil.dataFormatterByStr(requestDTO.getStartDate());
		this.sendCycle = requestDTO.getSendCycle();
		this.reportType = ReportType.valueOf(requestDTO.getReportType());
	}

	public void updateEnable(boolean enable){
		this.enable = enable;
	}
}
