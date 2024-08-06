package com.xiilab.servercore.volume.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.dto.DirectoryDTO;
import com.xiilab.modulecommon.dto.FileInfoDTO;
import com.xiilab.modulecommon.enums.PageMode;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8sdb.common.enums.PageInfo;
import com.xiilab.modulek8sdb.common.enums.RepositorySearchCondition;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.dataset.dto.DownloadFileResDTO;
import com.xiilab.servercore.volume.dto.VolumeReqDTO;
import com.xiilab.servercore.volume.dto.VolumeResDTO;
import com.xiilab.servercore.volume.service.VolumeFacadeService;
import com.xiilab.servercore.volume.service.VolumeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "VolumeController", description = "볼륨 API")
@RestController
@RequestMapping("/api/v1/core/volumes")
@RequiredArgsConstructor
public class VolumeController {
	private final VolumeService volumeService;
	private final VolumeFacadeService volumeFacadeService;

	@PostMapping("/astrago")
	@Operation(summary = "아스트라고 볼륨 생성")
	public ResponseEntity<HttpStatus> insertAstragoVolume(
		@RequestPart(name = "createAstragoVolumeDTO") VolumeReqDTO.Edit.CreateAstragoVolume createAstragoVolumeDTO,
		@RequestPart(name = "files", required = false) List<MultipartFile> files) {
		volumeFacadeService.insertAstragoVolume(createAstragoVolumeDTO, files);
		return new ResponseEntity(HttpStatus.OK);
	}

	@PostMapping("/local")
	@Operation(summary = "로컬 볼륨 생성")
	public ResponseEntity<HttpStatus> insertLocalVolume(
		@RequestBody VolumeReqDTO.Edit.CreateLocalVolume createLocalVolumeDTO) {
		volumeFacadeService.insertLocalVolume(createLocalVolumeDTO);
		return new ResponseEntity(HttpStatus.OK);
	}

	@GetMapping("")
	@Operation(summary = "볼륨 전체 조회")
	public ResponseEntity<VolumeResDTO.ResVolumes> getVolumes(
		RepositorySearchCondition repositorySearchCondition,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO,
		@RequestParam(value = "pageMode") PageMode pageMode) {
		return new ResponseEntity(
			volumeService.getVolumes(repositorySearchCondition, userInfoDTO, pageMode),
			HttpStatus.OK);
	}

	@GetMapping("/{volumeId}")
	@Operation(summary = "볼륨 상세 조회")
	public ResponseEntity<VolumeResDTO.ResVolumeWithStorage> getVolume(
		@PathVariable(name = "volumeId") Long volumeId) {
		return new ResponseEntity(
			volumeFacadeService.getVolume(volumeId)
			, HttpStatus.OK);
	}

	@GetMapping("/{volumeId}/workloads")
	@Operation(summary = "볼륨을 사용중인 워크로드 리스트 조회")
	public ResponseEntity<WorkloadResDTO.PageUsingVolumeDTO> getWorkloadsUsingVolume(
		PageInfo pageInfo,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO,
		@PathVariable(name = "volumeId") Long volumeId
	) {
		return new ResponseEntity(
			volumeFacadeService.getWorkloadsUsingVolume(pageInfo, volumeId, userInfoDTO),
			HttpStatus.OK);
	}

	@PutMapping("/{volumeId}")
	@Operation(summary = "볼륨 수정")
	public ResponseEntity<HttpStatus> modifyVolume(
		@PathVariable(name = "volumeId") Long volumeId,
		@RequestBody VolumeReqDTO.Edit.ModifyVolume modifyVolumeDTO,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO) {
		volumeFacadeService.modifyVolume(modifyVolumeDTO, volumeId, userInfoDTO);
		return new ResponseEntity(HttpStatus.OK);
	}

	@DeleteMapping("/{volumeId}")
	@Operation(summary = "볼륨 삭제")
	public ResponseEntity<HttpStatus> deleteVolume(
		@PathVariable(name = "volumeId") Long volumeId,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO) {
		volumeFacadeService.deleteVolume(volumeId, userInfoDTO);
		return new ResponseEntity(HttpStatus.OK);
	}

	@GetMapping("/astrago/{volumeId}/files")
	@Operation(summary = "astrago 볼륨 파일리스트 조회")
	public ResponseEntity<DirectoryDTO> getAstragoVolumeFiles(
		@PathVariable(name = "volumeId") Long volumeId,
		@RequestParam(value = "filePath") String filePath) {
		return new ResponseEntity(
			volumeService.getAstragoVolumeFiles(volumeId, filePath)
			, HttpStatus.OK);
	}

	@GetMapping("/astrago/{volumeId}/file")
	@Operation(summary = "astrago 볼륨 파일 상세 조회")
	public ResponseEntity<FileInfoDTO> getAstragoVolumeFileInfo(
		@PathVariable(name = "volumeId") Long volumeId,
		@RequestParam(value = "filePath") String filePath) {
		return new ResponseEntity(
			volumeFacadeService.getAstragoVolumeFileInfo(volumeId, filePath),
			HttpStatus.OK
		);
	}

	@GetMapping("/astrago/{volumeId}/preview")
	@Operation(summary = "astrago 볼륨 파일 미리 보기")
	public ResponseEntity<Resource> getAstragoVolumeFile(@PathVariable(name = "volumeId") Long volumeId,
		@RequestParam(value = "filePath") String filePath) {
		DownloadFileResDTO file = volumeFacadeService.getAstragoVolumeFile(volumeId, filePath);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(file.getMediaType());
		return new ResponseEntity(file.getByteArrayResource(), headers, HttpStatus.OK);
	}

	@PostMapping("/astrago/{volumeId}/files/upload")
	@Operation(summary = "astrago 볼륨 파일 업로드")
	public ResponseEntity<HttpStatus> astragoVolumeUploadFile(
		@PathVariable(name = "volumeId") Long volumeId,
		@RequestPart(name = "path") String path,
		@RequestPart(name = "files") List<MultipartFile> files) {
		volumeService.astragoVolumeUploadFile(volumeId, path, files);
		return new ResponseEntity(HttpStatus.OK);
	}

	@PostMapping("/astrago/{volumeId}/directory")
	@Operation(summary = "astrago 볼륨폴더 생성")
	public ResponseEntity<HttpStatus> astragoVolumeCreateDirectory(@PathVariable(name = "volumeId") Long volumeId,
		@RequestBody VolumeReqDTO.FilePath filePathDTO) {
		volumeService.astragoVolumeCreateDirectory(volumeId, filePathDTO);
		return new ResponseEntity(HttpStatus.OK);
	}

	@PostMapping("/astrago/{volumeId}/files/delete")
	@Operation(summary = "astrago 볼륨 파일, 디렉토리 삭제")
	public ResponseEntity<HttpStatus> astragoDatasetDeleteFiles(@PathVariable(name = "volumeId") Long volumeId,
		@RequestBody VolumeReqDTO.FilePaths reqFilePathsDTO) {
		volumeService.astragoVolumeDeleteFiles(volumeId, reqFilePathsDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/astrago/{volumeId}/files/download")
	@Operation(summary = "astrago 볼륨 파일, 디렉토리 다운로드")
	public ResponseEntity<Resource> downloadAstragoDatasetFile(@PathVariable(name = "volumeId") Long volumeId,
		@RequestParam(value = "filePath") String filePath) {
		DownloadFileResDTO downloadFileResDTO = volumeService.downloadAstragoVolumeFile(volumeId, filePath);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(downloadFileResDTO.getMediaType());
		headers.add("Content-Disposition", "attachment; filename=" + downloadFileResDTO.getFileName());
		return new ResponseEntity<>(downloadFileResDTO.getByteArrayResource(), headers, HttpStatus.OK);
	}

	@PostMapping("/astrago/{volumeId}/compress")
	@Operation(summary = "astrago 볼륨 압축")
	public ResponseEntity<HttpStatus> compressAstragoDatasetFiles(@PathVariable(name = "volumeId") Long volumeId,
		@RequestBody VolumeReqDTO.Compress compressReq) {

		volumeService.compressAstragoVolumeFiles(volumeId, compressReq);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/astrago/{volumeId}/decompress")
	@Operation(summary = "astrago 볼륨 압축해제")
	public ResponseEntity<HttpStatus> deCompressAstragoDatasetFile(@PathVariable(name = "volumeId") Long volumeId,
		@RequestParam(value = "filePath") String filePath) {

		volumeService.deCompressAstragoVolumeFile(volumeId, filePath);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/local/{volumeId}/files")
	@Operation(summary = "local 볼륨 파일, 디렉토리 리스트 조회")
	public ResponseEntity<DirectoryDTO> getLocalDatasetFiles(@PathVariable(name = "volumeId") Long volumeId,
		@RequestParam(value = "filePath") String filePath) {
		DirectoryDTO files = volumeFacadeService.getLocalVolumeFiles(volumeId,
			filePath);
		return new ResponseEntity<>(files, HttpStatus.OK);
	}

	@GetMapping("/local/{volumeId}/files/download")
	@Operation(summary = "local 볼륨 파일 다운로드")
	public ResponseEntity<Resource> DownloadLocalDatasetFile(@PathVariable(name = "volumeId") Long volumeId,
		@RequestParam(value = "filePath") String filePath) {
		DownloadFileResDTO file = volumeFacadeService.downloadLocalVolumeFile(volumeId,
			filePath);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(file.getMediaType());
		headers.add("Content-Disposition", "attachment; filename=" + file.getFileName());
		return new ResponseEntity<>(file.getByteArrayResource(), headers, HttpStatus.OK);
	}

	@GetMapping("/local/{volumeId}/file")
	@Operation(summary = "local 볼륨 파일 상세 조회")
	public ResponseEntity<FileInfoDTO> getLocalVolumeFileInfo(@PathVariable(name = "volumeId") Long volumeId,
		@RequestParam(value = "filePath") String filePath) {
		return new ResponseEntity<>(volumeFacadeService.getLocalVolumeFileInfo(volumeId, filePath), HttpStatus.OK);
	}

	@GetMapping("/local/{volumeId}/preview")
	@Operation(summary = "local 볼륨 파일 미리 보기")
	public ResponseEntity<Resource> getLocalVolumeFile(@PathVariable(name = "volumeId") Long volumeId,
		@RequestParam(value = "filePath") String filePath) {
		DownloadFileResDTO file = volumeFacadeService.getLocalVolumeFile(volumeId, filePath);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(file.getMediaType());
		return new ResponseEntity<>(file.getByteArrayResource(), headers, HttpStatus.OK);
	}
}
