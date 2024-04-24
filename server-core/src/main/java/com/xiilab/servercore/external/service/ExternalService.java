package com.xiilab.servercore.external.service;


import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.enums.CodeType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CodeErrorCode;
import com.xiilab.modulecommon.util.GitLabApi;
import com.xiilab.modulecommon.util.GithubApi;
import com.xiilab.modulecommon.util.ValidUtils;
import com.xiilab.modulek8sdb.credential.entity.CredentialEntity;
import com.xiilab.modulek8sdb.credential.repository.CredentialRepository;
import com.xiilab.modulek8sdb.network.repository.NetworkRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ExternalService {
	@Value("${github.public.token}")
	private String publicToken;
	@Value("${gitlab.token}")
	private String gitlabToken;
	private final NetworkRepository networkRepository;
	private final CredentialRepository credentialRepository;

	/**
	 * @param credentialId
	 * @param repoName     ex) jojoldu/blog-code.git
	 * @param codeType
	 * @return
	 */
	public List<String> getGitHubRepoBranchList(Long credentialId, String repoName, CodeType codeType) {
		String token = null;
		if (!ValidUtils.isNullOrZero(credentialId)) {
			CredentialEntity findCredential = credentialRepository.findById(credentialId)
				.orElseThrow();
			token = findCredential.getLoginPw();
		}

		// repository/project 형태로 넣어줘야함
		if(codeType == CodeType.GIT_LAB){
			String baseUrl = getBaseUrl(repoName);
			token = (token == null) ? gitlabToken : token;
			GitLabApi gitLabApi = new GitLabApi(baseUrl, token);
			Pattern pattern = Pattern.compile(baseUrl + "/(.*?)/([^/.]+)(\\.git)?$");
			Matcher matcher = pattern.matcher(repoName);
			if (matcher.find()) {
				String namespace = matcher.group(1);
				String project = matcher.group(2);
			return gitLabApi.getBranchList(namespace, project);
			}
		}else{
			// 토큰값 전달 안되면 PublicToken 넣음
			token = (token == null)? publicToken : token;
			GithubApi githubApi = new GithubApi(token);
			String repo = convertGitHubRepoUrlToRepoName(repoName);
			return githubApi.getBranchList(repo);
		}
		throw new RestApiException(CodeErrorCode.CODE_GET_BLANCHES_FAIL);
	}

	public static String getBaseUrl(String url) {
		int endIndex = url.indexOf("/", "http://".length());
		if (endIndex == -1) {
			return url; // 슬래시가 없는 경우는 그대로 반환
		} else {
			return url.substring(0, endIndex);
		}
	}
	public static String convertGitHubRepoUrlToRepoName(String url){
		// GitHub URL에서 마지막 슬래시 뒤의 문자열을 추출하여 리턴
		String[] parts = url.split("com/");
		String repoName = parts[parts.length - 1];
		// ".git" 확장자가 있다면 제거
		if (repoName.endsWith(".git")) {
			repoName = repoName.substring(0, repoName.length() - 4);
		}
		return repoName;
	}
}
