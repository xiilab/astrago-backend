package com.xiilab.serverbatch.job;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulek8s.node.dto.ResponseDTO;
import com.xiilab.modulek8s.node.repository.NodeRepository;
import com.xiilab.modulek8sdb.version.entity.CompatibleFrameworkVersionEntity;
import com.xiilab.modulek8sdb.version.entity.FrameWorkVersionEntity;
import com.xiilab.modulek8sdb.version.repository.CompatibleFrameWorkVersionRepository;
import com.xiilab.modulek8sdb.version.repository.FrameWorkVersionRepository;
import com.xiilab.modulek8sdb.version.repository.MaxCudaVersionRepository;
import com.xiilab.modulek8sdb.version.repository.MinCudaVersionRepository;

@Component
public class FrameworkVersionJob extends QuartzJobBean{
	@Autowired
	private FrameWorkVersionRepository frameWorkVersionRepository;
	@Autowired
	private MaxCudaVersionRepository maxCudaVersionRepository;
	@Autowired
	private MinCudaVersionRepository minCudaVersionRepository;
	@Autowired
	private CompatibleFrameWorkVersionRepository compatibleFrameWorkVersionRepository;
	@Autowired
	private NodeRepository nodeRepository;


	@Override
	@Transactional
	protected void executeInternal(JobExecutionContext context) {
		List<ResponseDTO.WorkerNodeDriverInfo> workerNodeDriverInfos = nodeRepository.getWorkerNodeDriverInfos();
		// 중복된 값들을 담을 Set
		Set<FrameWorkVersionEntity> duplicatedVersions = new HashSet<>();

		for (ResponseDTO.WorkerNodeDriverInfo workerNodeDriverInfo : workerNodeDriverInfos) {
			float driverMajor = Float.parseFloat(workerNodeDriverInfo.getDriverMajor());
			float driverMinor = Float.parseFloat(workerNodeDriverInfo.getDriverMinor());
			float driverRev = Float.parseFloat(workerNodeDriverInfo.getDriverRev());

			String computeMajor = workerNodeDriverInfo.getComputeMajor();
			String computeMinor = workerNodeDriverInfo.getComputeMinor();
			float version = Float.parseFloat(computeMajor + "." + computeMinor);

			String maxCudaVersion = maxCudaVersionRepository.getMaxCudaVersion(driverMajor, driverMinor, driverRev);
			String minCudaVersion = minCudaVersionRepository.getMinCudaVersion(version);

			// 현재 workerNodeDriverInfo에 대한 호환 가능한 프레임워크 버전 리스트
			List<FrameWorkVersionEntity> compatibleFrameworkVersion = frameWorkVersionRepository.getCompatibleFrameworkVersion(getSubstringBeforeFirstDot(maxCudaVersion), getSubstringBeforeFirstDot(minCudaVersion));

			// 중복된 값들을 추출하여 Set에 추가
			if (!duplicatedVersions.isEmpty()) {
				duplicatedVersions.retainAll(compatibleFrameworkVersion);
			} else {
				duplicatedVersions.addAll(compatibleFrameworkVersion);
			}
		}

		// 중복된 값들 출력
		// 기존 데이터 삭제
		compatibleFrameWorkVersionRepository.deleteAll();
		for (FrameWorkVersionEntity version : duplicatedVersions) {
			CompatibleFrameworkVersionEntity compatibleFrameworkVersionEntity = CompatibleFrameworkVersionEntity.builder()
				.frameWorkVersionEntity(version)
				.build();
			compatibleFrameWorkVersionRepository.save(compatibleFrameworkVersionEntity);
		}
	}
	public static float getSubstringBeforeFirstDot(String str) {
		String[] split = str.split("\\.");
		if (split.length > 1) {
			return Float.parseFloat(split[0] + "." + split[1]);
		}
		return Float.parseFloat(str);
	}
}
