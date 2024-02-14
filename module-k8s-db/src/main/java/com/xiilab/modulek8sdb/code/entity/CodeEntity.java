package com.xiilab.modulek8sdb.code.entity;

import com.xiilab.modulek8sdb.code.enums.CodeType;
import com.xiilab.modulek8sdb.common.entity.BaseEntity;
import com.xiilab.modulek8sdb.code.dto.CodeReqDTO;
import com.xiilab.modulek8sdb.credential.entity.CredentialEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_CODE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CodeEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CODE_ID")
	private Long id;								// CODE ID
	@Column(name = "CODE_TYPE")
	@Enumerated(value = EnumType.STRING)
	private CodeType codeType;
	@Column(name = "CODE_URL")
	private String codeURL;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CREDENTIAL_ID")
	private CredentialEntity credentialEntity; 		// credential Entity
	@Column(name = "WORKSPACE_NAME")
	private String workspaceResourceName;


	@Builder(builderClassName = "dtoConverter", builderMethodName = "dtoConverter")
	CodeEntity(CodeReqDTO codeReqDTO, CredentialEntity credentialEntity) {
		this.codeType = codeReqDTO.getCodeType();
		this.codeURL = codeReqDTO.getCodeURL();
		this.credentialEntity = credentialEntity;
		this.workspaceResourceName = codeReqDTO.getWorkspaceName();
	}
}
