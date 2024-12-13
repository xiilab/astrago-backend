package com.xiilab.modulek8sdb.modelrepo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.modelrepo.entity.ModelRepoEntity;

@Repository
public interface ModelRepoRepository extends JpaRepository<ModelRepoEntity, Long> {
	@Query("""
		select t
		from TB_MODEL_REPO t
		left join t.modelLabelEntityList l
		left join t.modelVersionList v
		where t.workspaceResourceName = :workspaceResourceName
		and (:search is null or :search = '' or t.modelName like concat('%', :search, '%'))
		order by t.regDate desc
		""")
	List<ModelRepoEntity> findAllByWorkspaceResourceName(@Param("workspaceResourceName") String workspaceResourceName,@Param("search") String search);
}
