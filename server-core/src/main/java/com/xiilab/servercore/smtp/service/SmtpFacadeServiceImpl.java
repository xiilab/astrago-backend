package com.xiilab.servercore.smtp.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.modulek8sdb.smtp.dto.SmtpDTO;
import com.xiilab.modulek8sdb.smtp.entity.SmtpEntity;
import com.xiilab.modulek8sdb.smtp.repository.SmtpRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SmtpFacadeServiceImpl implements SmtpFacadeService {
	private final SmtpRepository smtpRepository;
	@Override
	public void saveSmtp(SmtpDTO.RequestDTO requestDTO) {
		try{
			SmtpEntity entity = requestDTO.toEntity();
			smtpRepository.save(entity);
		}catch (IllegalArgumentException e){
			throw new RestApiException(CommonErrorCode.MAIL_SAVE_FAIL);
		}
	}

	@Override
	public List<SmtpDTO.ResponseDTO> getSmtp() {
		try{
			List<SmtpEntity> smtpEntityList = smtpRepository.findAll();
			return smtpEntityList.stream().map(SmtpDTO.ResponseDTO::new).toList();
		}catch (IllegalArgumentException e){
			throw new RestApiException(CommonErrorCode.MAIL_SAVE_FAIL);
		}
	}

	@Override
	public void deleteSmtpById(long id) {
		try{
			smtpRepository.deleteById(id);
		}catch (IllegalArgumentException e){
			throw new RestApiException(CommonErrorCode.MAIL_SAVE_FAIL);
		}
	}
}
