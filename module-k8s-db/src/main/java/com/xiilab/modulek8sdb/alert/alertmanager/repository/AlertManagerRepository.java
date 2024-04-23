package com.xiilab.modulek8sdb.alert.alertmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.alert.alertmanager.entity.AlertManagerEntity;

@Repository
public interface AlertManagerRepository extends JpaRepository<AlertManagerEntity, Long> {
	@Query("""
		select t from TB_ALERT_MANAGER t inner join t.alertManagerUserEntityList alertManagerUserEntityList
		where alertManagerUserEntityList.userId = ?1""")
	List<AlertManagerEntity> findByAlertManagerUserEntityList_UserId(String userId);
}
