package com.xiilab.modulek8s.common.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class APIBaseReqDTO {
	//사용자가 실제 입력한 name
	@Pattern(regexp = "^[^-_]*$")
	protected String name;
	//resource에 대한 설명
	protected String description;
 	
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	protected LocalDateTime expirationTime; //je.kim 한자연 커스텀 : 종료예정시간 추가
}
