# 문서개요

해당 문서는 Webterminal, Log 등 웹소켓을 사용하는 API에 대한 명세를 위함

# WebTerminal

## URL

/ws/workload/terminal

## 사용방법

json으로 다음과 같은 객체를 담아 요청

**1. terminal 접속 정보 전송**

```json
{
  "messageType": "TERMINAL_HOST",
  "workspace": "ws-sdfsdfsdfsdfsdf",
  "workload": "wl-sdfsdfsdfsdfsdf",
  "workloadType": "batch"
}
```

2. terminal init

```json
{
  "messageType": "TERMINAL_INIT"
}
```

3. terminal에 command 전송(keydownEvent 발생시 마다 호출)

```json
{
  "messageType": "TERMINAL_COMMAND",
  "command": "l"
}
```

**&번외& Terminal Resize**

```json
{
  "messageType": "TERMINAL_RESIZE",
  "column" : 10,
  "rows" : 10,
  "width" : 10,
  "height" : 10
}
```

# Log

## URL

/ws/workload/log

## 사용방법
접속정보를 서버로 전달
```json
{
    "workloadType": "INTERACTIVE",
    "workspaceName": "ws-32bc5e61-8c81-416c-9c3a-72ad34f45701",
    "workloadName" : "wl-7ef74e09-1e7b-41bb-a2f9-a45d1f21ab7d"
}
```

