package com.xiilab.modulecommon.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

public class GithubApi {
	private GitHub gitHub;

	public GithubApi(String token) {
		try {
			this.gitHub = new GitHubBuilder().withOAuthToken(token).build();
			gitHub.checkApiUrlValidity();
		} catch (IOException e) {
			throw new RuntimeException("깃허브 API를 호출 할 수 없습니다.");
		}
	}

	public List<String> getBranchList(String repoName) {
		try {
			GHRepository repository = gitHub.getRepository(repoName);
			Map<String, GHBranch> branches = repository.getBranches();
			return branches.values().stream().map(GHBranch::getName).toList();
		} catch (IOException e) {
			throw new RuntimeException("브랜치 목록을 조회할 수 없습니다.");
		}
	}
}
