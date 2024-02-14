package com.xiilab.servercore.image.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8sdb.image.dto.ImageDTO;
import com.xiilab.modulek8sdb.image.repository.ImageRepository;
import com.xiilab.modulek8sdb.workload.history.entity.ImageEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
	private final ImageRepository imageRepository;

	@Override
	public void saveImage(ImageDTO.ReqDTO imageDTO) {
		// imageRepository.save(ImageEntity.builder()
		// 	.name(imageDTO.getName())
		// 	.tag(imageDTO.getTag())
		// 	.description(imageDTO.getDescription())
		// 	.build());
	}

	@Override
	public ImageDTO.ResDTO getImageById(long id) {
		// ImageEntity imageEntity = imageRepository.findById(id).orElseThrow(IllegalArgumentException::new);
		// return ImageDTO.ResDTO.builder()
		// 	.id(imageEntity.getId())
		// 	.name(imageEntity.getName())
		// 	.tag(imageEntity.getTag())
		// 	.description(imageEntity.getDescription())
		// 	.build();
		return null;
	}

	@Override
	public List<ImageDTO.ResDTO> getImageList() {
		// List<ImageEntity> imageList = imageRepository.findAll();
		// return imageList.stream().map(imageEntity -> ImageDTO.ResDTO.builder()
		// 	.id(imageEntity.getId())
		// 	.name(imageEntity.getName())
		// 	.tag(imageEntity.getTag())
		// 	.description(imageEntity.getDescription())
		// 	.build()).collect(Collectors.toList());
		return null;
	}

	@Override
	public void deleteImageById(long id) {
		// imageRepository.deleteById(id);
	}
}
