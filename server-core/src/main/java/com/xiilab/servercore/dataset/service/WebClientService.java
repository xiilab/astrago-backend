package com.xiilab.servercore.dataset.service;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.xiilab.servercore.dataset.dto.NginxFilesDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebClientService {
	private final WebClient webClient;

	public <T> List<T> getObjectsFromUrl(String url, Class<T> responseType) {
		return webClient.get().uri(url)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.bodyToFlux(responseType)
			.collectList()
			.block();
	}
}
