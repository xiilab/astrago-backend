package com.xiilab.servercore.label.service;

import java.util.List;

import com.xiilab.servercore.label.dto.LabelDTO;

public interface LabelService {

	void addLabel(String workspaceResourceName,String labelName, String colorCode);

	List<LabelDTO> getLabels(String workspaceResourceName);

	boolean checkLabel(String workspaceResourceName, String labelName);

	void deleteLabelById(long labelId);
}
