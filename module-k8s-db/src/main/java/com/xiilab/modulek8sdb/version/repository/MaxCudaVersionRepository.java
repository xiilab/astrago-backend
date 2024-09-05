package com.xiilab.modulek8sdb.version.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.version.entity.MaxCudaVersionEntity;

public interface MaxCudaVersionRepository extends JpaRepository<MaxCudaVersionEntity, Long> {
	@Query(value = "select tmcv.CUDA_VERSION "
		+ "from TB_MAX_CUDA_VERSION tmcv "
		+ "where tmcv.MAJOR_VERSION <= :driverMajor "
		+ "and (tmcv.MAJOR_VERSION <= :driverMajor OR tmcv.MINOR_VERSION <= :driverMinor) "
		+ "and (tmcv.MAJOR_VERSION <= :driverMajor OR tmcv.MINOR_VERSION <= :driverMinor OR tmcv.REV <= :driverRev) "
		+ "order by tmcv.MAJOR_VERSION DESC "
		+ "limit 1",
	nativeQuery = true)
	String getMaxCudaVersion(@Param("driverMajor") float driverMajor, @Param("driverMinor") float driverMinor, @Param("driverRev") float driverRev);
}
