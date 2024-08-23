package com.xiilab.modulek8sdb.deploy.repository;

import static com.xiilab.modulek8sdb.deploy.entity.QDeployEntity.*;

import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.deploy.dto.DeploySearchCondition;
import com.xiilab.modulek8sdb.deploy.entity.DeployEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DeployRepositoryImpl implements DeployRepositoryCustom{
	private final JPAQueryFactory queryFactory;

	@Override
	public PageImpl<DeployEntity> getDeploys(String workspaceResourceName, DeploySearchCondition deploySearchCondition,
		PageRequest pageRequest) {
		List<DeployEntity> deployEntities = queryFactory.selectFrom(deployEntity)
			.where(
				eqWorkspaceName(workspaceResourceName),
				eqWorkloadStatus(deploySearchCondition.getWorkloadStatus()),
				eqName(deploySearchCondition.getSearchText()),
				deleteYnEqN()
			)
			.orderBy(deployEntity.createdAt.desc())
			.offset(pageRequest.getOffset())
			.limit(pageRequest.getPageSize())
			.fetch();
		Long count = queryFactory.select(deployEntity.count())
			.from(deployEntity)
			.where(
				eqWorkspaceName(workspaceResourceName),
				eqWorkloadStatus(deploySearchCondition.getWorkloadStatus()),
				eqName(deploySearchCondition.getSearchText()),
				deleteYnEqN()
			).fetchOne();
		return new PageImpl<>(deployEntities, pageRequest, count);
	}

	@Override
	public PageImpl<DeployEntity> getDeploysUsingModel(PageRequest pageRequest, Long modelRepoId) {
		List<DeployEntity> deployEntities = queryFactory.selectFrom(deployEntity)
			.where(
				deployEntity.modelRepoEntity.id.eq(modelRepoId),
				deleteYnEqN()
			)
			.orderBy(deployEntity.createdAt.desc())
			.offset(pageRequest.getOffset())
			.limit(pageRequest.getPageSize())
			.fetch();
		Long count = queryFactory.select(deployEntity.count())
			.from(deployEntity)
			.where(
				deployEntity.modelRepoEntity.id.eq(modelRepoId),
				deleteYnEqN()
			).fetchOne();
		return new PageImpl<>(deployEntities, pageRequest, count);
	}

	private static BooleanExpression eqWorkloadStatus(WorkloadStatus deploySearchCondition) {
		if(deploySearchCondition == null){
			return null;
		}
		return deployEntity.workloadStatus.eq(deploySearchCondition);
	}

	private static BooleanExpression eqWorkspaceName(String workspaceResourceName) {
		if (workspaceResourceName == null) {
			return null;
		}
		return deployEntity.workspaceResourceName.eq(workspaceResourceName);
	}
	private static BooleanExpression eqName(String deployName){
		if(deployName == null || deployName.isBlank()){
			return null;
		}
		return deployEntity.name.contains(deployName);
	}
	private static BooleanExpression deleteYnEqN() {
		return deployEntity.deleteYN.eq(DeleteYN.N);
	}
}
