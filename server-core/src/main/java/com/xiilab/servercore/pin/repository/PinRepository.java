package com.xiilab.servercore.pin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.servercore.pin.entity.PinEntity;
import com.xiilab.servercore.pin.enumeration.PinType;

@Repository
public interface PinRepository extends JpaRepository<PinEntity, Long> {
	List<PinEntity> findByTypeAndAndRegUser_RegUserId(PinType type, String id);

	long deleteByTypeAndResourceIdAndRegUser_RegUserId(PinType type, String resourceId, String id);

	PinEntity findByTypeAndResourceIdAndRegUser_RegUserId(PinType type, String resourceId, String id);
}
