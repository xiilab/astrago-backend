package com.xiilab.servercore.common.utils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.SSLException;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import com.xiilab.modulecommon.enums.CodeType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CodeErrorCode;
import com.xiilab.modulecommon.util.RepositoryUrlUtils;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;

@Slf4j
public class BitBucketApi {
	private WebClient webClient;
	private String apiDomain;
	private String projectKey;
	private String repositorySlug;

	public BitBucketApi(String gitCloneURL, String userName, String token) {
		// 토큰 없으면 exception
		if (Objects.isNull(token)) {
			log.error("Token is null.");
			throw new RestApiException(CodeErrorCode.BITBUCKET_CREDENTIALS_REQUIRED_MESSAGE);
		}
		if (Objects.isNull(userName)) {
			log.error("Username is null.");
			throw new RestApiException(CodeErrorCode.BITBUCKET_CREDENTIALS_REQUIRED_MESSAGE);
		}

		initializeRepositoryClient(gitCloneURL, userName, token);
	}

	private void initializeRepositoryClient(String gitCloneURL, String userName, String token) {
		String apiDomain = RepositoryUrlUtils.extractDomain(gitCloneURL);
		String repositoryName = RepositoryUrlUtils.convertRepoUrlToRepoName(CodeType.BIT_BUCKET, gitCloneURL);

		String[] pathSegments = repositoryName.split("/");
		String[] parsedRepoDetails = parseProjectKeyAndRepositorySlug(pathSegments);

		this.apiDomain = apiDomain;
		this.webClient = createWebClient(userName, token);
		this.projectKey = parsedRepoDetails[0];
		this.repositorySlug = parsedRepoDetails[1];
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
		log.info("Branch List restAPI URL: \"{}/rest/api/latest/projects/{}/repos/{}/branches\"", this.apiDomain, this.projectKey, this.repositorySlug);
		return this.webClient.get()
			.uri("/rest/api/latest/projects/{projectKey}/repos/{repositorySlug}/branches", this.projectKey, this.repositorySlug)
			.retrieve()
			.bodyToFlux(Map.class)
			.flatMapIterable(response -> (List<Map<String, Object>>) response.get("values"))  // "values" 리스트 추출
			.map(valueMap -> (String) valueMap.get("displayId"))  // 각 Map에서 "name" 값 추출
			.collectList()
			.block();
	}

	private WebClient createWebClient(String userName, String token) {
		try {
			SslContext sslContext = SslContextBuilder
				.forClient()
				.trustManager(InsecureTrustManagerFactory.INSTANCE)
				.build();
			HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

			return !Objects.isNull(token) ?
				WebClient.builder()
					.clientConnector(new ReactorClientHttpConnector(httpClient))
					.baseUrl(this.apiDomain)
					.defaultHeaders(header -> header.setBasicAuth(userName, token))
					.build() :
				WebClient.builder()
					.baseUrl(this.apiDomain)
					.clientConnector(new ReactorClientHttpConnector(httpClient))
					.build();
		} catch (SSLException e) {
			log.error("SSLException error.");
			throw new RestApiException(CodeErrorCode.CONNECTION_ERROR_MESSAGE);
		}
	}

	private String[] parseProjectKeyAndRepositorySlug(String[] pathSegments) {
		if (pathSegments.length == 2) {
			return new String[] { pathSegments[0].toUpperCase(), pathSegments[1] };
		} else if (pathSegments.length == 3) {
			return new String[] { pathSegments[1].toUpperCase(), pathSegments[2] };
		} else {
			throw new RestApiException(CodeErrorCode.UNSUPPORTED_REPOSITORY_ERROR_CODE);
		}
	}
}
