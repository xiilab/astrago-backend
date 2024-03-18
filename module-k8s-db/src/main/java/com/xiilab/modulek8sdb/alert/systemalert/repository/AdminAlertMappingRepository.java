package com.xiilab.modulek8sdb.alert.systemalert.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.alert.systemalert.entity.AdminAlertMappingEntity;

@Repository
public interface AdminAlertMappingRepository extends JpaRepository<AdminAlertMappingEntity, Long> {
}
