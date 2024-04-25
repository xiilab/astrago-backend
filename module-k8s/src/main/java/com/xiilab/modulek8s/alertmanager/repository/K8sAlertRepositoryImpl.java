package com.xiilab.modulek8s.alertmanager.repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Repository;

import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.modulek8s.config.K8sAdapter;

import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.openshift.api.model.monitoring.v1.PrometheusRule;
import io.fabric8.openshift.api.model.monitoring.v1.PrometheusRuleBuilder;
import io.fabric8.openshift.api.model.monitoring.v1.Rule;
import io.fabric8.openshift.api.model.monitoring.v1.RuleBuilder;
import io.fabric8.openshift.client.OpenShiftClient;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class K8sAlertRepositoryImpl implements K8sAlertRepository{
	private static final String PROMETHEUS = "prometheus";
	private static final String NAMESPACE = "astrago";
	private final K8sAdapter k8sAdapter;

	@Override
	public void createPrometheusRule(long alertId, List<String> exprList) {
		String prometheusName = setName(alertId);
		if (Objects.nonNull(exprList)) {
			PrometheusRule prometheusRule = new PrometheusRuleBuilder()
				// metaData 설정
				.withNewMetadata().withName(prometheusName)
				.withLabels(Map.of("app", "kube-prometheus-stack", "release", PROMETHEUS))
				.endMetadata()
				.withNewSpec()
				// group 설정
				.addNewGroup()
				.withName(prometheusName).addAllToRules(createExpr(exprList, prometheusName))
				.endGroup()
				.endSpec()
				.build();

			createPrometheusRule(prometheusRule);
		}
	}

	@Override
	public void deletePrometheusRule(long alertId){
		String name = setName(alertId);
		try (OpenShiftClient client = k8sAdapter.defaultOpenShiftClient()) {
			// 해당 name의 prometheusRule 삭제
			client.monitoring().prometheusRules().inNamespace(NAMESPACE).withName(name).delete();
		}catch (KubernetesClientException e){
			throw new K8sException(CommonErrorCode.ALERT_MANAGER_K8S_DELETE_FAIL);
		}
	}

	@Override
	public void updatePrometheusRule(long alertId, List<String> exprList) {
		// 기존 PrometheusRule 삭제
		deletePrometheusRule(alertId);
		// 수정된 PrometheusRule 추가
		createPrometheusRule(alertId, exprList);
	}

	@Override
	public boolean validationCheck(long id){
		try (OpenShiftClient client = k8sAdapter.defaultOpenShiftClient()) {
			return !client.monitoring()
				.prometheusRules()
				.inNamespace(NAMESPACE)
				.withName(PROMETHEUS +"-" + id).isReady();

		}catch (KubernetesClientException e){
			throw new K8sException(CommonErrorCode.ALERT_MANAGER_ADD_RULE_FAIL);
		}
	}

	/**
	 * prometheusRule 생성하는 메소드
	 * @param prometheusRule 생성될 prometheusRule
	 * @return 생성 여부
	 */
	private void createPrometheusRule(PrometheusRule prometheusRule){
		try (OpenShiftClient client = k8sAdapter.defaultOpenShiftClient()) {
			// PrometheusRule 생성
			client.monitoring().prometheusRules().inNamespace(NAMESPACE).resource(prometheusRule).create();
		}catch (KubernetesClientException e){
			throw new K8sException(CommonErrorCode.ALERT_MANAGER_ADD_RULE_FAIL);
		}
	}
	/**
	 * prometheusRule 규칙 리스트 생성 메소드
	 * @param exprList prometheusRule에 적용될 Expr List
	 * @return 생성된 Rule 리스트
	 */
	private List<Rule> createExpr(List<String> exprList, String name) {
		// split[0] 쿼리 split[1] 지속시간 split[2] alertName split[3] nodeName
		return exprList.stream().map(expr -> {
			String[] split = expr.split("~");
			// 규칙 설정 및 for설정,
			return new RuleBuilder()
				.withLabels(Map.of("app", NAMESPACE, "ruleName", name, "nodeName", split[3]))
				.withAlert(split[2]).withNewExpr(split[0]).withFor(split[1])
				.withAnnotations(Map.of("value", "{{$value}}")).build();
		}).toList();
	}

	/**
	 * 프로메테우스에서 사용될 Name 만들어주는 메소드
	 * @param id 생성된 Monitor id
	 * @return Prometheus에서 사용될 Name
	 */
	private String setName(Long id){
		return PROMETHEUS + "-" + id;
	}
}
