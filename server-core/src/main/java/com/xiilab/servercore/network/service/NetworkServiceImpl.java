package com.xiilab.servercore.network.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.NetworkErrorCode;
import com.xiilab.modulek8sdb.common.enums.NetworkCloseYN;
import com.xiilab.modulek8sdb.network.entity.NetworkEntity;
import com.xiilab.modulek8sdb.network.repository.NetworkRepository;
import com.xiilab.servercore.network.dto.ModifyNetworkDTO;
import com.xiilab.servercore.network.dto.PrivateRepositoryUrlDto;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NetworkServiceImpl implements NetworkService {
	private final NetworkRepository networkRepository;

	@Override
	public NetworkCloseYN getNetworkStatus() {
		NetworkEntity network = networkRepository.findTopBy(Sort.by("networkId").descending());
		if (network == null) {
			throw new RestApiException(NetworkErrorCode.NETWORK_NOT_FOUND);
		}
		return network.getNetworkCloseYN();
	}

	@Override
	@Transactional
	public void modifyNetworkStatus(ModifyNetworkDTO modifyNetworkDTO) {
		NetworkEntity network = networkRepository.findTopBy(Sort.by("networkId").descending());
		if (network == null) {
			throw new RestApiException(NetworkErrorCode.NETWORK_NOT_FOUND);
		}
		network.modifyNetworkStatus(modifyNetworkDTO.getNetworkStatus());
	}

	// @Override
	// public PrivateRepositoryUrlDto getPrivateRepositoryUrl() {
	// 	NetworkEntity network = networkRepository.findTopBy(Sort.by("networkId").descending());
	// 	PrivateRepositoryUrlDto privateRepositoryUrlDto = PrivateRepositoryUrlDto.builder()
	// 		.privateRepositoryUrl(network.getPrivateRepositoryUrl())
	// 		.build();
	// 	return privateRepositoryUrlDto;
	// }
	//
	// @Override
	// @Transactional
	// public void modifyPrivateRepositoryUrl(PrivateRepositoryUrlDto privateRepositoryUrlDto) {
	// 	NetworkEntity network = networkRepository.findTopBy(Sort.by("networkId").descending());
	// 	network.modifyPrivateRepositoryUrl(privateRepositoryUrlDto.getPrivateRepositoryUrl());
	// }
	//
	// @Override
	// @Transactional
	// public void deletePrivateRepositoryUrl() {
	// 	NetworkEntity network = networkRepository.findTopBy(Sort.by("networkId").descending());
	// 	network.modifyPrivateRepositoryUrl("");
	// }
	//
	// @Override
	// @Transactional
	// public void createPrivateRepositoryUrl(PrivateRepositoryUrlDto privateRepositoryUrlDto) {
	// 	NetworkEntity network = networkRepository.findTopBy(Sort.by("networkId").descending());
	// 	network.modifyPrivateRepositoryUrl(privateRepositoryUrlDto.getPrivateRepositoryUrl());
	// }
}
