package com.xiilab.servercore.workload.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;

import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.errorcode.WorkloadErrorCode;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.servercore.workload.dto.request.WorkloadLogMessage;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class WorkloadLogHandler extends TextWebSocketHandler {
	private final WorkloadModuleFacadeService workloadModuleFacadeService;
	// 특정 세션만 저장
	private final Map<String, WebSocketSession> workloadLogWebSocketSessionMap;

	// 소켓 연결됐을 때
	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		log.info("Socket 연결! sessionID: {}", session.getId());
		workloadLogWebSocketSessionMap.put(session.getId(), session);
	}

	// 클라이언트로부터 넘어온 메시지 처리
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws JsonProcessingException {
		WorkloadLogMessage logReqMessage = getMessageMap(message.getPayload().toString());
		String podName = getPodNameByWorkloadType(logReqMessage.getWorkspaceName(), logReqMessage.getWorkloadName(), logReqMessage.getWorkloadType())
			.map(pod -> pod.getMetadata().getName())
			.orElseThrow(() -> new K8sException(WorkloadErrorCode.NOT_FOUND_WORKLOAD_POD));
		if (!StringUtils.hasText(podName)) {
			throw new K8sException(WorkloadErrorCode.WORKLOAD_MESSAGE_ERROR);
		}

		sendLogMessage(session, logReqMessage.getWorkspaceName(), podName);
	}

	private void sendLogMessage(WebSocketSession session, String workspaceName, String podName) {
		try (
			LogWatch logWatch = workloadModuleFacadeService.watchLogByWorkload(workspaceName, podName);
			InputStream output = logWatch.getOutput();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(output))) {
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (session.isOpen()) {
					workloadLogWebSocketSessionMap.get(session.getId()).sendMessage(new TextMessage(line));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}

	private Optional<Pod> getPodNameByWorkloadType(String workspaceName, String workloadName,
		WorkloadType workloadType) {
		try {
			return Optional.of(workloadModuleFacadeService.getJobPod(workspaceName, workloadName, workloadType));
		} catch (RuntimeException e) {
			throw e;
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		try (session) {
			workloadLogWebSocketSessionMap.remove(session.getId());
		}
		// finally {
		// 	stopSendingMessages();
		// }
	}
	/**
	 * JSON Text 을 Class 으로 파싱
	 *
	 * @param message
	 * @return
	 */
	private WorkloadLogMessage getMessageMap(String message) throws JsonProcessingException {
		try {
			return new ObjectMapper().readValue(message, WorkloadLogMessage.class);
		} catch (JsonProcessingException e) {
			log.error("{}", e);
			throw e;
		}
	}
}
