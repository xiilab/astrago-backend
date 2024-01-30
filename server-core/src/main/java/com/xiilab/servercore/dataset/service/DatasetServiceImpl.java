package com.xiilab.servercore.dataset.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.servercore.dataset.entity.AstragoDatasetEntity;
import com.xiilab.servercore.dataset.repository.DatasetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DatasetServiceImpl implements DatasetService{

	private final DatasetRepository datasetRepository;

	@Override
	public void insertAstragoDataset(AstragoDatasetEntity astragoDatasetEntity, List<MultipartFile> files) {
		//dataset 저장
		datasetRepository.save(astragoDatasetEntity);
		//파일 업로드
		String hostStorageRootPath = astragoDatasetEntity.getStorageEntity().getHostPath();
		String datasetPath = hostStorageRootPath + "/" + astragoDatasetEntity.getDatasetName();
		// 업로드된 파일을 저장할 경로 설정
		Path uploadPath = Paths.get(datasetPath);
		try {
			Files.createDirectories(uploadPath);
			// 업로드된 각 파일에 대해 작업 수행
			for (MultipartFile file : files) {
				Path targetPath = uploadPath.resolve(file.getOriginalFilename());
				Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			throw new RuntimeException("파일 업로드를 실패했습니다.");
		}
	}
}
