package com.xiilab.servercore.external.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.util.GithubApi;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExternalService {
	@Value("${github.public.token}")
	private String publicToken;

	/**
	 *
	 * @param token
	 * @param repoName ex) jojoldu/blog-code.git
	 * @return
	 */
	public List<String> getGitHubRepoBranchList(String token, String repoName) {
		// 토큰값 전달 안되면 PublicToken 넣음
		token = (token != null)? token : publicToken;
		GithubApi githubApi = new GithubApi(token);
		return githubApi.getBranchList(repoName);
	}
}
