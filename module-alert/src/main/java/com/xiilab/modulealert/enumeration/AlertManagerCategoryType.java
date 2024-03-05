package com.xiilab.modulealert.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlertManagerCategoryType {
	GPU_TEMP("GPU 온도", "avg(DCGM_FI_DEV_GPU_TEMP{kubernetes_node = \"%s\"}) %s %s"), // GPU 온도
	GPU_MEMORY("GPU 메모리 사용률", "avg(DCGM_FI_DEV_GPU_UTIL{kubernetes_node=\"%s\"}) %s %s"), // GPU 메모리
	GPU_USAGE("GPU 사용률", "avg(DCGM_FI_DEV_FB_USED{kubernetes_node=\"%s\"}) %s %s"), // GPU 사용량
	MEMORY_USAGE("메모리 사용률", "(label_replace(((node_memory_MemTotal_bytes - avg_over_time(node_memory_MemFree_bytes[1m]) - avg_over_time(node_memory_Buffers_bytes[1m]) - avg_over_time(node_memory_Cached_bytes[1m]) - avg_over_time(node_memory_Slab_bytes[1m])) / node_memory_MemTotal_bytes) * 100,\"internal_ip\",\"$1\",\"instance\",\"(.*):(.*)\")) * on (internal_ip) group_left(node) kube_node_info{node = \"%s\"} %s %s"), // MEMORY 사용량
	CPU_USAGE("CPU 사용률", "(label_replace(100 - (avg by (instance) (irate(node_cpu_seconds_total{mode=\"idle\"}[1m])) * 100),\"internal_ip\",\"$1\",\"instance\",\"(.*):(.*)\")) * on (internal_ip) group_left(node) kube_node_info{node=\"%s\"} %s %s"), // CPU 사용량
	DISK_USAGE("DISK 사용률", "(label_replace((100 - ( (node_filesystem_avail_bytes{mountpoint=\"/\",fstype!=\"rootfs\"} * 100) / node_filesystem_size_bytes{mountpoint=\"/\",fstype!=\"rootfs\"} ) ),\"internal_ip\",\"$1\",\"instance\",\"(.*):(.*)\")) * on (internal_ip) group_left(node) kube_node_info{node=\"%s\"} %s %s"); // DISK 사용량

	private final String typeValue;
	private final String query;

}
