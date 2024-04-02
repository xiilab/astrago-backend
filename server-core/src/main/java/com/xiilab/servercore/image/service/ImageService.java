package com.xiilab.servercore.image.service;

import com.xiilab.servercore.image.dto.ImageReqDTO;
import com.xiilab.servercore.image.dto.ImageResDTO;

public interface ImageService {
	Long saveImage(ImageReqDTO.SaveImage saveImageDTO);

	ImageResDTO.FindImage findImageById(Long id);

	ImageResDTO.FindImages findImages(ImageReqDTO.FindSearchCondition findSearchCondition);

	void deleteImageById(Long id);

	void deleteImageWorkloadMapping(Long jobId);
}
