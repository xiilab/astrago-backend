package com.xiilab.servercore.external.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.xiilab.modulecommon.util.GitLabApi;
import com.xiilab.modulecommon.util.GithubApi;
import com.xiilab.modulecommon.util.NumberValidUtils;
import com.xiilab.modulek8sdb.common.enums.NetworkCloseYN;
import com.xiilab.modulek8sdb.credential.entity.CredentialEntity;
import com.xiilab.modulek8sdb.credential.repository.CredentialRepository;
import com.xiilab.modulek8sdb.network.entity.NetworkEntity;
import com.xiilab.modulek8sdb.network.repository.NetworkRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ExternalService {
	@Value("${github.public.token}")
	private String publicToken;
	@Value("${gitlab.url}")
	private String gitlabUrl;
	@Value("${gitlab.token}")
	private String gitlabToken;
	private final NetworkRepository networkRepository;
	private final CredentialRepository credentialRepository;

	/**
	 *
	 * @param credentialId
	 * @param repoName ex) jojoldu/blog-code.git
	 * @return
	 */
	public List<String> getGitHubRepoBranchList(Long credentialId, String repoName) {
		String token = null;
		if (!NumberValidUtils.isNullOrZero(credentialId)) {
			CredentialEntity findCredential = credentialRepository.findById(credentialId)
				.orElseThrow();
			token = findCredential.getLoginPw();
		}

		NetworkEntity network = networkRepository.findTopBy(Sort.by("networkId").descending());
		if(network.getNetworkCloseYN() == NetworkCloseYN.Y){
			token = (token == null) ? gitlabToken : token;
			GitLabApi gitLabApi = new GitLabApi(gitlabUrl, token);
			String[] repo = repoName.split("/");
			return gitLabApi.getBranchList(repo[0], repo[1]);
		}else{
			// 토큰값 전달 안되면 PublicToken 넣음
			token = (token == null)? publicToken : token;
			GithubApi githubApi = new GithubApi(token);
			return githubApi.getBranchList(repoName);
		}
	}
}
