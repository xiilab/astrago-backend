package com.xiilab.servercore.common.utils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.SSLException;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CodeErrorCode;
import com.xiilab.modulecommon.util.RepositoryUrlUtils;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.client.HttpClient;

public class BitBucketApi {
	private WebClient webClient;
	private String projectKey;
	private String repositorySlug;

	public BitBucketApi(String gitCloneURL, String token) {
		// 토큰 없으면 exception
		if (Objects.isNull(token)) {
			throw new RestApiException(CodeErrorCode.BITBUCKET_CREDENTIALS_REQUIRED_MESSAGE);
		}
		initializeRepositoryClient(gitCloneURL, token);
	}

	private void initializeRepositoryClient(String gitCloneURL, String token) {
		String baseUri = RepositoryUrlUtils.extractDomain(gitCloneURL);
		String repository = RepositoryUrlUtils.convertRepoUrlToRepoName(gitCloneURL);
		if (repository.split("/").length != 2) {
			throw new RestApiException(CodeErrorCode.UNSUPPORTED_REPOSITORY_ERROR_CODE);
		}
		String[] splitRepoName = repository.split("/");

		this.webClient = createWebClient(baseUri, token);
		this.projectKey = splitRepoName[0].toUpperCase();
		this.repositorySlug = splitRepoName[1];
	}

	public boolean isRepoConnected() {
		try {
			List<String> branchList = getBranchList();
			return branchList != null && !branchList.isEmpty();
		} catch (Exception e) {
			return false;
		}
	}

	public List<String> getBranchList() {
		return webClient.get()
			.uri("/rest/api/latest/projects/{projectKey}/repos/{repositorySlug}/branches", projectKey, repositorySlug)
			.retrieve()
			.bodyToFlux(Map.class)
			.flatMapIterable(response -> (List<Map<String, Object>>) response.get("values"))  // "values" 리스트 추출
			.map(valueMap -> (String) valueMap.get("displayId"))  // 각 Map에서 "name" 값 추출
			.collectList()
			.block();
	}

	private WebClient createWebClient(String bitBucketApiBaseUri, String token) {
		try {
			SslContext sslContext = SslContextBuilder
				.forClient()
				.trustManager(InsecureTrustManagerFactory.INSTANCE)
				.build();
			HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

			return !Objects.isNull(token) ?
				WebClient.builder()
					.clientConnector(new ReactorClientHttpConnector(httpClient))
					.baseUrl(bitBucketApiBaseUri)
					.defaultHeader("Authorization", "Bearer " + token)
					.build() :
				WebClient.builder()
					.baseUrl(bitBucketApiBaseUri)
					.clientConnector(new ReactorClientHttpConnector(httpClient))
					.build();
		} catch (SSLException e) {
			throw new RestApiException(CodeErrorCode.CONNECTION_ERROR_MESSAGE);
		}
	}
}