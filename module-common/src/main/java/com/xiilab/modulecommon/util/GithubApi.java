package com.xiilab.modulecommon.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CodeErrorCode;

public class GithubApi {
	private GitHub gitHub;

	public GithubApi(String token) {
		try {
			if(!StringUtils.isBlank(token)){
				this.gitHub = new GitHubBuilder().withOAuthToken(token).build();
			}else{
				this.gitHub = GitHub.connectAnonymously();
			}
			gitHub.checkApiUrlValidity();
		} catch (IOException e) {
			throw new RestApiException(CodeErrorCode.CONNECTION_ERROR_MESSAGE);
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

	public List<String> getBranchList(String repoName) {
		try {
			GHRepository repository = gitHub.getRepository(repoName);
			Map<String, GHBranch> branches = repository.getBranches();
			return branches.values().stream().map(GHBranch::getName).toList();
		} catch (IOException e) {
			throw new RestApiException(CodeErrorCode.NOT_FOUND_BRANCH_LIST);
		}
	}
}
