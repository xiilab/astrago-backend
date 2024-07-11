package com.xiilab.servercore.label.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.ModelRepoErrorCode;
import com.xiilab.modulek8sdb.modelrepo.entity.LabelEntity;
import com.xiilab.modulek8sdb.modelrepo.repository.LabelRepository;
import com.xiilab.servercore.label.dto.LabelDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LabelServiceImpl implements LabelService {

	private final LabelRepository labelRepository;

	@Override
	@Transactional
	public void addLabel(String workspaceResourceName, String labelName, String colorCode) {
		// 라벨 이름 중복 체크
		Optional<LabelEntity> findLabel = getLabelByWorkspaceResourceNameAndLabelName(workspaceResourceName, labelName);
		if (findLabel.isEmpty()) {
			try {
				labelRepository.save(LabelEntity.builder()
					.name(labelName)
					.workspaceResourceName(workspaceResourceName)
					.colorCode(colorCode)
					.build());
			} catch (IllegalArgumentException e) {
				throw new RestApiException(ModelRepoErrorCode.LABEL_SAVE_FAIL);
			}
		} else {
			throw new RestApiException(ModelRepoErrorCode.LABEL_DUPLICATE);
		}
	}

	@Override
	public List<LabelDTO> getLabels(String workspaceResourceName) {
		List<LabelEntity> getModelLabels = labelRepository.findAllByWorkspaceResourceName(
			workspaceResourceName);
		return getModelLabels.stream().map(LabelDTO::convertLabelDTO).toList();
	}

	@Override
	public boolean checkLabel(String workspaceResourceName, String labelName) {
		Optional<LabelEntity> findLabel = getLabelByWorkspaceResourceNameAndLabelName(workspaceResourceName, labelName);
		return findLabel.isEmpty();
	}

	@Override
	@Transactional
	public void deleteLabelById(long id) {
		labelRepository.deleteById(id);
	}

	private Optional<LabelEntity> getLabelByWorkspaceResourceNameAndLabelName(String workspaceResourceName,
		String labelName) {
		return labelRepository.findByWorkspaceResourceNameAndName(workspaceResourceName, labelName);
	}
}
