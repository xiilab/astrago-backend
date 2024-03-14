package com.xiilab.modulek8sdb.version.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.version.entity.FrameWorkVersionEntity;

public interface FrameWorkVersionRepository extends JpaRepository<FrameWorkVersionEntity, Long> {


	@Query(value = "SELECT tfv.* "
		+ "from TB_FRAMEWORK_VERSION tfv "
		+ "where tfv.CUDA_VERSION BETWEEN :minCudaVersion and :maxCudaVersion",
	nativeQuery = true)
	List<FrameWorkVersionEntity> getCompatibleFrameworkVersion(@Param("maxCudaVersion") float maxCudaVersion,@Param("minCudaVersion") float minCudaVersion);
}
