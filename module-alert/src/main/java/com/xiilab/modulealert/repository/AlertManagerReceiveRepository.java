package com.xiilab.modulealert.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.modulealert.entity.AlertManagerReceiveEntity;

@Repository
public interface AlertManagerReceiveRepository extends JpaRepository<AlertManagerReceiveEntity, Long> {
	Optional<List<AlertManagerReceiveEntity>> findAlertEntityByCurrentTime(String currentTime);
	List<AlertManagerReceiveEntity> findByAlertManagerAlertManagerUserEntityListUserId(String userId);
}
