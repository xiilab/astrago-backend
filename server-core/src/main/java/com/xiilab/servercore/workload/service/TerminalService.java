package com.xiilab.servercore.workload.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulek8s.workload.enums.WorkloadType;
import com.xiilab.modulek8s.workload.service.WorkloadModuleService;
import com.xiilab.servercore.common.utils.ThreadHelper;
import com.xiilab.servercore.workload.dto.TerminalMessage;

import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class TerminalService {
	private final WorkloadModuleService k8sService;
	private Boolean isReady;

	/**
	 * 웹 터미널 열
	 */
	private Integer columns = 20;

	/**
	 * 웹 터미널 행
	 */
	private Integer rows = 10;

	/**
	 * 웹 터미널 키 입력 Reader
	 */
	private BufferedWriter outputWriter;

	/**
	 * 웹 터미널 소켓 세션
	 */
	private WebSocketSession webSocketSession;

	private TerminalMessage messageInfo;

	private LinkedBlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();

	// 키보드 입력 겍체
	private OutputStream keyBoardOutputStream;

	// 터미널 출력 겍체
	private InputStream terminalInputStream;

	// 터미널 에러 출력 겍체
	private InputStream terminalErrorInputStream;

	private BufferedReader out;
	private BufferedReader error;

	// 쿠버네티스 명령어 겍체
	private ExecWatch watch;

	/**
	 * 웹 소켓 생성시 ssh 세션 연결및 쓰레드에 적재
	 */
	public void onTerminalInit() {
		ThreadHelper.start(() -> {
			try {
				initializeProcess();
				this.isReady = true;
			} catch (Exception e1) {
				log.error("onTerminalInit KubernetesClientException - {}", e1);
				// Optional<Throwable> rootCause =
				Stream.iterate(e1, Throwable::getCause)
					.filter(element -> element.getCause() == null)
					.findFirst()
					.ifPresent(cause -> {
						try {
							sendAlert(cause.getMessage());
						} catch (IOException e) {
							log.error("onTerminalInit Exception - {}", e);
							throw new RuntimeException(e);
						}
					});
			}
		});

	}

	/**
	 * ssh 세션 생성
	 */
	private void initializeProcess() throws KubernetesClientException {
		WorkloadType workloadType = messageInfo.getWorkloadType();
		String workloadName = messageInfo.getWorkload();
		String workspaceName = messageInfo.getWorkspace();
		this.watch = k8sService.connectWorkloadTerminal(workloadName, workspaceName, workloadType).usingListener(
			(code, reason) -> {
				log.info("close terminal : {}", code);
				try {
					disConnect();
				} catch (IOException | InterruptedException e) {
					log.error(e.getMessage());
					Thread.currentThread().interrupt();
				}
			}
		).exec("/bin/bash");

		// 키보드 입력을 받아옴
		keyBoardOutputStream = this.watch.getInput();
		// 터미널 출력 부분을 받아옴
		terminalInputStream = this.watch.getOutput();
		// 터미널 에러 출력 부분을 받아옴
		terminalErrorInputStream = this.watch.getError();

		// 웹 소켓에서 키보드 입력 수신시 아웃풋스트림 에 스트링값을 삽입하도록 버퍼 생성
		this.outputWriter = new BufferedWriter(new OutputStreamWriter(keyBoardOutputStream));

		error = new BufferedReader(new InputStreamReader(terminalErrorInputStream));
		out = new BufferedReader(new InputStreamReader(terminalInputStream));

		try {
			String message = new ObjectMapper().writeValueAsString(Map.of("type", "TERMINAL_INIT"));
			webSocketSession.sendMessage(new TextMessage(message));
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		// 클라이언트에서 수신받은 인풋스트림을 별도의 쓰레드로 받아와서 문자열로 치한후 웹소켓으로 클라이언트에게 전송
		// 몬가가 몬가 모르겠지만 됨
		ThreadHelper.start(() -> {
			Thread.currentThread().setName("TerminalError");
			printReader(error);
		});
		ThreadHelper.start(() -> {
			Thread.currentThread().setName("TerminalOutput");
			printReader(out);
		});
	}

	/**
	 * 버퍼 에서 받은 스트림을 문자열로 치환
	 *
	 * @param bufferedReader
	 */
	private void printReader(BufferedReader bufferedReader) {
		try {
			int nRead;
			char[] data = new char[10 * 1024];
			if (bufferedReader != null) {
				while ((nRead = bufferedReader.read(data, 0, data.length)) != -1) {
					StringBuilder builder = new StringBuilder(nRead);
					builder.append(data, 0, nRead);
					print(builder.toString());
				}
			}
		} catch (Exception e) {
			log.error("printReader ERROR - {} ", e);
		} finally {
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e) {
				throw new RuntimeException("web terminal close error");
			}
		}
	}

	/**
	 * 클라이언트에게 문자 전송
	 *
	 * @param text
	 * @throws IOException
	 */
	private void print(String text) throws IOException {
		Map<String, String> map = new HashMap<>();
		map.put("type", "TERMINAL_PRINT");
		map.put("text", text);
		String message = new ObjectMapper().writeValueAsString(map);
		webSocketSession.sendMessage(new TextMessage(message));
	}

	/**
	 * 클라이언트에게 로그 메세지 전송
	 *
	 * @param text
	 * @throws IOException
	 */
	public void sendLogMessage(String text) throws IOException {
		Map<String, String> map = new HashMap<>();
		map.put("type", "TERMINAL_LOG");
		map.put("text", text);
		String message = new ObjectMapper().writeValueAsString(map);
		webSocketSession.sendMessage(new TextMessage(message));
	}

	/**
	 * 클라이언트에게 경고문 전송
	 *
	 * @param text 메세지
	 */
	public void sendAlert(String text) throws IOException {
		Map<String, String> map = new HashMap<>();
		map.put("type", "TERMINAL_ALERT");
		map.put("text", text);
		String message = new ObjectMapper().writeValueAsString(map);
		webSocketSession.sendMessage(new TextMessage(message));
	}

	/**
	 * 클라이언트에서 받은 문자열을 스트림에 적재
	 *
	 * @param command
	 */
	public void onCommand(String command) throws InterruptedException {
		if (Objects.isNull(command)) {
			return;
		}
		commandQueue.put(command);
		ThreadHelper.start(() -> {
			try {
				outputWriter.write(Objects.requireNonNull(commandQueue.poll()));
				outputWriter.flush();
			} catch (NullPointerException | IOException e) {
				log.error("onCommand ERROR - {} ", e);
			}
		});
	}

	/**
	 * 직접 xterm 리사이즈
	 *
	 * @param columns
	 * @param rows
	 * @throws InterruptedException
	 */
	protected void terminalResize(Integer columns, Integer rows) throws InterruptedException {
		//        LinkedBlockingQueue<String> commandList = new LinkedBlockingQueue<>();
		//        commandList.put(String.format("export COLUMNS=%s\r", columns)); // 환경변수 COLUMNS 입력
		//        commandList.put(String.format("export LINES=%s\r", rows)); // 환경변수 LINES 입력

		this.watch.resize(columns, rows);

		//        ThreadHelper.start(() -> {
		//            try {
		//                outputWriter.write(Objects.requireNonNull(commandList.poll()));
		//                outputWriter.flush();
		//            } catch (IOException e) {
		//                log.error("terminalResize ERROR - {} ", e);
		//            }
		//        });
	}

	/**
	 * 터미널 사이즈 조정
	 *
	 * @param columns
	 * @param rows
	 */
	public void onTerminalResize(String columns, String rows) throws InterruptedException {
		// width , height 계산은 프론트엔드에서
		if (Objects.nonNull(columns) && Objects.nonNull(rows)) {
			this.columns = Integer.valueOf(columns);
			this.rows = Integer.valueOf(rows);

			if (Objects.nonNull(this.watch)) {
				terminalResize(this.columns, this.rows);
			}
		}
	}

	/**
	 * 쉘 종료
	 */
	public void exitShell() throws InterruptedException {
		LinkedBlockingQueue<String> commandList = new LinkedBlockingQueue<>();
		commandList.put(String.format("exit\n\r", columns));
		ThreadHelper.start(() -> {
			try {
				outputWriter.write(Objects.requireNonNull(commandList.poll()));
				outputWriter.flush();
			} catch (IOException e) {
				log.error("terminalResize ERROR - {} ", e);
			}
		});
	}

	/**
	 * 파드 통신 종료
	 */
	public void disConnect() throws IOException, InterruptedException {
		this.isReady = false;
		if (Objects.nonNull(this.watch)) {
			this.watch.close();
		}
		if (Objects.nonNull(out)) {
			out.close();
		}
		if (Objects.nonNull(error)) {
			error.close();
		}
		if (Objects.nonNull(keyBoardOutputStream)) {
			keyBoardOutputStream.close();
		}
		if (Objects.nonNull(terminalInputStream)) {
			terminalInputStream.close();
		}
		if (Objects.nonNull(terminalErrorInputStream)) {
			terminalErrorInputStream.close();
		}
		webSocketSession.close();
	}
}
