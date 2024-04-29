package com.xiilab.modulecommon.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
	private final String ASTRAGO = "ASTRAGO";
	@Value("${spring.mail.username}")
	private String adminEmailAddr;

	public void sendMail(MailDTO mailDTO) {
		try {
			if(!StringUtils.isBlank(mailDTO.getReceiverEmail())){
				MailUtils sendMail = new MailUtils(mailSender);
				sendMail.setSubject(mailDTO.getSubject());
				sendMail.setTo(mailDTO.getReceiverEmail());
				sendMail.setFrom(adminEmailAddr, ASTRAGO);

				sendMail.setText(
					createBody(
						createTitle(mailDTO.getTitle()) +
							createSubTitle(mailDTO.getSubTitle()) +
							createContentTitle(StringUtils.isBlank(mailDTO.getContentTitle()) ? "" : String.format(mailDTO.getContentTitle(), adminEmailAddr)) +
							createContents(mailDTO.getContents()) +
							createContentFooter(mailDTO.getFooter())
						, createFooter()
					)
				);

				sendMail.setLogo("image/logo.png");
				sendMail.setIcon("image/icon.png");
				sendMail.send();
			}
		} catch (MessagingException | UnsupportedEncodingException e) {
			throw new RestApiException(CommonErrorCode.MAIL_SEND_FAILED);
		}
	}

	private String createBody(String title, String footer){
		return """
			<!-- 배경 -->
			<table
			    class="wrapper"
			    cellpadding="0"
			    cellspacing="0"
			    style="
			        width: -webkit-fill-available;
			        border: 0;
			        font-family: 'Noto Sans KR', sans-serif;
			        padding: 26px 40px 32px;
			        background-color: #ffffff;">
			    <tr>
			        <td>
			            <table
			                class="container"
			                cellpadding="0"
			                cellspacing="0"
			                style="
			                    width: 600px;
			                    margin: 0 auto;
			                    background: linear-gradient(
			                        to bottom,
			                        #5b29c7 0,
			                        #5b29c7 200px,
			                        #f0f0f6 200px,
			                        #f0f0f6);
			                    padding: 26px 50px 32px;">
			                <!-- header 영역 : 로고 -->
			                <tr>
			                    <td style="padding-bottom: 24px; text-align: center">
			                        <img
			                            width="98"
			                            height="28"
			                            src="cid:logo" alt="Logo">
			                    </td>
			                </tr>
			                <!-- content 영역 : 본문 -->
			                <tr>
			                    <td style="padding-bottom: 26px">
			                        <table
			                            style="
			                                width: 500px;
			                                background-color: #ffffff;
			                                padding: 26px 40px 50px;">
			                            <!-- icon -->
			                            <tr>
			                                <td colspan="2" style="text-align: center; padding-bottom: 14px">
			                                    <img
			                                        width="48"
			                                        height="48"
			                                        src="cid:icon" alt="Icon">
			                                </td>
			                            </tr>
			                            %s
			                        </table>
			                    </td>
			                </tr>
			                <!-- footer -->
			                %s
			            </table>
			        </td>
			    </tr>
			</table>
			""".formatted(title, footer);
	}

	private String createTitle(String title){
		return """
            <!-- 제목 -->
            <tr>
                <td
					colspan="2"
                    style="
                        text-align: center;
                        padding-bottom: 24px;
                        font-size: 20px;
                        font-weight: 700;
                        line-height: 24px;
                        color: #5b29c7;">
                    %s
                </td>
            </tr>
			""".formatted(title);
	}

	private String createSubTitle(String text){
		return """
            <tr>
                <td
                    style="
                        text-align: center;
                        padding-bottom: 13px;
                        font-weight: 700;
                        line-height: 24px;">
                    <!--
                    메인문구, 줄바꿈은 br tag 사용<br />
                    강조 표시<span style="color: #5b29c7">span</span> tag 사용
                     -->
			    </td>
			</tr>
			""".formatted(text);
	}

	private String createSubText(String subText){
		return """
            <tr>
                <td
                    style="
                        text-align: center;
                        font-size: 14px;
                        font-weight: 400;
                        line-height: 24px;">
                    %s
                </td>
            </tr>
			""".formatted(subText);
	}
	private String createSubTable(String subTable){
		return """
            <tr>
                <td>
                    <table
                        cellpadding="0"
                        cellspacing="0"
                        style="
                            width: 420px;
                            margin-top: 40px;
                            border-top: solid 1px #afadb4;
                            border-bottom: solid 1px #afadb4;
                            padding: 28px 0 32px;
                            border-spacing: 10px;">
                        <colgroup>
                            <col style="width: 40px" />
                            <col style="width: 70px" />
                        </colgroup>
                        <!-- 테이블 제목 row -->
                        %s
                    </table>
                </td>
            </tr>
			""".formatted(subTable);
	}
	private String createContentTitle(String subTitle){
		return """
            <tr style="text-align: center">
                <td colspan="2">
                    %s
                </td>
            </tr>
			""".formatted(subTitle);
	}
	private String createContentFooter(String footer){
		if(footer.equals("")){
			return "";
		}else{
			return """
            <tr style="text-align: center">
                <td colspan="2">
                    %s
                </td>
            </tr>
			""".formatted(footer);
		}
	}
	private String createContents(List<MailDTO.Content> contents){
		String result = "";
		if(contents != null){
			for(MailDTO.Content content : contents){
				result += """
				         <tr>
				             <td style="max-width: 150px; font-weight: 400">
				                 %s
				             </td>
				             <td style="max-width: 250px; text-align: end">
				                 %s
				             </td>
				         </tr>
				""".formatted(content.getCol1(), content.getCol2());
			}
		}
		return result;
	}
	private String createFooter(){
		return """
            <!-- footer 영역 : 저작권 표시-->
            <tr>
                <td>
                    <table
                        style="
                            width: 500px;
                            text-align: center;
                            font-size: 11px;
                            font-weight: 400;
                            line-height: 16px;
                            color: #90919e;">
                        <tr>
                            <td>
                                자세한 사항은 astrago 관리자 페이지에서 확인해주세요
                            </td>
                        </tr>
                        <tr>
                            <td>
                                XIILAB
                                <!-- 링크 넣어야 함 -->
                                <a href="">astrago</a> Corp. All rights reserved.
                            </td>
                        </tr>
                    </table>
			    </td>
			</tr>
			""";
	}
}
