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
	List<CodeEntity> getCodeEntitiesByWorkspaceResourceNameAndRepositoryTypeAndDeleteYnEquals(
		String workspaceResourceName, RepositoryType repositoryType, DeleteYN deleteYn);

	List<CodeEntity> getCodeEntitiesByWorkspaceResourceNameAndCodeURLAndDeleteYnEquals(String resourceName, String codeUrl, DeleteYN deleteYn);

	@Query("select c from CodeEntity c where c.regUser.regUserId = ?1 and c.deleteYn = ?2")
	List<CodeEntity> findByRegUser_RegUserIdAndDeleteYn(String regUserId, DeleteYN deleteYn);
}
