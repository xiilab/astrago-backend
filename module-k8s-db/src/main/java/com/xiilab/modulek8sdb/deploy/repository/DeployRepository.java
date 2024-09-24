package com.xiilab.modulek8sdb.deploy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.deploy.entity.DeployEntity;

public interface DeployRepository extends JpaRepository<DeployEntity, Long>, DeployRepositoryCustom {

	@Query("select de from TB_DEPLOY de where de.resourceName = :resourceName")
	Optional<DeployEntity> findByResourceName(@Param("resourceName") String resourceName);

	@Query("""
		select de
		from TB_DEPLOY de
		where de.deleteYN = 'N'
		and de.modelRepoEntity.id =:modelRepoId
""")
	List<DeployEntity> findByModelRepoId(@Param("modelRepoId") long modelRepoId);
}
