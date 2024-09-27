package com.xiilab.servercore.common.utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLException;

import org.apache.commons.lang3.StringUtils;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CodeErrorCode;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.client.HttpClient;

public class GithubApi {
	private GitHub gitHub;
	private WebClient webClient;

	public GithubApi(String token) {
		try {
			if (!StringUtils.isBlank(token)) {
				// this.gitHub = new GitHubBuilder().withOAuthToken(token).build();
				SslContext context = SslContextBuilder.forClient()
					.trustManager(InsecureTrustManagerFactory.INSTANCE)
					.build();
				HttpClient httpClient = HttpClient.create().secure(provider -> provider.sslContext(context));

				webClient = WebClient.builder()
					.clientConnector(new ReactorClientHttpConnector(httpClient))
					.baseUrl("https://api.github.com")
					.defaultHeader("Authorization", "Bearer " + token)
					.defaultHeader("Accept", "application/vnd.github+json")
					.defaultHeader("X-GitHub-Api-Version", "2022-11-28")
					.build();
			} else {
				// this.gitHub = GitHub.connectAnonymously();
				SslContext context = SslContextBuilder.forClient()
					.trustManager(InsecureTrustManagerFactory.INSTANCE)
					.build();
				HttpClient httpClient = HttpClient.create().secure(provider -> provider.sslContext(context));
				webClient = WebClient.builder()
					.clientConnector(new ReactorClientHttpConnector(httpClient))
					.baseUrl("https://api.github.com")
					.defaultHeader("Accept", "application/vnd.github+json")
					.defaultHeader("X-GitHub-Api-Version", "2022-11-28")
					.build();
			}
			// gitHub.checkApiUrlValidity();
		} catch (RuntimeException e) {
			throw new RestApiException(CodeErrorCode.CONNECTION_ERROR_MESSAGE);
		} catch (SSLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isRepoConnected(String repoName) {
		try {
			gitHub.getRepository(repoName);
			return true;
		} catch (IOException e) {
			throw new RestApiException(CodeErrorCode.CONNECTION_ERROR_MESSAGE);
		}
	}

	public boolean isRepoConnected(String owner, String repo) {
		try {
			webClient.get()
				.uri("/repos/{owner}/{repo}", owner, repo)
				.retrieve()
				.bodyToFlux(String.class)  // 응답을 Map으로 받아옴
				.collectList().block();
			return true;
		} catch (RuntimeException e) {
			throw new RestApiException(CodeErrorCode.CONNECTION_ERROR_MESSAGE);
		}
	}

	public List<String> getBranchList(String repoName) {
		try {
			GHRepository repository = gitHub.getRepository(repoName);
			Map<String, GHBranch> branches = repository.getBranches();
			return branches.values().stream().map(GHBranch::getName).toList();
		} catch (IOException e) {
			throw new RestApiException(CodeErrorCode.NOT_FOUND_BRANCH_LIST);
		}
	}

	public List<String> getBranchList(String owner, String repo) {
		return webClient.get()
			.uri("/repos/{owner}/{repo}/branches", owner, repo)
			.retrieve()
			.bodyToFlux(Map.class)  // 응답을 Map으로 받아옴
			.map(map -> (String)map.get("name"))  // 각 Map에서 "name" 필드 추출
			.collectList()  // Flux를 List로 변환
			.block();
	}

}
