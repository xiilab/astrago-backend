package com.xiilab.modulek8sdb.code.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulek8sdb.code.entity.CodeEntity;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;

@Repository
public interface CodeRepository  extends JpaRepository<CodeEntity, Long> {
	@Query("""
		select c from CodeEntity c
		where c.codeURL = ?1 and c.repositoryType = ?2 and c.regUser.regUserId = ?3 and c.deleteYn = ?4""")
	List<CodeEntity> findByCodeURLAndRepositoryTypeAndRegUser_RegUserIdAndDeleteYn(String codeURL,
		RepositoryType repositoryType, String regUserId, DeleteYN deleteYn);

	@Query("""
		select c from CodeEntity c
		where c.workspaceResourceName = ?1 and c.codeURL = ?2 and c.repositoryType = ?3 and c.deleteYn = ?4""")
	List<CodeEntity> findByWorkspaceResourceNameAndCodeURLAndRepositoryTypeAndDeleteYn(String workspaceResourceName,
		String codeURL, RepositoryType repositoryType, DeleteYN deleteYn);
}
