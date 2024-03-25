package com.xiilab.modulek8sdb.report.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.xiilab.modulecommon.dto.ReportType;
import com.xiilab.modulecommon.util.DataConverterUtil;
import com.xiilab.modulek8sdb.report.entity.ReportReservationEntity;
import com.xiilab.modulek8sdb.report.entity.ReportReservationUserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ReportReservationDTO {
	protected String reportName;
	protected String reportType;
	protected String explanation;
	protected String startDate;
	protected String endDate;
	protected long sendCycle;

	public ReportReservationEntity convertEntity(){
		return ReportReservationEntity.builder()
			.name(this.reportName)
			.reportType(ReportType.valueOf(this.reportType))
			.explanation(this.explanation)
			.startDate(DataConverterUtil.dataFormatterByStr(this.startDate))
			.endDate(DataConverterUtil.dataFormatterByStr(this.endDate))
			.sendCycle(this.sendCycle)
			.enable(false)
			.isScheduler(false)
			.build();
	}

	@Getter
	public static class RequestDTO extends ReportReservationDTO{
		protected List<String> userIdList;
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ResponseDTO extends ReportReservationDTO{
		protected List<ReportReservationDTO.UserDTO> userDTOList;
		private long id;
		private boolean enable;
		@Builder(builderClassName = "toDTOBuilder", builderMethodName = "toDTOBuilder")
		ResponseDTO(ReportReservationEntity reportReservation) {
			this.id = reportReservation.getId();
			this.reportName = reportReservation.getName();
			this.reportType = reportReservation.getReportType().name();
			this.explanation = reportReservation.getExplanation();
			this.startDate = DataConverterUtil.dateFormat(reportReservation.getStartDate());
			this.endDate = DataConverterUtil.dateFormat(reportReservation.getEndDate());
			this.sendCycle = reportReservation.getSendCycle();
			this.userDTOList = Objects.nonNull(reportReservation.getReservationUserEntities()) ?
				reportReservation.getReservationUserEntities().stream().map(userEntity ->
					ReportReservationDTO.UserDTO.toDTOBuilder().userEntity(userEntity).build()).toList() : new ArrayList<>();
			this.enable = reportReservation.isEnable();
		}
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserDTO {
		private long id;
		private String userId;
		private String userName; // 사용자 이름
		private String email; // 사용자 Email
		private String firstName;
		private String lastName;

		@Builder(builderMethodName = "toDTOBuilder", builderClassName = "toDTOBuilder")
		public UserDTO(ReportReservationUserEntity userEntity){
			this.id = userEntity.getId();
			this.userId = userEntity.getUserId();
			this.userName = userEntity.getUserName();
			this.email = userEntity.getEmail();
			this.firstName = userEntity.getFirstName();
			this.lastName = userEntity.getLastName();
		}
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ReceiveDTO{
		private long reportId;
		private String reportName;
		private long receiverCount;
		private String transferDate;
		private boolean result;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DetailDTO{
		private ReportType reportType;
		private String reportName;
		private String explanation;
		private List<HistoryDTO> historyDTOList;
	}
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class HistoryDTO{
		private String transferDate;
		private String userName;
		private String userEmail;
		private boolean result;
	}
}
