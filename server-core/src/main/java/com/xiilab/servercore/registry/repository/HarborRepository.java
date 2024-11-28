package com.xiilab.servercore.registry.repository;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import com.xiilab.servercore.dataset.service.WebClientService;
import com.xiilab.servercore.registry.dto.RegistryImageDTO;
import com.xiilab.servercore.registry.dto.RegistryProjectDTO;
import com.xiilab.servercore.registry.dto.RegistryTagDTO;
import com.xiilab.servercore.registry.dto.harbor.ArtifactDTO;
import com.xiilab.servercore.registry.dto.harbor.ProjectDTO;
import com.xiilab.servercore.registry.dto.harbor.RepositoryDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Harbor에서 데이터를 조회하는 클래스
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HarborRepository implements RegistryRepository {
	private final WebClientService webClientService;
	@Value("${harbor.url}")
	private String harborUrl;
	@Value("${harbor.id}")
	private String harborId;
	@Value("${harbor.password}")
	private String harborPassword;

	private URI buildUri(String path, Map<String, String> pathVariables, Map<String, Object> queryParams) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(harborUrl)
			.path(path);

		// 쿼리 파라미터 추가
		if (queryParams != null) {
			queryParams.forEach(builder::queryParam);
		}

		// 경로 변수 매핑 후 URI 생성
		return (pathVariables != null)
			? builder.buildAndExpand(pathVariables).toUri()
			: builder.build().toUri();
	}

	@Override
	public List<RegistryProjectDTO> getProjectList() {
		URI uri = buildUri("/api/v2.0/projects", null, null);
		List<ProjectDTO.ResDTO> harborProjectList = webClientService.getObjectsFromUrl(uri, ProjectDTO.ResDTO.class,
			harborId, harborPassword);
		return harborProjectList.stream().map(harborPrj -> new RegistryProjectDTO(
			harborPrj.getId(),
			harborPrj.getName(),
			harborPrj.getOwnerId(),
			harborPrj.getOwnerName(),
			harborPrj.getCreationDate()
		)).toList();
	}

	@Override
	public RegistryProjectDTO getProjectByName(String projectName) {
		URI uri = buildUri(
			"/api/v2.0/projects/{projectName}/summary",
			Map.of("projectName", projectName),
			null
		);
		ProjectDTO.SummaryDTO harborPrj = webClientService.getObjectFromUrl(uri, ProjectDTO.SummaryDTO.class, harborId,
			harborPassword);
		return null;
	}

	@Override
	public boolean validateByProjectName(String projectName) {
		URI uri = buildUri(
			"/api/v2.0/projects",
			null,
			Map.of("project_name", projectName)
		);
		try {
			webClientService.headObjectFromUrl(uri, Void.class, harborId, harborPassword);
		} catch (WebClientResponseException e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean createProject(String projectName, boolean publicYN) {
		URI uri = buildUri("/api/v2.0/projects", null, null);
		webClientService.postObjectFromUrl(
			uri, Map.of(), Map.of("project_name", projectName, "public", publicYN),
			Map.class, Void.class, harborId, harborPassword
		);
		return true;
	}

	@Override
	public boolean deleteProjectByName(String projectName) {
		URI uri = buildUri(
			"/api/v2.0/projects/{projectName}",
			Map.of("projectName", projectName),
			null
		);
		try {
			webClientService.deleteObjectFromUrl(uri, Void.class, harborId, harborPassword);
		} catch (SSLException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public List<RegistryImageDTO> getImageList(String projectName, String searchCondition, int page, int pageSize) {
		Map<String, Object> queryParams = new HashMap<>();
		if (searchCondition != null && !searchCondition.isEmpty()) {
			queryParams.put("name", searchCondition);
		}
		queryParams.put("page", page);
		queryParams.put("page_size", pageSize);

		URI uri = buildUri(
			"/api/v2.0/projects/{projectName}/repositories",
			Map.of("projectName", projectName),
			queryParams
		);

		List<RepositoryDTO.ResDTO> harborRepositoryList = webClientService.getObjectsFromUrl(
			uri, RepositoryDTO.ResDTO.class, harborId, harborPassword
		);
		return harborRepositoryList.stream().map(harborRepo -> new RegistryImageDTO(
			harborRepo.getId(),
			harborRepo.getProjectId(),
			harborRepo.getName(),
			harborRepo.getDescription(),
			harborRepo.getArtifactCnt(),
			harborRepo.getPullCnt()
		)).toList();
	}

	@Override
	public RegistryImageDTO getImageInfo(String projectName, String repositoryName) {
		try {
			URI uri = buildUri(
				"/api/v2.0/projects/{projectName}/repositories/{repositoryName}",
				Map.of("projectName", projectName, "repositoryName", repositoryName),
				null
			);
			RepositoryDTO.ResDTO harborRepository = webClientService.getObjectFromUrl(
				uri, RepositoryDTO.ResDTO.class, harborId, harborPassword
			);
			return new RegistryImageDTO(
				harborRepository.getId(),
				harborRepository.getProjectId(),
				harborRepository.getName(),
				harborRepository.getDescription(),
				harborRepository.getArtifactCnt(),
				harborRepository.getPullCnt()
			);
		} catch (WebClientResponseException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public void deleteImage(String projectName, String imageName) {
		URI uri = buildUri(
			"/api/v2.0/projects/{projectName}/repositories/{imageName}",
			Map.of("projectName", projectName, "imageName", imageName),
			null
		);
		try {
			webClientService.deleteObjectFromUrl(uri, Void.class, harborId, harborPassword);
		} catch (SSLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<RegistryTagDTO> getImageTags(String projectName, String imageName) {
		URI uri = buildUri(
			"/api/v2.0/projects/{projectName}/repositories/{imageName}/artifacts",
			Map.of("projectName", projectName, "imageName", imageName),
			null
		);

		List<ArtifactDTO> harborTags = webClientService.getObjectsFromUrl(
			uri, ArtifactDTO.class, harborId, harborPassword
		);
		return harborTags.stream().map(harborTag -> new RegistryTagDTO(
			harborTag.getId(),
			harborTag.getProjectId(),
			harborTag.getRepositoryId(),
			harborTag.getRepositoryName(),
			harborTag.getSize(),
			harborTag.getDigest(),
			harborTag.getPushTime(),
			harborTag.getPullTime(),
			CollectionUtils.isEmpty(harborTag.getTags()) ? null : harborTag.getTags().get(0).getName()
		)).toList();
	}

	@Override
	public void deleteImageTag(String projectName, String imageName, String tag) {
		URI uri = buildUri(
			"/api/v2.0/projects/{projectName}/repositories/{imageName}/artifacts/{tag}",
			Map.of("projectName", projectName, "imageName", imageName, "tag", tag),
			null
		);
		try {
			webClientService.deleteObjectFromUrl(uri, Void.class, harborId, harborPassword);
		} catch (SSLException e) {
			throw new RuntimeException(e);
		}
	}
}
