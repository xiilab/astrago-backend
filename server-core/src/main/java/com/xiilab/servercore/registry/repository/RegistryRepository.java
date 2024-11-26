package com.xiilab.servercore.registry.repository;

import java.util.List;

import com.xiilab.servercore.registry.dto.RegistryImageDTO;
import com.xiilab.servercore.registry.dto.RegistryProjectDTO;
import com.xiilab.servercore.registry.dto.RegistryTagDTO;

public interface RegistryRepository {

	/**
	 * 프로젝트 리스트 전체 조회
	 *
	 * @return 프로젝트 Info list
	 */
	List<RegistryProjectDTO> getProjectList();

	/**
	 * 프로젝트 정보 조회
	 *
	 * @param projectName 조회할 프로젝트의 이름
	 * @return 프로젝트 info dto
	 */
	RegistryProjectDTO getProjectByName(String projectName);

	/**
	 * 해당 프로젝트 이름으로 된 프로젝트가 존재하는 지 확인하는 메소드
	 *
	 * @param projectName 조회할 프로젝트 이름
	 * @return 프로젝트 존재 여부
	 */
	boolean validateByProjectName(String projectName);

	/**
	 * 프로젝트 생성
	 *
	 * @param projectName 프로젝트 이름
	 * @param publicYN    공개여부
	 * @return 생성여부
	 */
	boolean createProject(String projectName, boolean publicYN);

	/**
	 * project 삭제
	 *
	 * @param projectName 삭제 할 프로젝트의 이름
	 * @return 삭제 여부
	 */
	boolean deleteProjectByName(String projectName);

	/**
	 * project에 등록된 image 조회
	 *
	 * @param projectName     조회 할 프로젝트 조회
	 * @param searchCondition
	 * @param page
	 * @param pageSize
	 * @return image 정보 리스트
	 */
	List<RegistryImageDTO> getImageList(String projectName, String searchCondition, int page, int pageSize);

	/**
	 * 등록된 repository의 정보 조회
	 *
	 * @param projectName    프로젝트 이름
	 * @param repositoryName 레포지토리 이름
	 * @return image 정보
	 */
	RegistryImageDTO getImageInfo(String projectName, String repositoryName);

	/**
	 * image를 삭제하는 메소드
	 *
	 * @param projectName 삭제 할 이미지가 속한 프로젝트
	 * @param imageName   삭제 할 이미지의 이름
	 */
	void deleteImage(String projectName, String imageName);

	/**
	 * image의 tag 리스트를 조회하는 메소드
	 *
	 * @param projectName 조회 할 project 이름
	 * @param imageName   조회 할 image 이름
	 * @return 이미지 tag 리스트
	 */
	List<RegistryTagDTO> getImageTags(String projectName, String imageName);

	/**
	 * 이미지의 특정 tag를 삭제하는 메소드
	 *
	 * @param projectName 삭제 할 프로젝트 이름
	 * @param imageName   삭제 할 이미지 이름
	 * @param tag         삭제 할 이미지 태그
	 */
	void deleteImageTag(String projectName, String imageName, String tag);
}
