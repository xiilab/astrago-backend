package com.xiilab.modulek8sdb.alert.systemalert.dto;

import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertSetEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SystemAlertSetDTO {
	protected boolean licenseSystemYN;
	protected boolean licenseEmailYN;
	protected boolean userSystemYN;
	protected boolean userEmailYN;
	protected boolean nodeSystemYN;
	protected boolean nodeEmailYN;
	protected boolean wsProduceSystemYN;
	protected boolean wsProduceEmailYN;
	protected boolean resourceOverSystemYN;
	protected boolean resourceOverEmailYN;
	protected boolean wsResourceSystemYN;
	protected boolean wsResourceEmailYN;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@SuperBuilder
	public static class ResponseDTO extends SystemAlertSetDTO{
		private Long id;

		@Builder(builderMethodName = "receiveDTOBuilder", builderClassName = "receiveDTOBuilder")
		ResponseDTO(SystemAlertSetEntity entity){
			this.id = entity.getId();
			this.licenseSystemYN = entity.isLicenseSystemYN();
			this.licenseEmailYN = entity.isLicenseEmailYN();
			this.userSystemYN = entity.isUserSystemYN();
			this.userEmailYN = entity.isUserEmailYN();
			this.nodeSystemYN = entity.isNodeSystemYN();
			this.nodeEmailYN = entity.isNodeEmailYN();
			this.wsProduceSystemYN = entity.isWsProduceSystemYN();
			this.wsProduceEmailYN = entity.isWsProduceEmailYN();
			this.resourceOverSystemYN = entity.isResourceOverSystemYN();
			this.resourceOverEmailYN = entity.isResourceOverEmailYN();
			this.wsResourceSystemYN = entity.isWsResourceSystemYN();
			this.wsResourceEmailYN = entity.isWsResourceEmailYN();
		}
	}

}
