package com.xiilab.servercore.common.utils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.SSLException;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CodeErrorCode;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.client.HttpClient;

public class BitBucketApi {
	// @Value("${bitbucket.uri}")
	private WebClient webClient;
	private String bitBucketApiBaseUri;
	private String projectKey;
	private String repositorySlug;

	public BitBucketApi(String bitBucketApiBaseUri, String projectKey, String repositorySlug, String token) {
		this.webClient = createWebClient(token);
		this.bitBucketApiBaseUri = bitBucketApiBaseUri;
		this.projectKey = projectKey.toUpperCase();
		this.repositorySlug = repositorySlug;
	}

	public List<String> getBranchList() {
		return webClient.get()
			.uri(this.bitBucketApiBaseUri + "/rest/api/latest/projects/" + this.projectKey + "/repos/"+ this.repositorySlug + "/branches")
			.retrieve()
			.bodyToFlux(Map.class)
			.flatMapIterable(response -> (List<Map<String, Object>>) response.get("values"))  // "values" 리스트 추출
			.map(valueMap -> (String) valueMap.get("displayId"))  // 각 Map에서 "name" 값 추출
			.collectList()
			.block();
	}

	private WebClient createWebClient(String token) {
		try {
			SslContext sslContext = SslContextBuilder
				.forClient()
				.trustManager(InsecureTrustManagerFactory.INSTANCE)
				.build();
			HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

			return !Objects.isNull(token) ?
				WebClient.builder()
					.clientConnector(new ReactorClientHttpConnector(httpClient))
					.defaultHeader("Authorization", "Bearer " + token)
					.build() :
				WebClient.builder()
					.clientConnector(new ReactorClientHttpConnector(httpClient))
					.build();
		} catch (SSLException e) {
			throw new RestApiException(CodeErrorCode.CONNECTION_ERROR_MESSAGE);
		}
	}
}
