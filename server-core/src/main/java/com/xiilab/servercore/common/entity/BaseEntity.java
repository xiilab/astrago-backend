package com.xiilab.servercore.common.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@EntityListeners(value = {AuditingEntityListener.class})
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
//모든 테이블에 공통적으로 들어가는 Entity로 이 BaseEntity를 extend 받아 Entity를 만든다.
public class BaseEntity {
	//SecurityContext에서 현재 로그인한 ID를 가져와 자동으로 넣어줌
	@Embedded
	@CreatedBy
	protected RegUser regUser;

	@Embedded
	@LastModifiedBy
	protected ModUser modUser;

	//데이터의 등록시간과 수정시간과 같이 자동으로 추가되고 변경되어야하는 칼럼 처리해주기 위한 Entity
	@CreatedDate
	@Column(name = "REG_DATE", updatable = false)
	protected LocalDateTime regDate;

	@LastModifiedDate
	@Column(name = "MOD_DATE")
	protected LocalDateTime modDate;

}
