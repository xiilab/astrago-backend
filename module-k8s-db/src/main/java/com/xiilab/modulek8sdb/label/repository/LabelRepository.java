package com.xiilab.modulek8sdb.label.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.label.entity.LabelEntity;

public interface LabelRepository extends JpaRepository<LabelEntity, Long> {

	@Query("select tl from TB_LABEL tl where tl.workspaceResourceName = :workspaceResourceName order by tl.order asc")
	List<LabelEntity> findAllByWorkspaceResourceName(@Param("workspaceResourceName") String workspaceResourceName);

	@Query("select tl from TB_LABEL tl where tl.workspaceResourceName = :workspaceResourceName and tl.name = :labelName order by tl.order asc")
	Optional<LabelEntity> findByWorkspaceResourceNameAndName(@Param("workspaceResourceName") String workspaceResourceName, @Param("labelName") String labelName);

	@Query("select count(tl) from TB_LABEL tl where tl.workspaceResourceName = :workspaceResourceName ")
	Integer findByWorkspaceResourceNameContaining(@Param("workspaceResourceName") String workspaceResourceName);
}
