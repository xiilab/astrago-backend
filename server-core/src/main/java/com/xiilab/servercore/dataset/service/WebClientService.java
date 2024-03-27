package com.xiilab.servercore.dataset.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class WebClientService {
	private final WebClient webClient;
	final long kilobyte = 1024;
	final long megabyte = kilobyte * 1024;
	final long gigabyte = megabyte * 1024;

	public <T> List<T> getObjectsFromUrl(String url, Class<T> responseType) {
		return webClient.get().uri(url)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.bodyToFlux(responseType)
			.collectList()
			.block();
	}

	public byte[] downloadFile(String url, MediaType mediaType){
		return webClient.get()
			.uri(url)
			.accept(mediaType)
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
	public String retrieveFileName(String fileUrl) {
		HttpHeaders headers = webClient.head()
			.uri(fileUrl)
			.retrieve()
			.toBodilessEntity()
			.block()
			.getHeaders();

		// Content-Disposition 헤더에서 파일 이름 추출
		String contentDisposition = headers.getFirst(HttpHeaders.CONTENT_DISPOSITION);
		if (contentDisposition != null && !contentDisposition.isEmpty()) {
			int startIndex = contentDisposition.indexOf("filename=");
			if (startIndex != -1) {
				startIndex += "filename=".length();
				int endIndex = contentDisposition.indexOf(";", startIndex);
				endIndex = (endIndex == -1) ? contentDisposition.length() : endIndex;
				return contentDisposition.substring(startIndex, endIndex).trim();
			}
		}
		// 파일 이름이 Content-Disposition 헤더에 없을 경우 URL에서 추출
		String[] urlSegments = fileUrl.split("/");
		return urlSegments[urlSegments.length - 1];
	}

	public String formatFileSize(long bytes) {
		if (bytes >= gigabyte) {
			return String.format("%.2f GB", (double) bytes / gigabyte);
		} else if (bytes >= megabyte) {
			return String.format("%.2f MB", (double) bytes / megabyte);
		} else if (bytes >= kilobyte) {
			return String.format("%.2f KB", (double) bytes / kilobyte);
		} else {
			return bytes + " Bytes";
		}
	}

	public String formatLastModifiedTime(long lastModifiedMillis) {
		if (lastModifiedMillis > 0) {
			Instant lastModifiedInstant = Instant.ofEpochMilli(lastModifiedMillis);
			LocalDateTime lastModifiedDateTime = LocalDateTime.ofInstant(lastModifiedInstant, ZoneId.systemDefault());
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			return formatter.format(lastModifiedDateTime);
		}
		return "Last-Modified not available";
	}
}
