package com.xiilab.servercore.image.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.ImageErrorCode;
import com.xiilab.modulecommon.util.NumberValidUtils;
import com.xiilab.modulek8sdb.credential.entity.CredentialEntity;
import com.xiilab.modulek8sdb.credential.repository.CredentialRepository;
import com.xiilab.modulek8sdb.image.entity.BuiltInImageEntity;
import com.xiilab.modulek8sdb.image.entity.CustomImageEntity;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;
import com.xiilab.modulek8sdb.image.repository.ImageRepository;
import com.xiilab.servercore.image.dto.ImageReqDTO;
import com.xiilab.servercore.image.dto.ImageResDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
	private final ImageRepository imageRepository;
	private final CredentialRepository credentialRepository;

	@Override
	@Transactional
	public Long saveImage(ImageReqDTO.SaveImage saveImageDTO) {
		// image의 타입에 따라 저장 로직 분기처리
		switch (saveImageDTO.getImageType()) {
			case BUILT:
				return saveBuiltInImage(saveImageDTO);
			case CUSTOM:
				return saveCustomImage(saveImageDTO);
			case HUB:
				break;
			default:
				return null;
		}

		return null;
	}

	@Override
	public ImageResDTO.FindImage findImageById(Long id) {
		ImageEntity findImage = imageRepository.findById(id)
			.orElseThrow(() -> new RestApiException(ImageErrorCode.NOT_FOUND_IMAGE));
		return ImageResDTO.FindImage.from(findImage);
	}

	@Override
	public ImageResDTO.FindImages findImages(ImageReqDTO.FindSearchCondition findSearchCondition) {
		PageRequest pageRequest = null;
		if (!NumberValidUtils.isNullOrZero(findSearchCondition.getPageNo()) && !NumberValidUtils.isNullOrZero(findSearchCondition.getPageSize())) {
			pageRequest = PageRequest.of(findSearchCondition.getPageNo() - 1, findSearchCondition.getPageSize());
		}

		Page<ImageEntity> images = imageRepository.findByImages(findSearchCondition.getImageType(),
			findSearchCondition.getWorkloadType(), pageRequest);

		return ImageResDTO.FindImages.from(images.getContent(), images.getTotalElements());
	}

	@Override
	public void deleteImageById(Long id) {
		imageRepository.deleteById(id);
	}

	private Long saveBuiltInImage(ImageReqDTO.SaveImage saveImageDTO) {
		BuiltInImageEntity builtInImage = BuiltInImageEntity.builder()
			.imageName(saveImageDTO.getImageName())
			.repositoryAuthType(saveImageDTO.getRepositoryAuthType())
			.imageType(saveImageDTO.getImageType())
			.workloadType(saveImageDTO.getWorkloadType())
			.title(saveImageDTO.getTitle())
			.thumbnailSavePath(saveImageDTO.getThumbnailSavePath())
			.thumbnailSaveFileName(saveImageDTO.getThumbnailSaveFileName())
			.frameworkType(saveImageDTO.getFrameWorkType())
			.frameworkVersion(saveImageDTO.getFrameworkVersion())
			.cudaVersion(saveImageDTO.getCudaVersion())
			.build();

		try {
			BuiltInImageEntity saveBuiltInImage = imageRepository.save(builtInImage);
			return saveBuiltInImage.getId();
		} catch (IllegalArgumentException e) {
			throw new RestApiException(ImageErrorCode.FAILED_SAVE_BUILT_IN_IMAGE);
		}
	}

	private Long saveCustomImage(ImageReqDTO.SaveImage saveImage) {
		CredentialEntity credentialEntity = null;
		if (!ObjectUtils.isEmpty(saveImage.getCredentialId())) {
			credentialEntity = credentialRepository.findById(saveImage.getCredentialId())
				.orElseThrow(() -> new RestApiException(ImageErrorCode.NOT_FOUND_CREDENTIAL));
		}

		CustomImageEntity customImageEntity = CustomImageEntity.builder()
			.imageName(saveImage.getImageName())
			.repositoryAuthType(saveImage.getRepositoryAuthType())
			.imageType(saveImage.getImageType())
			.workloadType(saveImage.getWorkloadType())
			.credentialEntity(credentialEntity)
			.build();

		try {
			CustomImageEntity customImage = imageRepository.save(customImageEntity);
			return customImage.getId();
		} catch (IllegalArgumentException e) {
			throw new RestApiException(ImageErrorCode.FAILED_SAVE_CUSTOM_IMAGE);
		}
	}

}
