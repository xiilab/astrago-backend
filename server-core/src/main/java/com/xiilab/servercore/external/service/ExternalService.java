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
import com.xiilab.modulecommon.util.RepositoryUrlUtils;
import com.xiilab.modulecommon.util.ValidUtils;
import com.xiilab.modulek8sdb.credential.entity.CredentialEntity;
import com.xiilab.modulek8sdb.credential.repository.CredentialRepository;
import com.xiilab.modulek8sdb.network.repository.NetworkRepository;
import com.xiilab.servercore.common.utils.BitBucketApi;
import com.xiilab.servercore.common.utils.GithubApi;

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
	 * @param gitCloneUrl     ex) jojoldu/blog-code.git
	 * @param codeType
	 * @return
	 */
	public List<String> getGitHubRepoBranchList(Long credentialId, String gitCloneUrl, CodeType codeType) {
		String token = null;
		String userName = null;
		if (!ValidUtils.isNullOrZero(credentialId)) {
			CredentialEntity findCredential = credentialRepository.findById(credentialId)
				.orElseThrow();
			userName = findCredential.getLoginId();
			token = findCredential.getLoginPw();
		}

		// repository/project 형태로 넣어줘야함
		if (codeType == CodeType.GIT_LAB) {
			String baseUrl = getBaseUrl(gitCloneUrl);
			token = (token == null) ? gitlabToken : token;
			GitLabApi gitLabApi = new GitLabApi(baseUrl, token);
			Pattern pattern = Pattern.compile(baseUrl + "/(.*?)/([^/.]+)(\\.git)?$");
			Matcher matcher = pattern.matcher(gitCloneUrl);
			if (matcher.find()) {
				String namespace = matcher.group(1);
				String project = matcher.group(2);
				return gitLabApi.getBranchList(namespace, project);
			}
		} else if (codeType == CodeType.BIT_BUCKET) {
			BitBucketApi bitBucketApi = new BitBucketApi(gitCloneUrl, userName, token);
			return bitBucketApi.getBranchList();
		} else if (codeType == CodeType.GIT_HUB){
			// 토큰값 전달 안되면 PublicToken 넣음
			token = (token == null) ? publicToken : token;
			GithubApi githubApi = new GithubApi(token);
			String repo = RepositoryUrlUtils.convertRepoUrlToRepoName(codeType, gitCloneUrl);
			String[] split = repo.split("/");
			return githubApi.getBranchList(split[0], split[1]);
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
}
