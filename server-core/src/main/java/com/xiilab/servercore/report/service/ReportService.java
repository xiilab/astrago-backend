package com.xiilab.servercore.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xiilab.modulek8sdb.report.dto.ReportReservationDTO;
import com.xiilab.moduleuser.dto.UserDTO;

public interface ReportService {

	ReportReservationDTO.ResponseDTO saveReportReservation(ReportReservationDTO.RequestDTO reservationDTO);
	
	void deleteReportReservation(long id);

	ReportReservationDTO.ResponseDTO getReportReservationById(long id);

	void updateReportReservationById(long id, ReportReservationDTO.RequestDTO reservationDTO);

	Page<ReportReservationDTO.ResponseDTO> getReportReservationList(Pageable pageable, UserDTO.UserInfo userInfoDTO);

	Page<ReportReservationDTO.ReceiveDTO> getReportReceiveList(Pageable pageable, UserDTO.UserInfo userInfoDTO);

	ReportReservationDTO.DetailDTO getReportReceiveListById(long id, UserDTO.UserInfo userInfoDTO);
}
