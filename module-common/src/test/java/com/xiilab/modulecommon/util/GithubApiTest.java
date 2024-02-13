package com.xiilab.modulecommon.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class GithubApiTest {

	@Test
	void getBranchList() {
		String token = "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";
		GithubApi githubApi = new GithubApi(token);
		List<String> branchList = githubApi.getBranchList("jojoldu/blog-code");
		System.out.println("branchList = " + branchList);
	}
}
