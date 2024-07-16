package com.xiilab.servercore.label.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.LabelErrorCode;
import com.xiilab.modulek8sdb.label.entity.LabelEntity;
import com.xiilab.modulek8sdb.label.repository.LabelRepository;
import com.xiilab.servercore.label.dto.LabelDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LabelServiceImpl implements LabelService {

	private final LabelRepository labelRepository;

	@Override
	@Transactional
	public void addLabel(String workspaceResourceName, LabelDTO labelDTO) {
		// 라벨 이름 중복 체크
		Optional<LabelEntity> findLabel = getLabelByWorkspaceResourceNameAndLabelName(workspaceResourceName,
			labelDTO.getLabelName());
		// 라벨 갯수 조회
		int count = getLabelCountByWorkspaceResourceName(workspaceResourceName);
		// 해당 워크스페이스에 해당 이름의 라벨이 없는경우 생성
		if (findLabel.isEmpty()) {
			try {
				labelRepository.save(labelDTO.convertLabelEntity(workspaceResourceName, count));
			} catch (IllegalArgumentException e) {
				throw new RestApiException(LabelErrorCode.LABEL_SAVE_FAIL);
			}
		} else {
			throw new RestApiException(LabelErrorCode.LABEL_DUPLICATE);
		}
	}

	@Override
	public List<LabelDTO.ResponseDTO> getLabels(String workspaceResourceName) {
		// 해당 워크스페이스의 등록된 라벨 리스트 조회
		List<LabelEntity> getModelLabels = labelRepository.findAllByWorkspaceResourceName(workspaceResourceName);
		return getModelLabels.stream().map(LabelDTO.ResponseDTO::convertLabelDTO).toList();
	}

	@Override
	public boolean checkLabel(String workspaceResourceName, String labelName) {
		// 라벨 중복 체크
		Optional<LabelEntity> findLabel = getLabelByWorkspaceResourceNameAndLabelName(workspaceResourceName, labelName);
		return findLabel.isEmpty();
	}

	@Override
	@Transactional
	public void deleteLabelById(long labelId) {
		// 해당 ID의 라벨 삭제
		if(getLabelEntity(labelId).isPresent()){
			try{
				labelRepository.deleteById(labelId);
			}catch (IllegalArgumentException e){
				throw new RestApiException(LabelErrorCode.LABEL_DELETE_FAIL);
			}
		}else{
			throw new RestApiException(LabelErrorCode.LABEL_NOT_FOUND);
		}
	}

	@Override
	@Transactional
	public void modifyLabels(List<LabelDTO.UpdateDTO> updateLabelDTOs) {
		for(LabelDTO.UpdateDTO updateLabelDTO : updateLabelDTOs){
			Optional<LabelEntity> labelEntity = getLabelEntity(updateLabelDTO.getLabelId());
			labelEntity.ifPresent(
				entity -> entity.updateLabel(updateLabelDTO.getLabelName(), updateLabelDTO.getColorCode(),
					updateLabelDTO.getColorCodeName(), updateLabelDTO.getOrder()));
		}
	}

	private Optional<LabelEntity> getLabelByWorkspaceResourceNameAndLabelName(String workspaceResourceName,
		String labelName) {
		return labelRepository.findByWorkspaceResourceNameAndName(workspaceResourceName, labelName);
	}

	private Optional<LabelEntity> getLabelEntity(long labelId) {
		return labelRepository.findById(labelId);
	}

	private int getLabelCountByWorkspaceResourceName(String workspaceResourceName) {
		return labelRepository.findByWorkspaceResourceNameContaining(workspaceResourceName);
	}
}
