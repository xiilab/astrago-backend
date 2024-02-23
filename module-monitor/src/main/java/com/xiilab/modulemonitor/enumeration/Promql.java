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
	NODE_COUNT("sum(kube_node_info)","총 노드 개수 조회", "NODE"),
	NODE_MEM_USAGE("label_replace(((node_memory_MemTotal_bytes - node_memory_MemFree_bytes - node_memory_Buffers_bytes - node_memory_Cached_bytes) / node_memory_MemTotal_bytes) * 100,  \"internal_ip\", \"$1\", \"instance\", \"(.*):(.*)\") * on (internal_ip) group_left(node) kube_node_info{%s}","Node 메모리 사용률 조회","NODE"),
	NODE_MEM_USAGE_KI("(node_memory_MemTotal_bytes - (node_memory_MemFree_bytes + node_memory_Buffers_bytes + node_memory_Cached_bytes + node_memory_Slab_bytes)) / 1024", "NODE의 Mem 사용량 조회", "NODE"),
	NODE_DISK_SIZE("max by (mountpoint) (label_replace(node_filesystem_size_bytes{job=\"node-exporter\", fstype!=\"\", mountpoint!=\"\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{%s})","Node의 Disk Size 조회","NODE"),
	NODE_DISK_USAGE_SIZE("max by (mountpoint) (label_replace(node_filesystem_avail_bytes{job=\"node-exporter\", fstype!=\"\", mountpoint!=\"\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{%s})","Node Disk 사용량 조회","NODE"),
	NODE_ROOT_DISK_SIZE("max by (mountpoint, node) (label_replace(node_filesystem_size_bytes{job=\"node-exporter\", fstype!=\"\", mountpoint=\"/\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{%s})","Node의 Disk Size 조회","NODE"),
	NODE_ROOT_DISK_USAGE_SIZE("max by (mountpoint, node) (label_replace(node_filesystem_avail_bytes{job=\"node-exporter\", fstype!=\"\", mountpoint=\"/\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{%s})","Node Disk 사용량 조회","NODE"),
	NODE_DISK_USAGE("(max by (mountpoint) (label_replace(node_filesystem_size_bytes{job=\"node-exporter\", fstype!=\"\", mountpoint=\"/\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{%s})"
		+ "- max by (mountpoint) (label_replace(node_filesystem_avail_bytes{job=\"node-exporter\", fstype!=\"\", mountpoint=\"/\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{%s})) "
		+ "/ max by (mountpoint) (label_replace(node_filesystem_size_bytes{job=\"node-exporter\", fstype!=\"\", mountpoint=\"/\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{%s}) * 100", "Node Disk 사용량", "NODE"),
	TOTAL_NODE_DISK_SIZE_BYTES("sum(node_filesystem_size_bytes{mountpoint=\"/\"})", "전체 노드의 DISK SIZE(Bytes) 조회", "NODE"),
	USAGE_NODE_DISK_SIZE_BYTES("sum(node_filesystem_size_bytes{mountpoint=\"/\"}) - sum(node_filesystem_avail_bytes{mountpoint=\"/\"})", "전체 노드의 DISK 사용중인 Bytes 조회", "NODE"),
	TOTAL_NODE_CPU_CORE("sum(kube_pod_container_resource_requests{resource=\"cpu\",%s})by(node)", "특정 노드의 cpu 코어 수 조회", "NODE"),
	TOTAL_NODE_MEMORY_SIZE("sum(kube_pod_container_resource_requests{resource=\"memory\",%s})by(node)", "특정 노드의 memory size 조회", "NODE"),
	TOTAL_NODE_GPU_COUNT("sum(kube_pod_container_resource_requests{resource=\"nvidia_com_gpu\",%s})by(node)", "특정 노드의 gpu 개수 조회", "NODE"),
	USAGE_NODE_CPU_COUNT("sum(kube_node_status_capacity{resource=\"cpu\",%s})by(node)", "특정 노드의 cpu 사용량 조회", "NODE"),
	USAGE_NODE_MEMORY_COUNT("sum(kube_node_status_capacity{resource=\"memory\",%s})by(node)", "특정 노드의 memory 사용량 조회", "NODE"),
	USAGE_NODE_GPU_COUNT("sum(kube_node_status_capacity{resource=\"nvidia_com_gpu\",%s})by(node)", "특정 노드의 gpu 사용량 조회", "NODE"),
	NODE_READY_PERCENT("count(kube_node_status_condition{status=\"true\", condition=\"Ready\"}) / count(kube_node_info) * 100", "", "" ),
	USAGE_NODE_CPU_CORE("sum(kube_pod_container_resource_requests{resource=\"cpu\",%s})by(node)", "특정 노드의 cpu 코어 수 조회", "NODE"),
	USAGE_NODE_MEMORY_SIZE("sum(kube_pod_container_resource_requests{resource=\"memory\",%s})by(node)", "특정 노드의 memory size 조회", "NODE"),
	USAGE_NODE_GPU_COUNT("sum(kube_pod_container_resource_requests{resource=\"nvidia_com_gpu\",%s})by(node)", "특정 노드의 gpu 개수 조회", "NODE"),
	TOTAL_NODE_CPU_CORE("sum(kube_node_status_capacity{resource=\"cpu\",%s})by(node)", "특정 노드의 cpu 사용량 조회", "NODE"),
	TOTAL_NODE_MEMORY_SIZE("sum(kube_node_status_capacity{resource=\"memory\",%s})by(node)", "특정 노드의 memory 사용량 조회", "NODE"),
	TOTAL_NODE_GPU_COUNT("sum(kube_node_status_capacity{resource=\"nvidia_com_gpu\",%s})by(node)", "특정 노드의 gpu 사용량 조회", "NODE"),


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
	WL_COUNT_BY_WORKSPACE("sum(kube_pod_info{%s}) by(namespace)","WorkSpace별 Workload 개수","Workload"),
	WL_RUNNING_COUNT("sum(kube_pod_status_phase{phase=\"Running\", %s}) by(namespace)","Running 상태 WorkLoad 개수","Workload"),
	WL_PENDING_COUNT("sum(kube_pod_status_phase{phase=\"Pending\", %s}) by(namespace)","Pending 상태 WorkLoad 개수","Workload"),
	WL_DISK_READ_BYTE("container_fs_reads_bytes_total{%s}","Workload Disk 읽기 조회","Workload"),
	WL_DISK_WRITE_BYTE("container_fs_writes_bytes_total{%s}","Workload Disk 쓰기 조회","Workload"),
	WL_NETWORK_RECEIVED("rate(container_network_transmit_packets_total{%s}[1m])","Network 패킷 수신 조회'","Workload"),
	WL_NETWORK_SEND("rate(container_network_receive_packets_total{%s}[1m])","Network 패킷 송신 조회'","Workload"),


	// Daemonset
	DAEMONSET_COUNT("count(kube_daemonset_created)", "데몬셋 총 개수 조회", "DAEMONSET"),
	DAEMONSET_UNHEALYHY_COUNT("sum(kube_daemonset_status_desired_number_scheduled - kube_daemonset_status_number_ready)","데몬셋 unhealthy Count","DAEMONSET"),


	// POD
	POD_COUNT("count(kube_pod_info)", "POD 총 개수 조회", "POD"),
	POD_RUNNING_PERCENT("count(kube_pod_status_phase{phase=\"Running\"} != 0) / count(kube_pod_info) * 100", "실행중인 POD 률 조회", "POD"),
	POD_FAIL_COUNT("count(kube_pod_status_phase{phase=\"Failed\"} != 0)", "POD FAIL Count", "POD"),
	POD_PENDING_COUNT("count(kube_pod_status_phase{phase=\"Pending\"} != 0)", "POD Pending Count", "POD"),
	POD_PENDING("kube_pod_status_phase{phase=\"Pending\"} != 0", "POD Pending 조회", "POD"),
	POD_PENDING_FAIL_INFO("(kube_pod_status_phase{phase=\"Failed\"} != 0 ) or kube_pod_status_phase{phase=\"Pending\"} != 0", "POD Fail, Pending List 조회", "POD"),

	// VOLUME
	VOLUME_COUNT("count(kube_persistentvolume_info)", "Volume 총 개수 조회", "VOLUME"),


	// DEPLOYMENT
	DEPLOYMENT_COUNT("count(kube_deployment_created)", "DEPLOYMENT 총 개수 조회", "DEPLOYMENT"),
	DEPLOYMENT_UNHEALYHY_COUNT("sum(kube_deployment_status_condition{status=\"unknown\"})", "DEPLOYMENT unhealthy Count","DEPLOYMENT"),


	// SERVICE
	SERVICE_COUNT("count(kube_service_info)", "SERVICE 총 개수 조회", "SERVICE"),

	// CONTAINER
	CONTAINER_COUNT("count(kube_pod_container_info)", "CONTAINER 총 개수 조회", "CONTAINER"),


	// NAMESPACE
	NAMESPACE_COUNT("count(kube_namespace_created)", "NAMESPACE 총 개수 조회", "NAMESPACE"),


	// STATEFULSET
	STATEFULSET_COUNT("count(kube_statefulset_created)", "STATEFULSET 총 개수 조회", "STATEFULSET"),
	STATEFULSET_UNHEALYHY_COUNT("count(kube_statefulset_status_replicas_ready == 0)", "STATEFULSET unhealthy count", "STATEFULSET"),

	// HPA
	HPA_COUNT("count(kube_horizontalpodautoscaler_info)", "HPA 총 개수 조회", "HPA"),
	HPA_UNHEALYHY_COUNT("count(kube_horizontalpodautoscaler_status_condition{status=\"unknown\"} != 0)", "HPA unhealthy Count", "HPA"),

	// CONTAINER
	CONTAINER_RESTART_COUNT("sum(kube_pod_container_status_restarts_total)", "Container Restart count", "CONTAINER"),
	CONTAINER_IMAGE_PULL_BACK_OFF_COUNT("count(kube_pod_container_status_waiting_reason{reason=\"ImagePullBackOff\"})", "Container imagePullBackOff Count", "CONTAINER"),
	CONTAINER_RESTART("kube_pod_container_status_restarts_total != 0", "컨테이너 재시작 조회", "CONTAINER"),

	;
// GPU 사용량, GPU Limit, GPU Request
	private final String query;
	private final String description;
	private final String type;
}
