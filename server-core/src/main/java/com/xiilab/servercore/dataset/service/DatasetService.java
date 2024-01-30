package com.xiilab.servercore.dataset.service;

import java.io.File;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.xiilab.servercore.dataset.dto.DatasetDTO;
import com.xiilab.servercore.dataset.entity.AstragoDatasetEntity;

public interface DatasetService {
	void insertAstragoDataset(AstragoDatasetEntity astragoDatasetEntity, List<MultipartFile> files);
}
