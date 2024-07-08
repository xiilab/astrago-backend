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

	@Column(name = "PRIVATE_REPOSITORY_URL")
	private String privateRepositoryUrl;

	@Column(name = "INIT_CONTAINER_IMAGE_URL")
	@Comment("init container 용 도커 이미지 경로")
	private String initContainerImageUrl;

	@Column(name = "LOCAL_VOLUME_IMAGE_URL")
	@Comment("로컬 데이터 셋, 모델 용 도커 이미지 경로")
	private String localVolumeImageUrl;

	public String getInitContainerURL(){
		return networkCloseYN == NetworkCloseYN.Y ? privateRepositoryUrl + initContainerImageUrl : initContainerImageUrl;
	}
	public String getLocalVolumeImageURL(){
		return networkCloseYN == NetworkCloseYN.Y ? privateRepositoryUrl + localVolumeImageUrl : localVolumeImageUrl;
	}
	public void modifyNetworkStatus(NetworkCloseYN networkCloseYN){
		this.networkCloseYN = networkCloseYN;
	}
	public void modifyPrivateRepositoryUrl(String privateRepositoryUrl){
		this.privateRepositoryUrl = privateRepositoryUrl;
	}
}
