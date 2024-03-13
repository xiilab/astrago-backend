package com.xiilab.modulek8sdb.version.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.version.entity.MinCudaVersionEntity;

public interface MinCudaVersionRepository extends JpaRepository<MinCudaVersionEntity, Long> {
	@Query(value = "select tmcv.CUDA_VERSION "
		+ "from TB_MIN_CUDA_VERSION tmcv "
		+ "where :version BETWEEN tmcv.MIN_VERSION and tmcv.MAX_VERSION "
		+ "order by tmcv.CUDA_VERSION "
		+ "limit 1",
		nativeQuery = true)
	String getMinCudaVersion(@Param("version") float version);
}
