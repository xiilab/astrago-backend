package com.xiilab.modulecommon.enums;

public enum K8sContainerReason {
	FailedScheduling,
	Scheduled,
	Created,
	Started,
	Failed,
	Killing,
	Preempting,
	BackOff,
	ExceededGracePeriod,
	FailedKillPod,
	FailedCreatePodContainer,
	FailedMount,
	NetworkNotReady,
	Pulling,
	Pulled,
	InspectFailed,
	ErrImageNeverPull
}
