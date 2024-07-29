package com.xiilab.modulek8sdb.modelrepo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.modelrepo.entity.ModelVersionEntity;

@Repository
public interface ModelRepoVersionRepository extends JpaRepository<ModelVersionEntity, Long> {

	@Query("select m from ModelVersionEntity m where m.modelRepoEntity.id = :modelRepoId order by  m.verson desc limit 1")
	ModelVersionEntity findByModelRepoEntityId(long modelRepoId);
}
