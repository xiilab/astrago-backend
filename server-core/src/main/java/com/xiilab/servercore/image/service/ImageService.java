package com.xiilab.servercore.image.service;

import com.xiilab.modulek8sdb.image.entity.ImageEntity;
import com.xiilab.servercore.image.dto.ImageReqDTO;
import com.xiilab.servercore.image.dto.ImageResDTO;

public interface ImageService {
	ImageEntity saveImage(ImageReqDTO.SaveImage saveImageDTO);

	ImageResDTO.FindImage findImageById(Long id);

	void deleteImageById(Long id);

	// void saveHubImage(ImageRequestDTO.CreateHubImage createHubImage);
	// 빌트인, 허브, 커스텀...
	// ImageDTO.ResDTO getImageById(long id);
	// List<ImageDTO.ResDTO> getImageList();
	// void deleteImageById(long id);
}
