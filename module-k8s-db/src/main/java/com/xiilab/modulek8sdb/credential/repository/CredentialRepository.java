package com.xiilab.modulek8sdb.credential.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.modulecommon.enums.CredentialType;
import com.xiilab.modulek8sdb.credential.entity.CredentialEntity;

@Repository
public interface CredentialRepository extends JpaRepository<CredentialEntity, Long>, CredentialRepositoryCustom {
	Page<CredentialEntity> findByRegUser_RegUserIdAAndType(String name, CredentialType type, Pageable pageable);
}
