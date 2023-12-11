package com.xiilab.modulek8s.storage;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.config.K8sAdapter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageService {
	private final K8sAdapter k8sAdapter;



}
