package com.xiilab.servercore.volume.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8sdb.common.enums.RepositoryDivision;
import com.xiilab.modulek8sdb.volume.entity.AstragoVolumeEntity;
import com.xiilab.modulek8sdb.volume.entity.LocalVolumeEntity;
import com.xiilab.modulek8sdb.volume.entity.Volume;
import com.xiilab.modulek8sdb.volume.entity.VolumeLabelMappingEntity;
import com.xiilab.modulek8sdb.volume.entity.VolumeWorkSpaceMappingEntity;
import com.xiilab.servercore.common.utils.CoreFileUtils;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class VolumeResDTO {
	private String creator;
	private String creatorName;
	private LocalDateTime createdAt;
	private Long volumeId;
	private String volumeName;
	private StorageType storageType;
	private String defaultPath;
	private String size;
	private RepositoryDivision division;
	private Integer requestVolume;
	private List<Label> labels;

	@Getter
	@SuperBuilder
	public static class ResVolumeWithStorage extends VolumeResDTO {
		private String storageName;
		private String ip;
		private String storagePath;
		private String volumePath;
		private String saveDirectoryName;

		public static VolumeResDTO.ResVolumeWithStorage toDto(Volume volume) {
			if (volume.isAstragoVolume()) {
				return VolumeResDTO.ResVolumeWithStorage.builder()
					.volumeId(volume.getVolumeId())
					.storageType(((AstragoVolumeEntity)volume).getStorageEntity().getStorageType())
					.volumeName(volume.getVolumeName())
					.requestVolume(((AstragoVolumeEntity)volume).getStorageEntity().getRequestVolume())
					.creator(volume.getRegUser().getRegUserName())
					.creatorName(volume.getRegUser().getRegUserRealName())
					.createdAt(volume.getRegDate())
					.division(volume.getDivision())
					.ip(((AstragoVolumeEntity)volume).getStorageEntity().getIp())
					.storagePath(((AstragoVolumeEntity)volume).getStorageEntity().getStoragePath())
					.volumePath(((AstragoVolumeEntity)volume).getVolumePath())
					.storageName(((AstragoVolumeEntity)volume).getStorageEntity().getStorageName())
					.size(CoreFileUtils.formatFileSize(volume.getVolumeSize()))
					.defaultPath(volume.getVolumeDefaultMountPath())
					.saveDirectoryName(((AstragoVolumeEntity)volume).getSaveDirectoryName())
					.labels(volume.getVolumeLabelMappingList().stream().map(Label::new).toList())
					.build();
			} else if (volume.isLocalVolume()) {
				return ResVolumeWithStorage.builder()
					.volumeId(volume.getVolumeId())
					.storageType(((LocalVolumeEntity)volume).getStorageType())
					.volumeName(volume.getVolumeName())
					.creator(volume.getRegUser().getRegUserName())
					.creatorName(volume.getRegUser().getRegUserRealName())
					.createdAt(volume.getRegDate())
					.division(volume.getDivision())
					.ip(((LocalVolumeEntity)volume).getIp())
					.storagePath(((LocalVolumeEntity)volume).getStoragePath())
					.volumePath("/")
					.defaultPath(volume.getVolumeDefaultMountPath())
					.labels(volume.getVolumeLabelMappingList().stream().map(Label::new).toList())
					.build();
			}

			return null;
		}
	}

	@Getter
	@SuperBuilder
	public static class ResVolume extends VolumeResDTO {
		private boolean isAvailable;

		public static VolumeResDTO.ResVolume toDto(Volume volume) {
			if (volume.isAstragoVolume()) {
				return ResVolume.builder()
					.volumeId(volume.getVolumeId())
					.storageType(((AstragoVolumeEntity)volume).getStorageEntity().getStorageType())
					.volumeName(volume.getVolumeName())
					.requestVolume(((AstragoVolumeEntity)volume).getStorageEntity().getRequestVolume())
					.creator(volume.getRegUser().getRegUserName())
					.creatorName(volume.getRegUser().getRegUserRealName())
					.createdAt(volume.getRegDate())
					.isAvailable(volume.isAvailable())
					.division(volume.getDivision())
					.size(CoreFileUtils.formatFileSize(volume.getVolumeSize()))
					.defaultPath(volume.getVolumeDefaultMountPath())
					.labels(volume.getVolumeLabelMappingList().stream().map(Label::new).toList())
					.build();
			} else if (volume.isLocalVolume()) {
				return ResVolume.builder()
					.volumeId(volume.getVolumeId())
					.storageType(((LocalVolumeEntity)volume).getStorageType())
					.volumeName(volume.getVolumeName())
					.requestVolume(null)
					.creator(volume.getRegUser().getRegUserName())
					.creatorName(volume.getRegUser().getRegUserRealName())
					.createdAt(volume.getRegDate())
					.isAvailable(volume.isAvailable())
					.division(volume.getDivision())
					.size(CoreFileUtils.formatFileSize(volume.getVolumeSize()))
					.defaultPath(volume.getVolumeDefaultMountPath())
					.labels(volume.getVolumeLabelMappingList().stream().map(Label::new).toList())
					.build();
			}
			return null;
		}
	}

	@Getter
	@Builder
	public static class ResVolumes {
		List<VolumeResDTO.ResVolume> volumes;
		long totalCount;

		public static VolumeResDTO.ResVolumes entitiesToDtos(List<Volume> volumes, long totalCount) {
			return VolumeResDTO.ResVolumes
				.builder()
				.totalCount(totalCount)
				.volumes(volumes.stream().map(VolumeResDTO.ResVolume::toDto).toList())
				.build();
		}
	}

	@Getter
	@SuperBuilder
	public static class VolumeInWorkspace extends VolumeResDTO {
		private String userId;
		private boolean isAvailable;

		public static VolumeInWorkspace entityToDto(Volume volume) {
			if (volume.isAstragoVolume()) {
				return VolumeInWorkspace.builder()
					.volumeId(volume.getVolumeId())
					.volumeName(volume.getVolumeName())
					.storageType(((AstragoVolumeEntity)volume).getStorageEntity().getStorageType())
					.creator(volume.getRegUser().getRegUserName())
					.creatorName(volume.getRegUser().getRegUserRealName())
					.createdAt(volume.getRegDate())
					.isAvailable(volume.isAvailable())
					.division(volume.getDivision())
					.size(CoreFileUtils.formatFileSize(volume.getVolumeSize()))
					.defaultPath(volume.getVolumeDefaultMountPath())
					.userId(volume.getRegUser().getRegUserId())
					.labels(volume.getVolumeLabelMappingList().stream().map(Label::new).toList())
					.build();
			} else if (volume.isLocalVolume()) {
				return VolumeInWorkspace.builder()
					.volumeId(volume.getVolumeId())
					.storageType(((LocalVolumeEntity)volume).getStorageType())
					.volumeName(volume.getVolumeName())
					.creator(volume.getRegUser().getRegUserName())
					.creatorName(volume.getRegUser().getRegUserRealName())
					.createdAt(volume.getRegDate())
					.isAvailable(volume.isAvailable())
					.division(volume.getDivision())
					.defaultPath(volume.getVolumeDefaultMountPath())
					.userId(volume.getRegUser().getRegUserId())
					.labels(volume.getVolumeLabelMappingList().stream().map(Label::new).toList())
					.build();
			}
			return null;
		}

		public static VolumeInWorkspace mappingEntityToDto(VolumeWorkSpaceMappingEntity volumeWorkSpaceMappingEntity) {
			if (volumeWorkSpaceMappingEntity.getVolume().isAstragoVolume()) {
				return VolumeInWorkspace.builder()
					.volumeId(volumeWorkSpaceMappingEntity.getVolume().getVolumeId())
					.volumeName(volumeWorkSpaceMappingEntity.getVolume().getVolumeName())
					.storageType(((AstragoVolumeEntity)volumeWorkSpaceMappingEntity.getVolume()).getStorageEntity().getStorageType())
					.creator(volumeWorkSpaceMappingEntity.getRegUser().getRegUserName())
					.creatorName(volumeWorkSpaceMappingEntity.getRegUser().getRegUserRealName())
					.createdAt(volumeWorkSpaceMappingEntity.getRegDate())
					.isAvailable(volumeWorkSpaceMappingEntity.getVolume().isAvailable())
					.division(volumeWorkSpaceMappingEntity.getVolume().getDivision())
					.size(CoreFileUtils.formatFileSize(volumeWorkSpaceMappingEntity.getVolume().getVolumeSize()))
					.defaultPath(volumeWorkSpaceMappingEntity.getVolumeDefaultMountPath())
					.userId(volumeWorkSpaceMappingEntity.getRegUser().getRegUserId())
					.labels(volumeWorkSpaceMappingEntity.getVolume().getVolumeLabelMappingList().stream().map(Label::new).toList())
					.build();
			} else if (volumeWorkSpaceMappingEntity.getVolume().isLocalVolume()) {
				return VolumeInWorkspace.builder()
					.volumeId(volumeWorkSpaceMappingEntity.getVolume().getVolumeId())
					.storageType(((LocalVolumeEntity)volumeWorkSpaceMappingEntity.getVolume()).getStorageType())
					.volumeName(volumeWorkSpaceMappingEntity.getVolume().getVolumeName())
					.creator(volumeWorkSpaceMappingEntity.getRegUser().getRegUserName())
					.creatorName(volumeWorkSpaceMappingEntity.getRegUser().getRegUserRealName())
					.createdAt(volumeWorkSpaceMappingEntity.getRegDate())
					.isAvailable(volumeWorkSpaceMappingEntity.getVolume().isAvailable())
					.division(volumeWorkSpaceMappingEntity.getVolume().getDivision())
					.defaultPath(volumeWorkSpaceMappingEntity.getVolumeDefaultMountPath())
					.userId(volumeWorkSpaceMappingEntity.getRegUser().getRegUserId())
					.labels(volumeWorkSpaceMappingEntity.getVolume().getVolumeLabelMappingList().stream().map(Label::new).toList())
					.build();
			}
			return null;
		}
	}

	@Getter
	@Builder
	public static class VolumesInWorkspace {
		private List<VolumeInWorkspace> volumes;

		public static VolumeResDTO.VolumesInWorkspace entitiesToDtos(List<Volume> volumes) {
			return VolumeResDTO.VolumesInWorkspace.builder()
				.volumes(volumes.stream().map(VolumeInWorkspace::entityToDto).toList())
				.build();
		}

		public static VolumeResDTO.VolumesInWorkspace mappingEntitiesToDtos(
			List<VolumeWorkSpaceMappingEntity> volumes) {
			return VolumeResDTO.VolumesInWorkspace.builder()
				.volumes(volumes.stream().map(VolumeInWorkspace::mappingEntityToDto).toList())
				.build();
		}
	}

	@Getter
	public static class Label {
		private Long labelId;
		private String labelName;
		private String colorCode;
		private String colorCodeName;
		private Integer order;

		public Label(VolumeLabelMappingEntity volumeLabelMappingEntity) {
			this.labelId = volumeLabelMappingEntity.getId();
			this.labelName = volumeLabelMappingEntity.getLabel().getName();
			this.colorCode = volumeLabelMappingEntity.getLabel().getColorCode();
			this.colorCodeName = volumeLabelMappingEntity.getLabel().getColorName();
			this.order = volumeLabelMappingEntity.getLabel().getOrder();
		}
	}
}
