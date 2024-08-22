package com.xiilab.modulek8sdb.modelrepo.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.modelrepo.entity.ModelVersionEntity;

@Repository
public interface ModelRepoVersionRepository extends JpaRepository<ModelVersionEntity, Long> {

	@Query("select t from TB_MODEL_REPO_VERSION t where t.modelRepoEntity.id = :modelRepoId order by  t.version desc limit 1")
	ModelVersionEntity findLatestByModelRepoEntityId(@Param("modelRepoId") long modelRepoId);
	List<ModelVersionEntity> findByModelRepoEntityId(long modelRepoId, Sort order);
}
