package com.xiilab.modulek8sdb.pin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulek8sdb.pin.entity.PinEntity;
import com.xiilab.modulek8sdb.pin.enumeration.PinType;

@Repository
public interface PinRepository extends JpaRepository<PinEntity, Long> {
	PinEntity findByTypeAndResourceNameAndRegUser_RegUserId(PinType type, String resourceId, String id);

	@Query("select t from TB_PIN t where t.type = ?1 and t.regUser.regUserId = ?2")
	List<PinEntity> findByTypeAndRegUser_RegUserId(PinType type, String regUserId);

	void deleteByTypeAndResourceNameAndRegUser_RegUserId(PinType type, String resourceName, String regUserId);

	@Transactional
	@Modifying
	@Query("delete from TB_PIN t where t.resourceName = ?1 and t.type = ?2")
	int deleteByResourceNameAndType(String resourceName, PinType type);
}
