package com.xiilab.modulek8sdb.smtp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.smtp.entity.SmtpEntity;

@Repository
public interface SmtpRepository  extends JpaRepository<SmtpEntity, Long> {
}
