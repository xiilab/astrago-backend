package com.xiilab.modulemonitor.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Promql {
	GPU_TEMP("DCGM_FI_DEV_GPU_TEMP","GPU 온도 조회","GPU"),
	GPU_MEM_TEMP("DCGM_FI_DEV_MEMORY_TEMP","GPU MEM 온도 조회","GPU"),
	GPU_DEV_NAME("DCGM_FI_DEV_NAME","GPU 장치 이름 조회","GPU"),
	GPU_COUNT("DCGM_FI_DEV_COUNT","GPU 개수 조회","GPU"),
	GPU_USAGE("DCGM_FI_DEV_GPU_UTIL", "GPU 사용량 조회", "GPU"),
	GPU_MEM_USAGE("(max_over_time(DCGM_FI_DEV_FB_USED[1m]) / (max_over_time(DCGM_FI_DEV_FB_USED[1m]) + min_over_time(DCGM_FI_DEV_FB_FREE[1m])", "GPU MEM 사용량 조회", "GPU"),
	GPU_POWER_USAGE("DCGM_FI_DEV_TOTAL_ENERGY_CONSUMPTION","GPU 전력 소비량 조회","GPU"),
	GPU_MEM_CLOCK_SPEED("DCGM_FI_DEV_MEM_CLOCK","GPU MEW 클럭 속도 조회","GPU"),
	NODE_INFO("sum(kube_pod_info{node!=\"\"}) by(node)","노드 정보 조회", "NODE"),
	NODE_COUNT("count(node_uname_info)","총 노드 개수 조회", "NODE"),
	WS_GPU_QUOTA("kube_resourcequota{type=\"hard\", resource=~\"request.*gpu\"}","WorkSpace GPU 할당량 조회", "WorkSpace"),
	WS_GPU_USAGE("kube_resourcequota{type=\"used\", resource=~\"request.*gpu\"}","WorkSpace GPU 사용량 조회","WorkSpace"),
	WS_CPU_QUOTA("kube_resourcequota{type=\"hard\", resource=~\"requests.*cpu\"}","WorkSpace CPU 할당량 조회","WorkSpace"),
	WS_CPU_USAGE("kube_resourcequota{type=\"used\", resource=~\"requests.*cpu\"}","WorkSpace CPU 사용량 조회","WorkSpace"),
	WS_MEM_QUOTA("kube_resourcequota{type=\"hard\", resource=~\"requests.*memory\"}","WorkSpace MEM 할당량 조회","WorkSpace"),
	WS_MEM_USAGE("kube_resourcequota{type=\"used\", resource=~\"requests.*memory\"}","WorkSpace MEM 사용량 조회","WorkSpace"),
	WL_COUNT_BY_WORKSPACE("sum(kube_pod_info{}) by(namespace)","WorkSpace별 Workload 개수","WorkSpace"),
	WL_RUNNING_COUNT("sum(kube_pod_status_phase{phase=\"Running\", %s}) by(namespace)","Running 상태 WorkLoad 개수","WorkSpace"),
	WL_PANDING_COUNT("sum(kube_pod_status_phase{phase=\"Pending\", %s}) by(namespace)","Pending 상태 WorkLoad 개수","WorkSpace"),
	WS_VOLUM_COUNT("sum(pv_collector_bound_pvc_count) by(namespace)","Workspace에 생성된 Volum 개수","WorkSpace"),

	// 워크로드는 K8s에서 처리



	;

	private final String query;
	private final String description;
	private final String type;

}
