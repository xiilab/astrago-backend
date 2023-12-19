package com.xiilab.modulemonitor.enumeration;

import lombok.Getter;

@Getter
public enum K8sErrorStatus {
	CrashLoopBackOff,
	ImagePullBackOff,
	ErrImagePull,
	InvalidImageName
}
