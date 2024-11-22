package com.xiilab.servercore.workspace.entity;

import com.xiilab.modulecommon.enums.DefaultYN;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	private int workspaceCreateLimit;
	@Enumerated(EnumType.STRING)
	private DefaultYN workloadPendingCreateYN;

	public void updateResource(double cpu, double mem, int gpu, int workspaceCreateLimit, DefaultYN workloadPendingCreateYN) {
		this.cpu = cpu;
		this.mem = mem;
		this.gpu = gpu;
		this.workspaceCreateLimit = workspaceCreateLimit;
		this.workloadPendingCreateYN = workloadPendingCreateYN;
	}
}
