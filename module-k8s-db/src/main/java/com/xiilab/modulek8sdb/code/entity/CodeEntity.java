package com.xiilab.modulek8sdb.code.entity;

import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulek8sdb.code.enums.CodeType;
import com.xiilab.modulek8sdb.common.entity.BaseEntity;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.credential.entity.CredentialEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_CODE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
public class CodeEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CODE_ID")
	private Long id;								// CODE ID
	@Column(name = "CODE_TYPE")
	@Enumerated(value = EnumType.STRING)
	private CodeType codeType;

	@Column(name = "REPOSITORY_TYPE")
	@Enumerated(value = EnumType.STRING)
	private RepositoryType repositoryType;
	@Column(name = "CODE_URL")
	private String codeURL;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CREDENTIAL_ID")
	private CredentialEntity credentialEntity; 		// credential Entity
	@Column(name = "WORKSPACE_NAME")
	private String workspaceResourceName;

	@Column(name = "DELETE_YN")
	@Enumerated(EnumType.STRING)
	private DeleteYN deleteYn = DeleteYN.N;


	@Builder(builderClassName = "dtoConverter", builderMethodName = "dtoConverter")
	CodeEntity(CodeType codeType, String codeURL, String workspaceResourceName, CredentialEntity credentialEntity, RepositoryType repositoryType) {
		this.codeType = codeType;
		this.codeURL = codeURL;
		this.credentialEntity = credentialEntity;
		this.workspaceResourceName = workspaceResourceName;
		this.repositoryType = repositoryType;
	}
}
