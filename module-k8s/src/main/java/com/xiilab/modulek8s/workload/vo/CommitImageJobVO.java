package com.xiilab.modulek8s.workload.vo;

import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.PodTemplateSpec;
import io.fabric8.kubernetes.api.model.PodTemplateSpecBuilder;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.JobSpec;
import io.fabric8.kubernetes.api.model.batch.v1.JobSpecBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommitImageJobVO {
	private final String workspace;
	private final String workload;
	private final String nodeName;
	private final String username;
	private final String imageName;
	private final String imageTag;

	/**
	 * 워크로드의 image를 commit하는 job의 manifest를 생성하는 메소드
	 *
	 * @return job manifest 객체
	 */
	public Job getJobManifest() {
		return new JobBuilder()
			.withMetadata(getMetadata())
			.withSpec(getSpec())
			.build();
	}

	/**
	 * job의 metadata를 생성하는 메소드
	 *
	 * @return objectMeta 객체
	 */
	private ObjectMeta getMetadata() {
		return new ObjectMetaBuilder()
			.withName(workload + "-commit")
			.withNamespace("astrago")
			.withLabels(
				Map.of(
					"userId", username,
					"workload", workload,
					"workspace", workspace
				)
			)
			.build();
	}

	/**
	 * job의 spec을 생성하는 메소드
	 *
	 * @return
	 */
	private JobSpec getSpec() {
		return new JobSpecBuilder()
			.withTemplate(getPodTemplateSpec())
			.withBackoffLimit(1)
			.build();
	}

	private PodTemplateSpec getPodTemplateSpec() {
		return new PodTemplateSpecBuilder()
			.withNewMetadata()
			.withName(workload + "-commit")
			.withNamespace("astrago")
			.endMetadata()
			.withNewSpec()
			.withNodeName(nodeName)
			.withContainers(getContainer())
			.withVolumes(getVolume())
			.withRestartPolicy("Never")
			.endSpec()
			.build();
	}

	private Container getContainer() {
		return new ContainerBuilder()
			.withName(workload + "-commit")
			.withImage("xiilab/nerdctl:astrago")
			.withCommand("/bin/sh", "-c")
			.addToArgs(getJobScript())
			.addAllToEnv(getEnvVars())
			.withNewSecurityContext()
			.withRunAsUser(0L)
			.withPrivileged(true)
			.endSecurityContext()
			.addNewVolumeMount()
			.withName("containerd-socket")
			.withMountPath("/var/run/containerd")
			.endVolumeMount()
			.build();
	}

	private List<EnvVar> getEnvVars() {
		EnvVar namespaceENV = new EnvVar("NAMESPACE", workspace, null);
		EnvVar workloadENV = new EnvVar("WORKLOAD_NAME", workload, null);
		EnvVar imageENV = new EnvVar("IMAGE_NAME", username + "/" + imageName + ":" + imageTag, null);
		EnvVar urlENV = new EnvVar("HARBOR_URL", "10.61.3.161:30002", null);
		EnvVar passENV = new EnvVar("HARBOR_PASSWORD", "Harbor12345", null);
		return List.of(namespaceENV, workloadENV, imageENV, urlENV, passENV);
	}

	private Volume getVolume() {
		return new VolumeBuilder()
			.withName("containerd-socket")
			.withNewHostPath("/var/run/containerd", "Directory")
			.build();
	}

	private String getJobScript() {
		return """
			ACTUAL_CONTAINER_NAME=$(nerdctl --namespace k8s.io ps --format "{{.ID}} {{.Names}} {{.Image}}" | grep -v 'registry.k8s.io/pause' | grep "${NAMESPACE}/${WORKLOAD_NAME}" | awk '{print $1}')
			if [ -z "$ACTUAL_CONTAINER_NAME" ]; then
			    echo "실제 애플리케이션 컨테이너를 찾을 수 없습니다. 종료합니다."
			    exit 1
			else
			    echo "실제 애플리케이션 컨테이너 $ACTUAL_CONTAINER_NAME 를 찾았습니다. Commit 및 Push 진행 중..."
			fi
			if ! echo $HARBOR_PASSWORD | nerdctl login -u admin --password-stdin --insecure-registry http://${HARBOR_URL}; then
			    echo "Harbor에 로그인 할 수 없습니다. 로그인 정보 및 Harbor 상태를 확인해주세요."
			    exit 1
			fi
			if ! nerdctl --namespace k8s.io commit $ACTUAL_CONTAINER_NAME ${HARBOR_URL}/${IMAGE_NAME}; then
			    echo "이미지를 커밋하는 중 오류가 발생했습니다. 컨테이너 이름과 이미지 이름을 확인하세요."
			    exit 1
			fi
			if ! nerdctl --namespace k8s.io push --insecure-registry ${HARBOR_URL}/${IMAGE_NAME}; then
			    echo "이미지를 Harbor에 푸시하는 중 오류가 발생했습니다. 네트워크 연결이나 이미지 이름을 확인하세요."
			    exit 1
			fi
			echo "이미지를 Harbor에 성공적으로 푸시했습니다."
			""";
	}
}
