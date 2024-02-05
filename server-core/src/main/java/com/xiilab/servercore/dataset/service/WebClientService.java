package com.xiilab.servercore.dataset.service;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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

	public void downloadFile(String url){
		byte[] fileContent = webClient.get()
			.uri(url)
			.accept(MediaType.APPLICATION_OCTET_STREAM)
			.retrieve()
			.bodyToMono(byte[].class)
			.block();
	}

	public HttpHeaders getFileInfo(String url){
		return webClient.head()
			.uri(url)
			.retrieve()
			.toBodilessEntity()
			.block()
			.getHeaders();
	}
}
