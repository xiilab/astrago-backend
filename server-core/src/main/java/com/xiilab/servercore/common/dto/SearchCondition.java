package com.xiilab.servercore.common.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class SearchCondition {
	private String option;
	private String keyword;
}
