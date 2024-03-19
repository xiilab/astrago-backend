package com.xiilab.servercore.workspace.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "TB_WORKSPACE_SETTING")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WorkspaceSettingEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private double cpu;
	private double mem;
	private int gpu;

	public void updateResource(double cpu, double mem, int gpu) {
		this.cpu = cpu;
		this.mem = mem;
		this.gpu = gpu;
	}
}
