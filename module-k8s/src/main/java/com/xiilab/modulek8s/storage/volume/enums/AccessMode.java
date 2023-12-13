package com.xiilab.modulek8s.storage.volume.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccessMode {
	RWM("ReadWriteMany");

	private final String accessMode;

}
