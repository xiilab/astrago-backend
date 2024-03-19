package com.xiilab.modulecommon.enums;

public enum K8SReasonType {
	Created,
	Started,
	Failed,
	Killing,
	Preempting,
	BackOff,
	ExceededGracePeriod,
	FailedKillPod,
	FailedCreatedPodContainer,
	NeworkNotReady,
	Pulling,
	Pulled,
	InspectFailed,
	ErrImageNeverPull,
	Backoff
}
