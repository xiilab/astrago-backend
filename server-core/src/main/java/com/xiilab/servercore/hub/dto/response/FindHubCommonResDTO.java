package com.xiilab.servercore.hub.dto.response;

import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.image.entity.HubImageEntity;
import com.xiilab.servercore.common.dto.ResDTO;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
public class FindHubCommonResDTO {
	@Getter
	public static class HubImage extends ResDTO {
		private Long id;
		private ImageType imageType;
		private DeleteYN deleteYN;
		private String imageName;
		private RepositoryAuthType repositoryAuthType;
		private WorkloadType workloadType;

		public HubImage(HubImageEntity hubImageEntity) {
			super(hubImageEntity.getRegUser().getRegUserId(), hubImageEntity.getRegUser().getRegUserName(),
				hubImageEntity.getRegUser().getRegUserRealName(), hubImageEntity.getRegDate(),
				hubImageEntity.getModDate());
			this.id = hubImageEntity.getId();
			this.imageType = hubImageEntity.getImageType();
			this.deleteYN = hubImageEntity.getDeleteYN();
			this.imageName = hubImageEntity.getImageName();
			this.repositoryAuthType = hubImageEntity.getRepositoryAuthType();
			this.workloadType = hubImageEntity.getWorkloadType();
		}
	}
}
