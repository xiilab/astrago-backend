package com.xiilab.modulemonitor.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Promql {
	// GPU
	GPU_TEMP("DCGM_FI_DEV_GPU_TEMP{%s}","GPU 온도 조회","GPU"),
	GPU_FAN_SPEED("DCGM_FI_DEV_FAN_SPEED{%s}","GPU 팬 속도 조회","GPU"),
	GPU_AVG_TEMP("avg(DCGM_FI_DEV_GPU_TEMP{%s})","GPU 평균 온도 조회","GPU"),
	GPU_MEM_TEMP("DCGM_FI_DEV_MEMORY_TEMP{%s}","GPU MEM 온도 조회","GPU"),
	GPU_MEM_AVG_TEMP("avg(DCGM_FI_DEV_MEMORY_TEMP{%s})","GPU MEM 평균 온도 조회","GPU"),
	GPU_COUNT("DCGM_FI_DEV_COUNT{%s}","GPU 개수 조회 및 GPU Name 조회","GPU"),
	GPU_USAGE("DCGM_FI_DEV_GPU_UTIL{%s}", "GPU 사용량 조회", "GPU"),
	GPU_AVG_USAGE("avg(DCGM_FI_DEV_GPU_UTIL{%s})", "GPU 평균 사용량 조회", "GPU"),
	GPU_MEM_USAGE("max_over_time(DCGM_FI_DEV_FB_USED{%s}[1m]) / (max_over_time(DCGM_FI_DEV_FB_USED{%s}[1m]) + min_over_time(DCGM_FI_DEV_FB_FREE{%s}[1m]))", "GPU MEM 사용량 조회", "GPU"),
	GPU_MEM_AVG_USAGE("avg(max_over_time(DCGM_FI_DEV_FB_USED{%s}[1m]) / (max_over_time(DCGM_FI_DEV_FB_USED{%s}[1m]) + min_over_time(DCGM_FI_DEV_FB_FREE{%s}[1m])", "GPU MEM 평균 사용량 조회", "GPU"),
	GPU_POWER_USAGE("DCGM_FI_DEV_POWER_USAGE{%s}","GPU 전력 사용량 조회","GPU"),
	GPU_MEM_CLOCK_SPEED("DCGM_FI_DEV_MEM_CLOCK{%s}","GPU MEW 클럭 속도 조회","GPU"),


	// NODE
	CPU_USAGE("(label_replace(100 - (avg by (instance) (irate(node_cpu_seconds_total{mode=\"idle\"}[1m])) * 100), \"internal_ip\", \"$1\", \"instance\", \"(.*):(.*)\")) * on (internal_ip) group_left(node) kube_node_info{%s}","CPU 사용량 조회","CPU"),
	NODE_INFO("sum(kube_pod_info{%s}) by(node)","노드 정보 조회", "NODE"),
	NODE_COUNT("count(node_uname_info{%s})","총 노드 개수 조회", "NODE"),
	NODE_MEM_USAGE("label_replace(((node_memory_MemTotal_bytes - node_memory_MemFree_bytes - node_memory_Buffers_bytes - node_memory_Cached_bytes) / node_memory_MemTotal_bytes) * 100,  \"internal_ip\", \"$1\", \"instance\", \"(.*):(.*)\") * on (internal_ip) group_left(node) kube_node_info{%s}","Node 메모리 사용률 조회","NODE"),
	NODE_MEM_USAGE_KI("(node_memory_MemTotal_bytes - (node_memory_MemFree_bytes + node_memory_Buffers_bytes + node_memory_Cached_bytes + node_memory_Slab_bytes)) / 1024", "NODE의 Mem 사용량 조회", "NODE"),
	NODE_DISK_SIZE("max by (mountpoint) (label_replace(node_filesystem_size_bytes{job=\"node-exporter\", fstype!=\"\", mountpoint!=\"\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{%s})","Node의 Disk Size 조회","NODE"),
	NODE_DISK_USAGE_SIZE("max by (mountpoint) (label_replace(node_filesystem_avail_bytes{job=\"node-exporter\", fstype!=\"\", mountpoint!=\"\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{%s})","Node Disk 사용량 조회","NODE"),
	NODE_DISK_USAGE("(max by (mountpoint) (label_replace(node_filesystem_size_bytes{job=\"node-exporter\", fstype!=\"\", mountpoint=\"/\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{%s})"
		+ "- max by (mountpoint) (label_replace(node_filesystem_avail_bytes{job=\"node-exporter\", fstype!=\"\", mountpoint=\"/\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{%s})) "
		+ "/ max by (mountpoint) (label_replace(node_filesystem_size_bytes{job=\"node-exporter\", fstype!=\"\", mountpoint=\"/\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{%s}) * 100", "Node Disk 사용량", "NODE"),
	TOTAL_NODE_DISK_SIZE_BYTES("sum(node_filesystem_size_bytes{mountpoint=\"/\"})", "전체 노드의 DISK SIZE(Bytes) 조회", "NODE"),
	USAGE_NODE_DISK_SIZE_BYTES("sum(node_filesystem_size_bytes{mountpoint=\"/\"}) - sum(node_filesystem_avail_bytes{mountpoint=\"/\"})", "전체 노드의 DISK 사용중인 Bytes 조회", "NODE"),


	// WORK SPACE
	WS_GPU_QUOTA("kube_resourcequota{type=\"hard\", resource=~\"request.*gpu\", %s}","WorkSpace GPU 할당량 조회", "WorkSpace"),
	WS_GPU_USAGE("kube_resourcequota{type=\"used\", resource=~\"request.*gpu\", %s}","WorkSpace GPU 사용량 조회","WorkSpace"),
	WS_CPU_QUOTA("kube_resourcequota{type=\"hard\", resource=~\"requests.*cpu\", %s}","WorkSpace CPU 할당량 조회","WorkSpace"),
	WS_CPU_USAGE("kube_resourcequota{type=\"used\", resource=~\"requests.*cpu\", %s}","WorkSpace CPU 사용량 조회","WorkSpace"),
	WS_MEM_QUOTA("kube_resourcequota{type=\"hard\", resource=~\"requests.*memory\", %s}","WorkSpace MEM 할당량 조회","WorkSpace"),
	WS_MEM_USAGE("kube_resourcequota{type=\"used\", resource=~\"requests.*memory\", %s}","WorkSpace MEM 사용량 조회","WorkSpace"),
	WS_PHASE_PENDING_COUNT("kube_pod_status_phase{phase=\"Pending\", namespace != \"\"} * on (namespace) group_left kube_namespace_status_phase{}", "", ""),
	WS_VOLUME_COUNT("sum(pv_collector_bound_pvc_count{%s}) by(namespace)","Workspace에 생성된 Volum 개수","WorkSpace"),


	// WORK LOAD
	WL_COUNT_BY_WORKSPACE("sum(kube_pod_info{%s}) by(namespace)","WorkSpace별 Workload 개수","WorkSpace"),
	WL_RUNNING_COUNT("sum(kube_pod_status_phase{phase=\"Running\", %s}) by(namespace)","Running 상태 WorkLoad 개수","WorkSpace"),
	WL_PENDING_COUNT("sum(kube_pod_status_phase{phase=\"Pending\", %s}) by(namespace)","Pending 상태 WorkLoad 개수","WorkSpace"),
	WL_DISK_READ_BYTE("container_fs_reads_bytes_total{%s}","Workload Disk 읽기 조회","Workload"),
	WL_DISK_WRITE_BYTE("container_fs_writes_bytes_total{%s}","Workload Disk 쓰기 조회","Workload"),
	WL_NETWORK_RECEIVED("rate(container_network_transmit_packets_total{%s}[1m])","Network 패킷 수신 조회'","Workload"),
	WL_NETWORK_SEND("rate(container_network_receive_packets_total{%s}[1m])","Network 패킷 송신 조회'","Workload"),



	;
// GPU 사용량, GPU Limit, GPU Request
	private final String query;
	private final String description;
	private final String type;
}
