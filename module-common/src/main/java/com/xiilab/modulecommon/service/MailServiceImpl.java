package com.xiilab.modulecommon.service;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.dto.MailDTO;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.modulecommon.util.MailUtils;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
	private final JavaMailSender mailSender;
	private final String SYSTEM = "SYSTEM";
	@Value("${spring.mail.username}")
	private String adminEmailAddr;

	public void sendMail(MailDTO mailDTO) {
		try {

			// String testUser = createSection(mailDTO.getTitle(),
			// 	createUserBody("testUser", mailDTO.getReceiverEmail())
			// );

			MailUtils sendMail = new MailUtils(mailSender);
			sendMail.setSubject(mailDTO.getTitle());
			sendMail.setText(mailDTO.getContent());
			sendMail.setTo(mailDTO.getSenderEmail());
			sendMail.setFrom(adminEmailAddr, SYSTEM);
			sendMail.send();
		} catch (MessagingException | UnsupportedEncodingException e) {
			throw new RestApiException(CommonErrorCode.MAIL_SEND_FAILED);
		}
	}

	// private String createSection(String title, String body) {
	// 	return """
	// 		<table
	// 			class="container"
	// 			cellpadding="0"
	// 			cellspacing="0"
	// 			style="
	// 				width: 600px;
	// 				margin: 0 auto;
	// 			"
	// 		>
	// 		    <!-- content 영역 : 본문 -->
	// 		            <tr>
	// 		              <td style="padding-bottom: 26px">
	// 		                <table
	// 		                	style="
	// 		                		background-color: #ffffff;
	// 		                		padding: 26px 40px 50px;
	// 		                	"
	// 		                >
	// 		                </table>
	// 		              </td>
	// 		            </tr>
	// 		    <!-- footer 영역 : 저작권 표시-->
	// 		            <tr>
	// 		              <td>
	// 		                <table
	// 		                        style="
	// 		                        text-align: center;
	// 		                        font-size: 11px;
	// 		                        font-weight: 400;
	// 		                        line-height: 16px;
	// 		                        color: #90919e;
	// 		                      "
	// 		                >
	// 		                  <tr>
	// 		                    <td>
	// 		                      자세한 사항은 astrago 관리자 페이지에서 확인해주세요
	// 		                    </td>
	// 		                  </tr>
	// 		                  <tr>
	// 		                    <td>
	// 		                      XIILAB
	// 		                      <!-- 링크 넣어야 함 -->
	// 		                      <a href="">astrago</a> Corp. All rights reserved.
	// 		                    </td>
	// 		                  </tr>
	// 		                </table>
	// 		              </td>
	// 		            </tr>
	// 		</table>
	// 		""".formatted(title, body);
	// }
	//
	// private String createUserBody(String userName, String userMail) {
	// 	return """
	// 		<!-- 메인 문구 -->
	// 		              <tr>
	// 		                <td
	// 		                        style="
	// 		                        text-align: center;
	// 		                        padding-bottom: 13px;
	// 		                        font-weight: 700;
	// 		                        line-height: 24px;
	// 		                      "
	// 		                >
	// 		                  <span style="color: #5b29c7">%s(%s)</span>님이 회원가입을 요청하였습니다.
	// 		                </td>
	// 		              </tr>
	// 		""".formatted(userName, userMail);
	// }

}
