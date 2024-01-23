package com.xiilab.servercore.pin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.servercore.pin.entity.PinEntity;
import com.xiilab.servercore.pin.enumeration.PinType;

@Repository
public interface PinRepository extends JpaRepository<PinEntity, Long> {
	List<PinEntity> findByTypeAndUser_Id(PinType type, String id);

	long deleteByTypeAndResourceIdAndUser_Id(PinType type, String resourceId, String id);

	PinEntity findByTypeAndResourceIdAndUser_Id(PinType type, String resourceId, String id);
}
