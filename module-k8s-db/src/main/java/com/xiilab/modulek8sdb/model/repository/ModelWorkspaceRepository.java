package com.xiilab.modulek8sdb.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.model.entity.ModelWorkSpaceMappingEntity;

public interface ModelWorkspaceRepository extends JpaRepository<ModelWorkSpaceMappingEntity, Long> {

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("delete from ModelWorkSpaceMappingEntity mwm where mwm.model.modelId = :modelId")
	void deleteModelWorkspaceMappingById(@Param("modelId") Long modelId);

	@Query("select mwm  from ModelWorkSpaceMappingEntity mwm "
		+ " join fetch mwm.model m where mwm.workspaceResourceName = :workspaceResourceName and mwm.model.deleteYn = 'N' "
		+ "order by mwm.regDate DESC")
	List<ModelWorkSpaceMappingEntity> findByWorkspaceResourceName(@Param("workspaceResourceName") String workspaceResourceName);

	@Query("select mwm "
		+ "from ModelWorkSpaceMappingEntity mwm "
		+ "where mwm.model.modelId = :modelId "
		+ "and mwm.model.deleteYn = 'N'"
		+ "and mwm.workspaceResourceName like :workspaceResourceName")
	ModelWorkSpaceMappingEntity findByWorkspaceResourceNameAndModelId(@Param("workspaceResourceName") String workspaceResourceName, @Param("modelId") Long modelId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("delete "
		+ "from ModelWorkSpaceMappingEntity mwm "
		+ "where mwm.model.modelId = :modelId "
		+ "and mwm.workspaceResourceName like :workspaceResourceName")
	void deleteByModelIdAndWorkspaceResourceName(@Param("modelId") Long modelId, @Param("workspaceResourceName") String workspaceResourceName);
}
