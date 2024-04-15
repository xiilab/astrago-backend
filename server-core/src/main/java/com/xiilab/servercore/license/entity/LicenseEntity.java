package com.xiilab.servercore.license.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.LicenseErrorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_LICENSE")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class LicenseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "LICENSE_ID")
	private Long id;
	@Column(name = "LICENSE_VERSION")
	private String version;
	@Column(name = "LICENSE_GPU_COUNT")
	private int gpuCount;
	@Column(name = "LICENSE_START_DATE")
	private LocalDate startDate;
	@Column(name = "LICENSE_END_DATE")
	private LocalDate endDate;
	@Column(name = "LICENSE_KEY")
	private String licenseKey;
	@Column(name = "LICENSE_REG_DATE")
	@CreatedDate
	private LocalDateTime regDate;

	/**
	 * 생성자 패턴으로 licenseKey로 key를 복호화 후 entity 생성
	 * @param licenseKey
	 */
	public LicenseEntity(String licenseKey) {
		//licenseKey 검증
		boolean validateLicensekey = validateLicensekey(licenseKey);
		//invalid한 licensekey일 경우 에러를 발생
		if (!validateLicensekey) {
			throw new RestApiException(LicenseErrorCode.INVALID_LICENSE_KEYS);
		}
		//licenseKey 복호화
		decryptLicensekey(licenseKey);
	}

	/**
	 * LhunAlgorithm을 통한 숫자 합 구하는 메서드
	 *
	 * @param verificationNum 검증할 번호
	 * @return verifiSum
	 */
	public Integer getLhunAlgorithm(String verificationNum) {

		int verifiSum = 0;        //Luhn Algorithm을 통해 얻은 수의 총합

		//홀수번째 자리는 * 2를 하여 더하고 짝 수자리는 그냥 더함
		for (int i = 1; i <= verificationNum.length(); i++) {
			if (i % 2 == 0) {
				verifiSum += Integer.parseInt(verificationNum.substring(i - 1, i));
			} else {
				verifiSum += getdigit(Integer.parseInt(verificationNum.substring(i - 1, i)) * 2);
			}
		}

		return verifiSum;
	}

	/**
	 * 라이센스 키 검증
	 *
	 * @param licensekey 라이센스 키
	 * @return 라이센스 키 정보 (만료일, gpu 개수)
	 */
	private void decryptLicensekey(String licensekey) {
		String licenselimitDate = "20" + (Integer.parseInt(licensekey.substring(0, 7)) / 7); //만료일 221124 -> 20221124로 변
		String limitDate = licenselimitDate.substring(0, 4) + "-" + licenselimitDate.substring(4, 6) + "-" + licenselimitDate.substring(6, 8); //만료일 20221124 -> 2022-11-24로 변경
		int gpuUnit = Integer.parseInt(licensekey.substring(8, 12));   //gpu개수
		this.startDate = LocalDate.now();
		this.endDate = LocalDate.parse(limitDate);
		this.gpuCount = gpuUnit;
		this.licenseKey = licensekey;
	}

	/**
	 * 라이센스 키를 검증하는 메서드
	 *
	 * @param licensekey 라이센스 키
	 * @return 만료일 체크 지나면 false 지나지 않았으면 true
	 */
	public boolean validateLicensekey(String licensekey) {
		boolean result = true;
		String dateNode;
		String verificationNum;
		try {
			//Uyuni-Suite License Key를 계산하기 위해 종료 날짜(limitDate), node수(node)로 나눈 후 다시 합쳐 하나의 String으로 만듬
			dateNode = licensekey.substring(0, 7) + licensekey.substring(8, 12);
			//검증번호(verificationNumber)
			verificationNum = licensekey.substring(19, 23);
		} catch (StringIndexOutOfBoundsException e) {
			//null이 아닌 유효하지 않은 키값이 들어왔을 때 예외 처리
			throw new RestApiException(LicenseErrorCode.INVALID_LICENSE_KEYS);
		}

		//Luhn Algorithm을 통해 얻은 수의 총합
		if ((getLhunAlgorithm(dateNode) + getLhunAlgorithm(verificationNum)) % 10 != 0) {
			result = false;
		}
		return result;
	}

	/**
	 * 검증키를 만들 때, 검증키를 확인할 때 홀수번째 자리들에 * 2를 한 수가 두자리 숫자인지 한자리 숫자인지 확인하고
	 * 두자리 숫자라면 더하여 한자리로 만드는 메서드
	 *
	 * @param doubleDigit 홀수 번째 자리
	 * @return singleDigit 한자리로 변환된 수
	 */
	public int getdigit(int doubleDigit) {
		if (doubleDigit < 10) {
			return doubleDigit;
		} else {
			// share 몫, remain 나머지
			int share = doubleDigit / 10;
			int remain = doubleDigit % 10;
			return share + remain;
		}
	}

	public void checkLicense() {
		LocalDate now = LocalDate.now();
		if (now.isAfter(endDate)) {
			throw new RestApiException(LicenseErrorCode.LICENSE_PAST_EXPIRATION_DATE);
		}
	}
}
