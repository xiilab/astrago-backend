package com.xiilab.modulek8s.common.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StorageType {
	NFS,
	NFS1,
	PURE;

}
