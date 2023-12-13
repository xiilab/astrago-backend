package com.xiilab.modulek8s.storage.storageclass.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StorageType {
	NFS,
	PURE;

}
