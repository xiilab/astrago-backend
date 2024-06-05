package com.xiilab.servercore.smtp.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.dto.MailDTO;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.SmtpErrorCode;
import com.xiilab.modulecommon.service.MailService;
import com.xiilab.modulek8sdb.smtp.dto.SmtpDTO;
import com.xiilab.modulek8sdb.smtp.entity.SmtpEntity;
import com.xiilab.modulek8sdb.smtp.repository.SmtpRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SmtpFacadeServiceImpl implements SmtpFacadeService {
	private final SmtpRepository smtpRepository;
	private final MailService mailService;

	@Override
	public void saveSmtp(SmtpDTO.RequestDTO requestDTO) {
		try{
			SmtpEntity smtpEntity = requestDTO.toEntity();
			Long saveEntityId = smtpRepository.save(smtpEntity).getId();

			if(!validationCheckSmtp(smtpEntity)){
				deleteSmtpById(saveEntityId);
				throw new RestApiException(SmtpErrorCode.SMTP_INFO_MISS);
			}

		}catch (IllegalArgumentException e){
			throw new RestApiException(SmtpErrorCode.SMTP_SAVE_FAIL);
		}catch (DataIntegrityViolationException e){
			throw new RestApiException(SmtpErrorCode.SMTP_DUPLICATION_USER_NAME);
		}
	}

	@Override
	public List<SmtpDTO.ResponseDTO> getSmtp() {
		try{
			List<SmtpEntity> smtpEntityList = smtpRepository.findAll();
			return smtpEntityList.stream().map(SmtpDTO.ResponseDTO::new).toList();
		}catch (IllegalArgumentException e){
			throw new RestApiException(SmtpErrorCode.SMTP_NOT_FOUND);
		}
	}

	@Override
	public void deleteSmtpById(long id) {
		try{
			smtpRepository.deleteById(id);
		}catch (IllegalArgumentException e){
			throw new RestApiException(SmtpErrorCode.SMTP_DELETE_FAIL);
		}
	}

	private boolean validationCheckSmtp(SmtpEntity smtpEntity){
		com.xiilab.modulecommon.dto.SmtpDTO smtpDTO = com.xiilab.modulecommon.dto.SmtpDTO.builder()
			.host(smtpEntity.getHost())
			.port(smtpEntity.getPort())
			.username(smtpEntity.getUserName())
			.password(smtpEntity.getPassword())
			.build();

		MailDTO mailDTO = MailDTO.builder()
			.title("TEST_KIM")
			.subject("TST_KIM")
			.subTitle("TEST_KIM")
			.receiverEmail("y.kim@xiilab.com")
			.footer("TEST_KIM").build();

		return mailService.sendMail(mailDTO, smtpDTO);
	}
}
