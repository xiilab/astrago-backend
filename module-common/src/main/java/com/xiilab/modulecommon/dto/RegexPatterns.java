package com.xiilab.modulecommon.dto;

public class RegexPatterns {
	public static final String GITHUB_URL_PATTERN = "^https:\\/\\/github\\.com\\/[a-zA-Z0-9_-]+\\/[a-zA-Z0-9_-]+(?:\\.git)?$";
	public static final String GITLAB_URL_PATTERN = "^https:\\/\\/gitlab\\.com\\/[a-zA-Z0-9_-]+\\/[a-zA-Z0-9_-]+(?:\\.git)?$";
	public static final String BITBUCKET_URL_PATTERN = "^https?:\\/\\/(?:[a-zA-Z0-9.-]+(?::[0-9]+)?|localhost(?::7990)?)\\/scm\\/[a-zA-Z0-9_\\-]+\\/[a-zA-Z0-9_\\-]+\\.git$";
}
