package com.xiilab.modulemonitor.node;

import org.springframework.stereotype.Service;

import com.xiilab.modulemonitor.config.K8sAdapter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NodeService {
	private final K8sAdapter k8sAdapter;


}
