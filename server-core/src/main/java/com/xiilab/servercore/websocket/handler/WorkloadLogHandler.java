package com.xiilab.servercore.websocket.handler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;

import io.fabric8.kubernetes.client.dsl.LogWatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class WorkloadLogHandler extends TextWebSocketHandler {
	private ScheduledExecutorService executorService;
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
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
		String[] splitMessage = message.getPayload().toString().split(",");
		if (splitMessage.length != 2) {
			log.error("Websocket Message: {}", message);
			throw new RuntimeException("잘못된 메시지가 전달되었습니다.");
		}

		String workspaceId = splitMessage[0];
		String workloadId = splitMessage[1];

		executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(() -> {
			try (LogWatch logWatch = workloadModuleFacadeService.watchLogByWorkload(workspaceId, workloadId);
				 InputStream output = logWatch.getOutput();
				 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(output))) {
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					if (session.isOpen()) {
						workloadLogWebSocketSessionMap.get(session.getId()).sendMessage(new TextMessage(line));
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		try (session) {
			workloadLogWebSocketSessionMap.remove(session.getId());
		} finally {
			stopSendingMessages();
		}
	}

	private void stopSendingMessages() {
		executorService.shutdown();
	}
}
