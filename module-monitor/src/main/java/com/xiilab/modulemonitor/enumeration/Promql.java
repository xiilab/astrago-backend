package com.xiilab.modulemonitor.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Promql {
	// GPU
	GPU_TEMP("avg(DCGM_FI_DEV_GPU_TEMP{%s}) by(gpu, kubernetes_node, modelName)","GPU 온도 조회","GPU"),
	GPU_FAN_SPEED("avg(DCGM_FI_DEV_FAN_SPEED{%s}) by(gpu, kubernetes_node, modelName)","GPU 팬 속도 조회","GPU"),
	GPU_AVG_TEMP("avg(DCGM_FI_DEV_GPU_TEMP{%s})","GPU 평균 온도 조회","GPU"),
	GPU_MEM_TEMP("avg(DCGM_FI_DEV_MEMORY_TEMP{%s})","GPU MEM 온도 조회","GPU"),
	GPU_MEM_AVG_TEMP("avg(DCGM_FI_DEV_MEMORY_TEMP{%s})","GPU MEM 평균 온도 조회","GPU"),
	GPU_COUNT("DCGM_FI_DEV_COUNT{%s}","GPU 개수 조회 및 GPU Name 조회","GPU"),
	GPU_USAGE("avg(DCGM_FI_DEV_GPU_UTIL{%s}) by(gpu, kubernetes_node, modelName)", "GPU 사용률 조회", "GPU"),
	GPU_AVG_USAGE("avg(DCGM_FI_DEV_GPU_UTIL{%s})", "GPU 평균 사용량 조회", "GPU"),
	GPU_MEM_USAGE("avg(max_over_time(DCGM_FI_DEV_FB_USED{%1$s}[1m])) by(gpu, kubernetes_node, modelName) / (avg(max_over_time(DCGM_FI_DEV_FB_USED{%1$s}[1m])) by(gpu, kubernetes_node, modelName) + avg(max_over_time(DCGM_FI_DEV_FB_FREE{%1$s}[1m])) by(gpu, kubernetes_node, modelName))", "GPU MEM 사용률 조회", "GPU"),
	GPU_MEM_AVG_USAGE("avg(max_over_time(DCGM_FI_DEV_FB_USED{%s}[1m]) / (max_over_time(DCGM_FI_DEV_FB_USED{%s}[1m]) + min_over_time(DCGM_FI_DEV_FB_FREE{%s}[1m])", "GPU MEM 평균 사용량 조회", "GPU"),
	GPU_POWER_USAGE("avg(DCGM_FI_DEV_POWER_USAGE{%s}) by(gpu, kubernetes_node, modelName)","GPU 전력 사용률 조회","GPU"),
	GPU_AVG_POWER_USAGE("DCGM_FI_DEV_POWER_USAGE{%s}","GPU 전력 사용량 조회","GPU"),
	GPU_MEM_CLOCK_SPEED("DCGM_FI_DEV_MEM_CLOCK{%s}","GPU MEW 클럭 속도 조회","GPU"),

	// NODE
	NODE_CPU_USAGE("(label_replace(100 - (avg by (instance) (irate(node_cpu_seconds_total{mode=\"idle\"}[1m])) * 100), \"internal_ip\", \"$1\", \"instance\", \"(.*):(.*)\")) * on (internal_ip) group_left(node) kube_node_info{%s}","CPU 사용량 조회","NODE"),
	NODE_INFO("sum(kube_pod_info{%s}) by(node)","노드 정보 조회", "NODE"),
	NODE_COUNT("sum(kube_node_info)","총 노드 개수 조회", "NODE"),
	NODE_MEM_USAGE("label_replace(((node_memory_MemTotal_bytes - node_memory_MemFree_bytes - node_memory_Buffers_bytes - node_memory_Cached_bytes) / node_memory_MemTotal_bytes) * 100,  \"internal_ip\", \"$1\", \"instance\", \"(.*):(.*)\") * on (internal_ip) group_left(node) kube_node_info{%s}","Node 메모리 사용률 조회","NODE"),
	NODE_MEM_USAGE_KI("sum((node_memory_MemTotal_bytes - (node_memory_MemFree_bytes + node_memory_Buffers_bytes + node_memory_Cached_bytes + node_memory_Slab_bytes)) / 1024)", "NODE의 Mem 사용량 조회", "NODE"),
	NODE_DISK_SIZE("max by (mountpoint) (label_replace(node_filesystem_size_bytes{job=\"node-exporter\", fstype!=\"\", mountpoint!=\"\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{%s})","Node의 Disk Size 조회","NODE"),
	NODE_DISK_USAGE_SIZE("max by (mountpoint) (label_replace(node_filesystem_avail_bytes{job=\"node-exporter\", fstype!=\"\", mountpoint!=\"\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{%s})","Node Disk 사용량 조회","NODE"),
	NODE_ROOT_DISK_SIZE("max by (mountpoint, node) (label_replace(node_filesystem_size_bytes{job=\"node-exporter\", fstype!=\"\", mountpoint=\"/\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{%s})","Node의 Disk Size 조회","NODE"),
	NODE_ROOT_DISK_USAGE_SIZE("max by (mountpoint, node) (label_replace(node_filesystem_avail_bytes{job=\"node-exporter\", fstype!=\"\", mountpoint=\"/\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{%s})","Node Disk 사용량 조회","NODE"),
	NODE_DISK_USAGE("(max by (mountpoint) (label_replace(node_filesystem_size_bytes{job=\"node-exporter\", fstype!=\"\", mountpoint=\"/\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{%1$s})"
		+ "- max by (mountpoint) (label_replace(node_filesystem_avail_bytes{job=\"node-exporter\", fstype!=\"\", mountpoint=\"/\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{%1$s})) "
		+ "/ max by (mountpoint) (label_replace(node_filesystem_size_bytes{job=\"node-exporter\", fstype!=\"\", mountpoint=\"/\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{%1$s}) * 100", "Node Disk 사용량", "NODE"),
	TOTAL_NODE_DISK_SIZE_BYTES("sum(node_filesystem_size_bytes{mountpoint=\"/\"})", "전체 노드의 DISK SIZE(Bytes) 조회", "NODE"),
	USAGE_NODE_DISK_SIZE_BYTES("sum(node_filesystem_size_bytes{mountpoint=\"/\"}) - sum(node_filesystem_avail_bytes{mountpoint=\"/\"})", "전체 노드의 DISK 사용중인 Bytes 조회", "NODE"),
	NODE_LOAD1("label_replace((avg(node_load1) by(nodename, job, instance, container,service)), \"internal_ip\",\"$1\",\"instance\",\"(.*):(.*)\") * on (internal_ip) group_left(node) kube_node_info{%s}","1분 단위 GPU 평균 부하","NODE"),
	NODE_LOAD5("label_replace((avg(node_load5) by(nodename, job, instance, container,service)), \"internal_ip\",\"$1\",\"instance\",\"(.*):(.*)\") * on (internal_ip) group_left(node) kube_node_info{%s}","5분 단위 GPU 평균 부하","NODE"),
	NODE_LOAD15("label_replace((avg(node_load15) by(nodename, job, instance, container,service)), \"internal_ip\",\"$1\",\"instance\",\"(.*):(.*)\") * on (internal_ip) group_left(node) kube_node_info{%s}","15분 단위 GPU 평균 부하","NODE"),
	USAGE_NODE_CPU_CORE("sum(kube_pod_container_resource_requests{resource=\"cpu\",%s})by(node)", "특정 노드의 cpu 코어 수 조회", "NODE"),
	USAGE_NODE_MEMORY_SIZE("sum(kube_pod_container_resource_requests{resource=\"memory\",%s})by(node)", "특정 노드의 memory size 조회", "NODE"),
	USAGE_NODE_GPU_COUNT("sum(kube_pod_container_resource_requests{resource=\"nvidia_com_gpu\",%s})by(node)", "특정 노드의 gpu 개수 조회", "NODE"),
	TOTAL_NODE_CPU_CORE("sum(kube_node_status_capacity{resource=\"cpu\",%s})by(node)", "특정 노드의 cpu 사용량 조회", "NODE"),
	TOTAL_NODE_MEMORY_SIZE("sum(kube_node_status_capacity{resource=\"memory\",%s})by(node)", "특정 노드의 memory 사용량 조회", "NODE"),
	TOTAL_NODE_GPU_COUNT("sum(kube_node_status_capacity{resource=\"nvidia_com_gpu\",%s})by(node)", "특정 노드의 gpu 사용량 조회", "NODE"),
	USAGE_NODE_CPU_COUNT("sum(kube_node_status_capacity{resource=\"cpu\",%s})by(node)", "특정 노드의 cpu 사용량 조회", "NODE"),
	USAGE_NODE_MEMORY_COUNT("sum(kube_node_status_capacity{resource=\"memory\",%s})by(node)", "특정 노드의 memory 사용량 조회", "NODE"),
	USAGE_NODE_GPU_USAGE("sum(kube_node_status_capacity{resource=\"nvidia_com_gpu\",%s})by(node)", "특정 노드의 gpu 사용량 조회", "NODE"),
	NODE_READY_PERCENT("sum(kube_node_status_condition{status=\"true\", condition=\"Ready\"} > 0) / count(kube_node_info) * 100", "", "" ),
	NODE_CPU_TEMP("label_replace(avg(node_hwmon_temp_celsius) by(instance),  \"internal_ip\", \"$1\", \"instance\", \"(.*):(.*)\") * on (internal_ip) group_left(node) kube_node_info{%s}", "노드 CPU TEMP 조회", "NODE"),
	NODE_NETWORK_RECEIVE("avg by(instance) (label_replace(node_network_receive_bytes_total, \"internal_ip\", \"$1\", \"instance\",\"(.*):(.*)\") * on(internal_ip) group_left(node) kube_node_info{%s})", "노드의 네트워크 수신", "NODE"),
	NODE_NETWORK_TRANSMIT("avg by(instance) (label_replace(node_network_transmit_bytes_total, \"internal_ip\", \"$1\", \"instance\", \"(.*):(.*)\") * on(internal_ip) group_left(node) kube_node_info{%s})","노드의 네트워크 송신","NODE"),
	TOTAL_NODE_REQUEST_RESOURCE("sum(kube_pod_container_resource_requests{%s})by(resource)", "특정 노드의 cpuRequest 리소스 사용량 조회", "NODE"),
	TOTAL_NODE_LIMIT_RESOURCE("sum(kube_pod_container_resource_limits{%s})by(resource)", "특정 노드의 limit 리소스 사용량 조회", "NODE"),
	NODE_DISK_READ("label_replace(avg(node_disk_read_bytes_total) by(instance), \"internal_ip\",\"$1\",\"instance\",\"(.*):(.*)\") * on(internal_ip) group_right kube_node_info{%s}", "", "NODE"),
	NODE_DISK_WRITTEN("label_replace(avg(node_disk_written_bytes_total) by(instance), \"internal_ip\",\"$1\",\"instance\",\"(.*):(.*)\") * on(internal_ip) group_right kube_node_info{%s}", "", "NODE"),
	NODE_MEMORY_BUFFERS("label_replace(node_memory_Buffers_bytes, \"internal_ip\",\"$1\",\"instance\",\"(.*):(.*)\") * on(internal_ip) group_right kube_node_info{%s}", "NODE MEM Buffer 조회", "NODE"),
	NODE_MEMORY_CACHED("label_replace(node_memory_Cached_bytes, \"internal_ip\",\"$1\",\"instance\",\"(.*):(.*)\") * on(internal_ip) group_right kube_node_info{%s}", "NODE MEM Cached 조회", "NODE"),
	NODE_MEMORY_MEM_TOTAL("label_replace(node_memory_MemTotal_bytes, \"internal_ip\",\"$1\",\"instance\",\"(.*):(.*)\") * on(internal_ip) group_right kube_node_info{%s}", "NODE MEM Total 조회", "NODE"),
	NODE_MEMORY_MEM_FREE("label_replace(node_memory_MemFree_bytes, \"internal_ip\",\"$1\",\"instance\",\"(.*):(.*)\") * on(internal_ip) group_right kube_node_info{%s}", "NDOE MEM Free 조회", "NODE"),
	NODE_CPU_TOTAL_BY_NODE_NAME("sum(kube_node_status_capacity{resource=\"cpu\", %s})", "", "NODE"),
	NODE_TOTAL_DISK_SIZE_BYTE("label_replace(node_filesystem_size_bytes{mountpoint=\"/\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{%s}", "", "NODE"),
	NODE_USAGE_DISK_SIZE_BYTE("label_replace(node_filesystem_size_bytes{mountpoint=\"/\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{%1$s} - label_replace(node_filesystem_avail_bytes{mountpoint=\"/\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{%1$s}","","NODE"),



	// WORK SPACE
	WS_GPU_QUOTA("kube_resourcequota{type=\"hard\", resource=~\"cpuRequest.*gpu\", %s}","WorkSpace GPU 할당량 조회", "WorkSpace"),
	WS_GPU_USAGE("kube_resourcequota{type=\"used\", resource=~\"requests.nvidia.com/gpu\", %s}","WorkSpace GPU 사용량 조회","WorkSpace"),
	WS_CPU_QUOTA("kube_resourcequota{type=\"hard\", resource=~\"requests.*cpu\", %s}","WorkSpace CPU 할당량 조회","WorkSpace"),
	WS_CPU_USAGE("kube_resourcequota{type=\"used\", resource=~\"requests.*cpu\", %s}","WorkSpace CPU 사용량 조회","WorkSpace"),
	WS_MEM_QUOTA("kube_resourcequota{type=\"hard\", resource=~\"requests.*memory\", %s}","WorkSpace MEM 할당량 조회","WorkSpace"),
	WS_MEM_USAGE("kube_resourcequota{type=\"used\", resource=~\"requests.*memory\", %s}","WorkSpace MEM 사용량 조회","WorkSpace"),
	WS_PHASE_PENDING_COUNT("kube_pod_status_phase{phase=\"Pending\", namespace != \"\"} * on (namespace) group_left kube_namespace_status_phase{}", "", ""),
	WS_VOLUME_COUNT("sum(pv_collector_bound_pvc_count{%s}) by(namespace)","Workspace에 생성된 Volum 개수","WorkSpace"),


	// WORK LOAD
	WL_COUNT_BY_WORKSPACE("sum(kube_pod_info{%s}) by(namespace)","WorkSpace별 Workload 개수","WorkSpace"),
	WL_RUNNING_COUNT("count(kube_pod_status_phase{phase=\"Running\", %s}) by(namespace)","Running 상태 WorkLoad 개수","WorkSpace"),
	WL_PENDING_COUNT("count(kube_pod_status_phase{phase=\"Pending\", %s}) by(namespace)","Pending 상태 WorkLoad 개수","WorkSpace"),
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
	POD_MEM_USAGE_BYTE("container_memory_working_set_bytes{container != \"\", %s}", "POD Mem 사용량 조회 ", "POD"),
	POD_CPU_USAGE_BYTE("avg(container_cpu_usage_seconds_total{container != \"\", %s}) by(pod)", "POD CPU 사용량 조회 ", "POD"),
	POD_GPU_UTIL("DCGM_FI_DEV_GPU_UTIL{%s}","POD GPU 사용률 조회","POD"),
	POD_DISK_USAGE("container_fs_usage_bytes{}","POD DISK 사용량 조회","POD"),

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

	// RESOURCE OPTIMIZATION
	// CPU
	RESOURCE_OPTIMIZATION_CPU("sum(max_over_time(node_namespace_pod_container:container_cpu_usage_seconds_total:sum_irate{pod =~ \"wl-.*\"}[%sh])) by (namespace, pod) > %s and on (namespace, pod) (kube_pod_created < %s)","n시간 동안 최대 CPU 사용량이 일정 수준을 넘은 워크로드 조회","CPU"),
	// MEM
	RESOURCE_OPTIMIZATION_MEM("((sum(max_over_time(container_memory_working_set_bytes{pod=~\"wl-.*\"}[%1$sh])) by(namespace, pod)/sum(max_over_time(kube_pod_container_resource_limits{pod=~\"wl-.*\", resource = \"memory\"}[%1$sh])) by (namespace, pod) != 0)* 100) > %2$s and on (pod,namespace) (kube_pod_created < %3$s)","n시간 동안 최대 MEM 사용량이 일정 수준을 넘은 워크로드 조회","MEM"),
	// GPU
	RESOURCE_OPTIMIZATION_GPU("(max_over_time(DCGM_FI_DEV_GPU_UTIL{pod=~\"wl-.*\"}[%sh]) > %s and on (namespace, pod) (kube_pod_created < %s)) > %s","n시간 동안 최대 GPU 사용량이 일정 수준을 넘은 워크로드 조회","GPU"),


	// REPORT
	REPORT_CLUSTER_GPU_UTIL("round(avg(DCGM_FI_DEV_GPU_UTIL))","GPU 사용률","REPORT"),
	REPORT_CLUSTER_CPU_UTIL("round(100 - (avg(irate(node_cpu_seconds_total{mode=\"idle\"}[1m])) * 100))","CPU 사용률","REPORT"),
	REPORT_CLUSTER_MEM_UTIL("round(avg(((node_memory_MemTotal_bytes - node_memory_MemFree_bytes - node_memory_Buffers_bytes - node_memory_Cached_bytes) / node_memory_MemTotal_bytes) * 100))","MEM 사용률","REPORT"),
	REPORT_GPU_RESOURCE_TOTAL("round(sum(kube_node_status_capacity{resource=\"nvidia_com_gpu\"}))", "GPU 총량", "REPORT"),
	REPORT_GPU_RESOURCE_REQUEST("round(avg(kube_resourcequota{resource=\"requests.nvidia.com/gpu\", type=\"hard\"}))", "GPU 요청량", "REPORT"),
	REPORT_GPU_RESOURCE_USAGE("round(avg(DCGM_FI_DEV_GPU_UTIL{}))", "GPU 사용량", "REPORT"),
	REPORT_CPU_RESOURCE_TOTAL("round(sum(kube_node_status_capacity{resource=\"cpu\"}))", "CPU 총량", "REPORT"),
	REPORT_CPU_RESOURCE_REQUEST("round(avg(kube_resourcequota{resource=\"requests.cpu\", type=\"hard\"}))", "CPU 요청량", "REPORT"),
	REPORT_CPU_RESOURCE_USAGE("round(round(sum(kube_node_status_capacity{resource=\"cpu\"})) * (round(100 - (avg(irate(node_cpu_seconds_total{mode=\"idle\"}[1m])) * 100)) / 100))", "CPU 사용량", "REPORT"),
	REPORT_MEM_RESOURCE_TOTAL("round(sum(kube_node_status_capacity{resource=\"memory\"}))", "MEM 총량", "REPORT"),
	REPORT_MEM_RESOURCE_REQUEST("round(avg(kube_resourcequota{resource=\"requests.memory\", type=\"hard\"}))", "MEM 요청량", "REPORT"),
	REPORT_MEM_RESOURCE_USAGE("round(round(sum(kube_node_status_capacity{resource=\"memory\"})) * avg(((node_memory_MemTotal_bytes - node_memory_MemFree_bytes - node_memory_Buffers_bytes - node_memory_Cached_bytes) / node_memory_MemTotal_bytes)))", "MEM 사용량", "REPORT"),
	REPORT_TOTAL_SCORE("round((avg(DCGM_FI_DEV_GPU_UTIL) * 0.4) * ((100 - (avg(irate(node_cpu_seconds_total{mode=\"idle\"}[1m])) * 100)) * 0.3) * (avg(((node_memory_MemTotal_bytes - node_memory_MemFree_bytes - node_memory_Buffers_bytes - node_memory_Cached_bytes) / node_memory_MemTotal_bytes) * 100) * 0.3))", "REPORT 활용 점수", "REPORT"),
	REPORT_STATISTICS_GPU_REQUEST("avg(kube_resourcequota{resource=\"requests.nvidia.com/gpu\", type=\"hard\"}) by(namespace)", "워크스페이스별 리소스 활용 통계 GPU 요청량", "REPORT"),
	REPORT_STATISTICS_CPU_REQUEST("avg(kube_resourcequota{resource=\"requests.cpu\", type=\"hard\"}) by(namespace)", "워크스페이스별 리소스 활용 통계 CPU 요청량", "REPORT"),
	REPORT_STATISTICS_MEM_REQUEST("avg(kube_resourcequota{resource=\"requests.memory\", type=\"hard\"}) by(namespace)", "워크스페이스별 리소스 활용 통계 MEM 요청량", "REPORT"),
	REPORT_SYSTEM_NODE_INFO("sum(kube_node_info) by(node, internal_ip)", "", ""),
	REPORT_SYSTEM_INFO_CPU("avg(kube_node_status_capacity{resource=\"cpu\"}) by(node, instance)", "", ""),
	REPORT_SYSTEM_INFO_MEM("avg(kube_node_status_capacity{resource=\"memory\"}) by(node, instance)", "", ""),
	REPORT_SYSTEM_INFO_DISK ("max by (node) (label_replace(node_filesystem_size_bytes{job=\"node-exporter\", fstype!=\"\", mountpoint!=\"\", mountpoint=\"/\"}, \"internal_ip\", \"$1\", \"instance\", \"(.*):.*\") * on(internal_ip) group_left(node) kube_node_info{})", "", ""),
	REPORT_SYSTEM_INFO_GPU("avg(kube_node_status_capacity{resource=\"nvidia_com_gpu\"}) by(node, instance)", "", ""),
	REPORT_SYSTEM_INFO_OS("label_replace(node_os_info,  \"internal_ip\", \"$1\", \"instance\", \"(.*):(.*)\") * on (internal_ip) group_left(node) kube_node_info{}", "", ""),
	REPORT_SYSTEM_AVG_CPU("label_replace(100 * (1 - avg by (instance)(rate(node_cpu_seconds_total{mode=\"idle\"}[1m]))), \"internal_ip\", \"$1\", \"instance\", \"(.*):(.*)\") * on (internal_ip) group_left(node) kube_node_info{}", "", ""),
	REPORT_SYSTEM_MAX_CPU("label_replace(100 * (1 - min by (instance)(rate(node_cpu_seconds_total{mode=\"idle\"}[1m]))), \"internal_ip\", \"$1\", \"instance\", \"(.*):(.*)\") * on (internal_ip) group_left(node) kube_node_info{}", "", ""),
	REPORT_SYSTEM_AVG_MEM("label_replace(((node_memory_MemTotal_bytes - node_memory_MemFree_bytes - node_memory_Buffers_bytes - node_memory_Cached_bytes) / node_memory_MemTotal_bytes) * 100,  \"internal_ip\", \"$1\", \"instance\", \"(.*):(.*)\") * on (internal_ip) group_left(node) kube_node_info{}", "", ""),
	REPORT_SYSTEM_MAX_MEM("max(label_replace(((node_memory_MemTotal_bytes - node_memory_MemFree_bytes - node_memory_Buffers_bytes - node_memory_Cached_bytes) / node_memory_MemTotal_bytes) * 100,  \"internal_ip\", \"$1\", \"instance\", \"(.*):(.*)\") * on (internal_ip) group_left(node) kube_node_info{}) by(node)", "", ""),
	REPORT_SYSTEM_DISK_USAGE_90("label_replace((node_filesystem_size_bytes{mountpoint=\"/\"} - node_filesystem_avail_bytes{mountpoint=\"/\"}) / (node_filesystem_size_bytes{mountpoint=\"/\"}) * 100,  \"internal_ip\", \"$1\", \"instance\", \"(.*):(.*)\") * on (internal_ip) group_left(node) kube_node_info{} > 90","",""),
	REPORT_SYSTEM_DISK_USAGE("label_replace((node_filesystem_size_bytes{mountpoint=\"/\"} - node_filesystem_avail_bytes{mountpoint=\"/\"}) / (node_filesystem_size_bytes{mountpoint=\"/\"}) * 100,  \"internal_ip\", \"$1\", \"instance\", \"(.*):(.*)\") * on (internal_ip) group_left(node) kube_node_info{}","",""),
	REPORT_SYSTEM_INFO_GPU_MODEL("avg(label_replace(DCGM_FI_DEV_GPU_TEMP, \"node\", \"$1\", \"kubernetes_node\", \"(.*)\") * on(node) group_left kube_node_info{}) by(node, modelName)", "", ""),
	REPORT_SYSTEM_AVG_GPU_TEMP("avg(DCGM_FI_DEV_GPU_TEMP) by(kubernetes_node, gpu)","",""),
	REPORT_SYSTEM_MAX_GPU_TEMP("max(DCGM_FI_DEV_GPU_TEMP) by(kubernetes_node, gpu)","",""),
	REPORT_SYSTEM_AVG_GPU_USAGE("avg(DCGM_FI_DEV_GPU_UTIL{}) by(gpu, kubernetes_node, modelName)", "", ""),
	REPORT_SYSTEM_MAX_GPU_USAGE("max(DCGM_FI_DEV_GPU_UTIL{}) by(gpu, kubernetes_node, modelName)", "", ""),
	REPORT_SYSTEM_AVG_GPU_MEM("avg(max_over_time(DCGM_FI_DEV_FB_USED{}[1m])) by(gpu, kubernetes_node, modelName) / (avg(max_over_time(DCGM_FI_DEV_FB_USED{}[1m])) by(gpu, kubernetes_node, modelName) + avg(max_over_time(DCGM_FI_DEV_FB_FREE{}[1m])) by(gpu, kubernetes_node, modelName))", "", ""),
	REPORT_SYSTEM_MAX_GPU_MEM("max(max_over_time(DCGM_FI_DEV_FB_USED{}[1m])) by(gpu, kubernetes_node, modelName) / (max(max_over_time(DCGM_FI_DEV_FB_USED{}[1m])) by(gpu, kubernetes_node, modelName) + max(max_over_time(DCGM_FI_DEV_FB_FREE{}[1m])) by(gpu, kubernetes_node, modelName))", "", ""),
	// DASH BOARD
	DASHBOARD_WS_GPU_USAGE("round(((sum(kube_resourcequota{type=\"used\", resource=~\"requests.nvidia.com/gpu\"}) by(namespace)) / sum(kube_resourcequota{type=\"hard\", resource=~\"requests.nvidia.com/gpu\"} > 0) by(namespace)) * 100, 0.01)", "", "DASHBOARD"),
	DASHBOARD_WS_CPU_USAGE("round(((sum(kube_resourcequota{type=\"used\", resource=~\"requests.cpu\"}) by(namespace)) / sum(kube_resourcequota{type=\"hard\", resource=~\"requests.cpu\"} > 0) by(namespace)) * 100 , 0.01)", "", "DASHBOARD"),
	DASHBOARD_WS_MEM_USAGE("round(((sum(kube_resourcequota{type=\"used\", resource=~\"requests.memory\"}) by(namespace)) / sum(kube_resourcequota{type=\"hard\", resource=~\"requests.memory\"} > 0) by(namespace)) * 100, 0.01)", "", "DASHBOARD"),
	DASHBOARD_WS_PENDING_COUNT("count(kube_pod_status_phase{phase=\"Pending\", namespace =~ \"ws.*\"} > 0 and kube_pod_container_status_waiting_reason{namespace =~ \"ws.*\"} < 0) by(namespace)", "", "DASHBOARD"),
	DASHBOARD_WS_ERROR_COUNT("count(kube_pod_container_status_waiting_reason{namespace =~ \"ws.*\"} > 0) by(namespace) or count(kube_pod_status_reason{reason != \"\", namespace =~ \"ws.*\"} > 0) by(namespace)", "", "DASHBOARD"),
	DASHBOARD_WS_RUNNING_COUNT("count(kube_pod_status_phase{phase=\"Running\", namespace =~ \"ws.*\"} > 0) by(namespace)", "", "DASHBOARD"),
	// TERMINAL
	TERMINAL_GPU_UTILIZATION("DCGM_FI_DEV_GPU_UTIL{%s}", "", "TERMINAL"),
	TERMINAL_GPU_MEM_USAGE("(label_replace(max_over_time(DCGM_FI_DEV_FB_USED{%1$s}[1m]) / (max_over_time(DCGM_FI_DEV_FB_USED{%1$s}[1m]) + min_over_time(DCGM_FI_DEV_FB_FREE{%1$s}[1m])), \"node\", \"$1\", \"kubernetes_node\", \"(.*)\") * 100) * on(node) group_left kube_node_info{}", "", "TERMINAL"),
	TERMINAL_MEM_UTILIZATION("sum(container_memory_usage_bytes{%s})", "", "TERMINAL"),
	TERMINAL_CPU_UTILIZATION("round(sum (rate (container_cpu_usage_seconds_total{%s}[1m])) / sum (machine_cpu_cores{%s}) * 100)", "", "TERMINAL"),

	;
// GPU 사용량, GPU Limit, GPU Request
	private final String query;
	private final String description;
	private final String type;
}
