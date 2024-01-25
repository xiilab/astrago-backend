package com.xiilab.servercore.image.service;

import java.util.List;

import com.xiilab.servercore.image.dto.ImageDTO;

public interface ImageService {
	void saveImage(ImageDTO.ReqDTO imageDTO);
	ImageDTO.ResDTO getImageById(long id);
	List<ImageDTO.ResDTO> getImageList();
	void deleteImageById(long id);
}
