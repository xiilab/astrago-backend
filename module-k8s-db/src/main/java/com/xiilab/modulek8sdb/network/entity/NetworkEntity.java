package com.xiilab.modulek8sdb.network.entity;

import org.hibernate.annotations.Comment;

import com.xiilab.modulek8sdb.common.enums.NetworkCloseYN;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "TB_NETWORK")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NetworkEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "NETWORK_ID")
	private Long networkId;

	@Enumerated(EnumType.STRING)
	@Column(name = "NETWORK_CLOSE_YN")
	private NetworkCloseYN networkCloseYN;

	@Column(name = "INIT_CONTAINER_HUB")
	@Comment("init container 용 도커 이미지 경로")
	private String initContainerHub;
	@Column(name = "INIT_CONTAINER_HARBOR")
	@Comment("init container 용 폐쇄망 도커 이미지 경로")
	private String initContainerHarbor;

	@Column(name = "CONNECTION_TEST_HUB")
	@Comment("스토리지 생성 시 연결 테스트 용 도커 이미지 경로")
	private String connectionTestHub;
	@Column(name = "CONNECTION_TEST_HARBOR")
	@Comment("스토리지 생성 시 연결 테스트 용 폐쇄망 도커 이미지 경로")
	private String connectionTestHarbor;

	@Column(name = "LOCAL_VOLUME_HUB")
	@Comment("로컬 데이터 셋, 모델 용 도커 이미지 경로")
	private String localVolumeHub;
	@Column(name = "LOCAL_VOLUME_HARBOR")
	@Comment("로컬 데이터 셋, 모델 용 폐쇄망 도커 이미지 경로")
	private String localVolumeHarbor;

	public String getInitContainerURL(){
		return networkCloseYN == NetworkCloseYN.Y ? initContainerHarbor : initContainerHub;
	}
	public String getConnectionTestURL(){
		return networkCloseYN == NetworkCloseYN.Y ? connectionTestHarbor : connectionTestHub;
	}
	public String getLocalVolumeURL(){
		return networkCloseYN == NetworkCloseYN.Y ? localVolumeHarbor : localVolumeHub;
	}
}
