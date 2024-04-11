package com.xiilab.modulecommon.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Project;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CodeErrorCode;

public class GitLabApi {

	private org.gitlab4j.api.GitLabApi gitLabApi;

	public GitLabApi(String gitlabUrl, String gitlabToken) {
		if(!StringUtils.isBlank(gitlabToken)){
			this.gitLabApi = new org.gitlab4j.api.GitLabApi(gitlabUrl,gitlabToken);
		}else{
			this.gitLabApi = new org.gitlab4j.api.GitLabApi(gitlabUrl,"");
		}
	}

	public boolean isRepoConnected(String namespace, String projectName) {
		try {
			gitLabApi.getProjectApi().getProject(namespace, projectName);
			return true;
		} catch (GitLabApiException e) {
			throw new RestApiException(CodeErrorCode.CONNECTION_ERROR_MESSAGE);
		}
	}

	public List<String> getBranchList(String namespace, String projectName) {
		try {
			Project project = gitLabApi.getProjectApi().getProject(namespace, projectName);
			List<Branch> branches = gitLabApi.getRepositoryApi().getBranches(project.getId());
			return branches.stream().map(Branch::getName).toList();
		}catch (GitLabApiException e) {
			throw new RestApiException(CodeErrorCode.NOT_FOUND_BRANCH_LIST);
		}
	}
}
