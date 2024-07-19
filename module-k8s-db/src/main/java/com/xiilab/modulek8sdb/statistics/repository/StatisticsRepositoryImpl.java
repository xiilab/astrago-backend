package com.xiilab.modulek8sdb.statistics.repository;

import static com.xiilab.modulek8sdb.code.entity.QCodeEntity.*;
import static com.xiilab.modulek8sdb.credential.entity.QCredentialEntity.*;
import static com.xiilab.modulek8sdb.dataset.entity.QDataset.*;
import static com.xiilab.modulek8sdb.workload.history.entity.QJobEntity.*;
import static com.xiilab.modulek8sdb.workload.history.entity.QWorkloadEntity.*;
import static com.xiilab.modulek8sdb.workspace.entity.QResourceQuotaEntity.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulek8sdb.statistics.dto.StatisticsDTO;
import com.xiilab.modulek8sdb.workspace.enums.ResourceQuotaStatus;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StatisticsRepositoryImpl implements StatisticsRepository {
	private final JPAQueryFactory jpaQueryFactory;
	private final String COUNT = "count";
	private final String USER_ID = "userId";

	@Override
	public List<StatisticsDTO.UsageDTO> resourceUsageDTOList() {

		return jpaQueryFactory
			.select(Projections.fields(StatisticsDTO.UsageDTO.class,
				jobEntity.gpuRequest.sum().as("gpuUsage"),
				jobEntity.cpuRequest.sum().as("cpuUsage"),
				jobEntity.memRequest.sum().as("memUsage"),
				workloadEntity.creatorId.as(USER_ID)
			))
			.from(workloadEntity)
			.leftJoin(jobEntity).on(workloadEntity.id.eq(jobEntity.id))
			.where(workloadEntity.creatorId.isNotNull())
			.groupBy(workloadEntity.creatorId)
			.fetch();
	}
	@Override
	public List<StatisticsDTO.ResourceRequestDTO> resourceRequestDTOList(){
		return jpaQueryFactory
			.select(Projections.fields(StatisticsDTO.ResourceRequestDTO.class,
				resourceQuotaEntity.gpuReq.sum().as("gpuRequest"),
				resourceQuotaEntity.cpuReq.sum().as("cpuRequest"),
				resourceQuotaEntity.memReq.sum().as("memRequest"),
				resourceQuotaEntity.regUser.regUserId.as(USER_ID)
				))
			.from(resourceQuotaEntity)
			.where(resourceQuotaEntity.status.eq(ResourceQuotaStatus.APPROVE))
			.groupBy(resourceQuotaEntity.regUser.regUserId)
			.fetch();
	}
	@Override
	public List<StatisticsDTO.CountDTO> getUserResourceRequestCount(){
		return jpaQueryFactory
			.select(Projections.fields(StatisticsDTO.CountDTO.class,
				resourceQuotaEntity.status.count().as(COUNT),
				resourceQuotaEntity.regUser.regUserId.as(USER_ID)
				))
			.from(resourceQuotaEntity)
			.groupBy(resourceQuotaEntity.regUser.regUserId)
			.fetch();
	}
	@Override
	public List<StatisticsDTO.CountDTO> resourceQuotaApproveCount(){
		return jpaQueryFactory
			.select(Projections.fields(StatisticsDTO.CountDTO.class,
				resourceQuotaEntity.count().as(COUNT),
				resourceQuotaEntity.regUser.regUserId.as(USER_ID)
			))
			.from(resourceQuotaEntity)
			.where(resourceQuotaEntity.status.eq(ResourceQuotaStatus.APPROVE))
			.groupBy(resourceQuotaEntity.regUser.regUserId)
			.fetch();
	}
	@Override
	public List<StatisticsDTO.CountDTO> resourceQuotaRejectCount(){
		return jpaQueryFactory
			.select(Projections.fields(StatisticsDTO.CountDTO.class,
				resourceQuotaEntity.count().as(COUNT),
				resourceQuotaEntity.regUser.regUserId.as(USER_ID)
			))
			.from(resourceQuotaEntity)
			.where(resourceQuotaEntity.status.eq(ResourceQuotaStatus.REJECT))
			.groupBy(resourceQuotaEntity.regUser.regUserId)
			.fetch();
	}
	@Override
	public List<StatisticsDTO.CountDTO> getCreateWorkloadCount(){
		return jpaQueryFactory
			.select(Projections.fields(StatisticsDTO.CountDTO.class,
				workloadEntity.count().as(COUNT),
				workloadEntity.creatorId.as(USER_ID)
				))
			.from(workloadEntity)
			.where(workloadEntity.creatorId.isNotNull())
			.groupBy(workloadEntity.creatorId)
			.fetch();
	}
	@Override
	public List<StatisticsDTO.CountDTO> getCreateCodeCount(){
		return jpaQueryFactory
			.select(Projections.fields(StatisticsDTO.CountDTO.class,
				codeEntity.count().as(COUNT),
				codeEntity.regUser.regUserId.as(USER_ID)
				))
			.from(codeEntity)
			.groupBy(codeEntity.regUser.regUserId)
			.fetch();
	}
	@Override
	public List<StatisticsDTO.CountDTO> getCreateDatasetCount(){
		return jpaQueryFactory
			.select(Projections.fields(StatisticsDTO.CountDTO.class,
				dataset.count().as(COUNT),
				dataset.regUser.regUserId.as(USER_ID)
				))
			.from(dataset)
			.groupBy(dataset.regUser.regUserId)
			.fetch();
	}
	@Override
	public List<StatisticsDTO.CountDTO> getCreateCredentialCount() {
		return jpaQueryFactory
			.select(Projections.fields(StatisticsDTO.CountDTO.class,
				credentialEntity.count().as(COUNT),
				credentialEntity.regUser.regUserId.as(USER_ID)
			))
			.from(credentialEntity)
			.groupBy(credentialEntity.regUser.regUserId)
			.fetch();
	}

}
