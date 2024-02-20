package com.xiilab.modulek8sdb.credential.repository;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.credential.entity.CredentialEntity;

@Repository
public interface CredentialRepositoryCustom {
	Page<CredentialEntity> findByIdIn(Collection<Long> ids, Pageable pageable);
}
