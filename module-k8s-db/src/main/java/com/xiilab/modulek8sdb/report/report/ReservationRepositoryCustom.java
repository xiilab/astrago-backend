package com.xiilab.modulek8sdb.report.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xiilab.modulek8sdb.report.entity.ReportReservationEntity;

public interface ReservationRepositoryCustom {
	Page<ReportReservationEntity> getReportReservationList(String userId, Pageable pageable);

}
