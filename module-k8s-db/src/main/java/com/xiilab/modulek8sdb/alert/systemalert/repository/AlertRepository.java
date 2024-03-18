package com.xiilab.modulek8sdb.alert.systemalert.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.alert.systemalert.entity.AlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.AlertRole;

public interface AlertRepository extends JpaRepository<AlertEntity, Long>, AlertRepositoryCustom {


}
