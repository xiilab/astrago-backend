package com.xiilab.servercore.workspace.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceResourceSettingDTO {
	private double cpu;
	private double mem;
	private int gpu;
	private int workspaceCreateLimit;
}
