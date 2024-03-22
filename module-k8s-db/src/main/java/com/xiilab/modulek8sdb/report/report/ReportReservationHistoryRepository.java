package com.xiilab.modulek8sdb.report.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.report.entity.ReportReservationHistoryEntity;

@Repository
public interface ReportReservationHistoryRepository extends JpaRepository<ReportReservationHistoryEntity, Long> {
}
