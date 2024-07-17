package com.xiilab.servercore.label.service;

import java.util.List;

import com.xiilab.servercore.label.dto.LabelDTO;

public interface LabelService {

	LabelDTO.ResponseDTO addLabel(String workspaceResourceName, LabelDTO labelDTO);

	List<LabelDTO.ResponseDTO> getLabels(String workspaceResourceName);

	boolean checkLabel(String workspaceResourceName, String labelName);

	void deleteLabelById(long labelId);

	void modifyLabels(List<LabelDTO.UpdateDTO> updateLabelDTOs);
}
