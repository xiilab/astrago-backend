package com.xiilab.modulek8sdb.report.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xiilab.modulek8sdb.report.dto.ReportReservationDTO;
import com.xiilab.modulek8sdb.report.entity.ReportReservationEntity;

public interface ReservationRepositoryCustom {
	Page<ReportReservationEntity> getReportReservationList(String userId, Pageable pageable);

	Page<ReportReservationDTO.ReceiveDTO> getReportReceiveList(String userId, Pageable pageable);

	ReportReservationEntity getReportReceiveListById(long id, String userId);

}
