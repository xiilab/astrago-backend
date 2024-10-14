package com.xiilab.modulek8sdb.credential.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xiilab.modulecommon.enums.CredentialType;
import com.xiilab.modulek8sdb.credential.entity.CredentialEntity;

@Repository
public interface CredentialRepository extends JpaRepository<CredentialEntity, Long>, CredentialRepositoryCustom {
	@Query("""
    SELECT c FROM TB_CREDENTIAL c
    WHERE c.regUser.regUserId = :name
    AND (:type IS NULL OR c.type = :type)
""")
	Page<CredentialEntity> findByRegUser_RegUserIdAndType(@Param("name") String name, @Param("type") CredentialType type, Pageable pageable);
}
