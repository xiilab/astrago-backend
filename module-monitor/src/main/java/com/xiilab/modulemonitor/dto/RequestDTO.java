package com.xiilab.modulemonitor.dto;

import java.util.Objects;

import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
public class RequestDTO {
	private String metricName;
	private String startDate;
	private String endDate;
	private String namespace;
	private String podName;
	private String nodeName;
	private String instance;

	public RequestDTO(String metricName,
		String startDate,
		String endDate,
		String namespace,
		String podName,
		String nodeName,
		String instance
	) {
		this.metricName = metricName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.namespace = namespace;
		this.podName = podName;
		this.nodeName = nodeName;
		this.instance = instance;
	}

	public String metricName() {
		return metricName;
	}

	public String startDate() {
		return startDate;
	}

	public String endDate() {
		return endDate;
	}

	public String namespace() {
		return namespace;
	}

	public String podName() {
		return podName;
	}

	public String nodeName() {
		return nodeName;
	}

	public String instance() {
		return instance;
	}

	public void changeMetricName(String metricName){
		this.metricName = metricName;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		var that = (RequestDTO)obj;
		return Objects.equals(this.metricName, that.metricName) &&
			Objects.equals(this.startDate, that.startDate) &&
			Objects.equals(this.endDate, that.endDate) &&
			Objects.equals(this.namespace, that.namespace) &&
			Objects.equals(this.podName, that.podName) &&
			Objects.equals(this.nodeName, that.nodeName) &&
			Objects.equals(this.instance, that.instance);
	}

	@Override
	public int hashCode() {
		return Objects.hash(metricName, startDate, endDate, namespace, podName, nodeName, instance);
	}

	@Override
	public String toString() {
		return "RequestDTO[" +
			"metricName=" + metricName + ", " +
			"startDate=" + startDate + ", " +
			"endDate=" + endDate + ", " +
			"namespace=" + namespace + ", " +
			"podName=" + podName + ", " +
			"nodeName=" + nodeName + ", " +
			"instance=" + instance + ']';
	}

}
