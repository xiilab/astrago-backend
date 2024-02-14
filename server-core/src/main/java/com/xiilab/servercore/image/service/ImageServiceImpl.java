package com.xiilab.servercore.image.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.xiilab.moduleuser.service.UserService;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.image.dto.ImageDTO;
import com.xiilab.servercore.image.entity.ImageEntity;
import com.xiilab.servercore.image.repository.ImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
	private final ImageRepository imageRepository;
	private final UserService userService;
	@Override
	public void saveImage(ImageDTO.ReqDTO imageDTO, UserInfoDTO userInfoDTO) {
		imageRepository.save(ImageEntity.builder()
			.name(imageDTO.getName())
			.tag(imageDTO.getTag())
			.description(imageDTO.getDescription())
			.build());
		userService.increaseUserImageCount(userInfoDTO.getId());
	}

	@Override
	public ImageDTO.ResDTO getImageById(long id) {
		ImageEntity imageEntity = imageRepository.findById(id).orElseThrow(IllegalArgumentException::new);
		return ImageDTO.ResDTO.builder()
			.id(imageEntity.getId())
			.name(imageEntity.getName())
			.tag(imageEntity.getTag())
			.description(imageEntity.getDescription())
			.build();
	}

	@Override
	public List<ImageDTO.ResDTO> getImageList() {
		List<ImageEntity> imageList = imageRepository.findAll();
		return imageList.stream().map(imageEntity -> ImageDTO.ResDTO.builder()
			.id(imageEntity.getId())
			.name(imageEntity.getName())
			.tag(imageEntity.getTag())
			.description(imageEntity.getDescription())
			.build()).collect(Collectors.toList());
	}

	@Override
	public void deleteImageById(long id) {
		imageRepository.deleteById(id);
	}
}
