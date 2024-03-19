package com.xiilab.modulecommon.enums;

public enum K8sContainerReason {
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
	NetworkNotReady,
	Pulling,
	Pulled,
	InspectFailed,
	ErrImageNeverPull
}
