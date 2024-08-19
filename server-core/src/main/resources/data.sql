INSERT INTO TB_WORKSPACE_SETTING (id, cpu, gpu, mem)
values (1, 4, 8, 16)
ON DUPLICATE KEY UPDATE id = id;

-- MIN 쿠다 버전 초기값
INSERT INTO TB_MIN_CUDA_VERSION (MIN_CUDA_VERSION_ID, CUDA_VERSION, MAX_VERSION, MIN_VERSION, MOD_DATE, REG_DATE)
VALUES (1, '1.0', '1.1', '1.0', null, now()),
       (2, '1.1', '1.1', '1.0', null, now()),
       (3, '2.0', '1.1', '1.0', null, now()),
       (4, '2.1', '1.3', '1.0', null, now()),
       (5, '3.0', '2.0', '1.0', null, now()),
       (6, '4.0', '2.1', '1.0', null, now()),
       (7, '5.0', '3.5', '1.0', null, now()),
       (8, '6.0', '3.5', '1.0', null, now()),
       (9, '6.5', '5.9', '1.1', null, now()),
       (10, '7.0', '5.9', '2.0', null, now()),
       (11, '8.0', '6.9', '2.0', null, now()),
       (12, '9.0', '7.2', '3.0', null, now()),
       (13, '10.0', '7.5', '3.0', null, now()),
       (14, '11.0', '8.0', '3.5', null, now()),
       (15, '11.1', '8.6', '3.5', null, now()),
       (16, '11.5', '8.7', '3.5', null, now()),
       (17, '11.8', '9.0', '3.5', null, now()),
       (18, '12.0', '9.0', '5.0', null, now())
ON DUPLICATE KEY UPDATE MIN_CUDA_VERSION_ID = MIN_CUDA_VERSION_ID;

-- MAX 쿠다 버전 초기값
INSERT INTO TB_MAX_CUDA_VERSION (MAX_CUDA_VERSION_ID, CUDA_VERSION, MAJOR_VERSION, MINOR_VERSION, MOD_DATE, REG_DATE,
                                 REV)
VALUES (1, '12.3', '545.0', '23.0', null, now(), '8.0'),
       (2, '12.3', '545.0', '23.0', null, now(), '6.0'),
       (3, '12.2', '535.0', '104.0', null, now(), '5.0'),
       (4, '12.2', '535.0', '86.0', null, now(), '9.0'),
       (5, '12.2', '535.0', '54.0', null, now(), '3.0'),
       (6, '12.1', '530.0', '30.0', null, now(), '2.0'),
       (7, '12.0', '525.0', '85.0', null, now(), '12.0'),
       (8, '12.0', '525.0', '60.0', null, now(), '13.0'),
       (9, '11.8', '520.0', '61.0', null, now(), '5.0'),
       (10, '11.7', '515.0', '48.0', null, now(), '7.0'),
       (11, '11.7', '515.0', '43.0', null, now(), '4.0'),
       (12, '11.6', '510.0', '47.0', null, now(), '3.0'),
       (13, '11.6', '510.0', '39.0', null, now(), '1.0'),
       (14, '11.5', '495.0', '29.0', null, now(), '5.0'),
       (15, '11.6', '495.0', '29.0', null, now(), '5.0'),
       (16, '11.7', '495.0', '29.0', null, now(), '5.0'),
       (17, '11.4', '470.0', '82.0', null, now(), '1.0'),
       (18, '11.4', '470.0', '57.0', null, now(), '2.0'),
       (19, '11.4.0', '470.0', '42.0', null, now(), '1.0'),
       (20, '11.3.1', '465.0', '19.0', null, now(), '1.0'),
       (21, '11.3.0', '465.0', '19.0', null, now(), '1.0'),
       (22, '11.2.2', '460.0', '32.0', null, now(), '3.0'),
       (23, '11.2.1', '460.0', '32.0', null, now(), '3.0'),
       (24, '11.2.0', '460.0', '27.0', null, now(), '3.0'),
       (25, '11.1.1', '455.0', '32.0', null, now(), '0.0'),
       (26, '11.1', '455.0', '23.0', null, now(), '0.0'),
       (27, '11.0.3', '450.0', '51.0', null, now(), '6.0'),
       (28, '11.0.2', '450.0', '51.0', null, now(), '5.0'),
       (29, '11.0', '450.0', '36.0', null, now(), '6.0'),
       (30, '10.2.89', '440.0', '33.0', null, now(), '0.0'),
       (31, '10.1', '418.0', '39.0', null, now(), '0.0'),
       (32, '10.0.130', '410.0', '48.0', null, now(), '0.0'),
       (33, '9.2', '396.0', '37.0', null, now(), '0.0'),
       (34, '9.2', '396.0', '26.0', null, now(), '0.0'),
       (35, '9.1', '390.0', '46.0', null, now(), '0.0'),
       (36, '9.0', '384.0', '81.0', null, now(), '0.0'),
       (37, '8.0', '375.0', '26.0', null, now(), '0.0'),
       (38, '8.0', '367.0', '48.0', null, now(), '0.0'),
       (39, '7.5', '352.0', '31.0', null, now(), '0.0'),
       (40, '7.0', '346.0', '46.0', null, now(), '0.0')
ON DUPLICATE KEY UPDATE MAX_CUDA_VERSION_ID = MAX_CUDA_VERSION_ID;

-- 프레임워크 버전
INSERT INTO TB_FRAMEWORK_VERSION (FRAMEWORK_VERSION_ID, CUDA_VERSION, FRAMEWORK_TYPE, FRAMEWORK_VERSION, MOD_DATE,
                                  REG_DATE)
VALUES (1, '11.8', 'PYTORCH', '2.2.1', null, now()),
       (2, '11.8', 'PYTORCH', '2.2.0', null, now()),
       (3, '11.8', 'PYTORCH', '2.1.2', null, now()),
       (4, '11.8', 'PYTORCH', '2.1.1', null, now()),
       (5, '11.8', 'PYTORCH', '2.1.0', null, now()),
       (6, '11.7', 'PYTORCH', '2.0.1', null, now()),
       (7, '11.7', 'PYTORCH', '2.0.0', null, now()),
       (8, '11.6', 'PYTORCH', '1.13.1', null, now()),
       (9, '11.6', 'PYTORCH', '1.13.0', null, now()),
       (10, '10.2', 'PYTORCH', '1.12.1', null, now()),
       (11, '10.2', 'PYTORCH', '1.12.0', null, now()),
       (12, '10.2', 'PYTORCH', '1.11.0', null, now()),
       (13, '10.2', 'PYTORCH', '1.10.1', null, now()),
       (14, '10.2', 'PYTORCH', '1.10.0', null, now()),
       (15, '10.2', 'PYTORCH', '1.9.1', null, now()),
       (16, '10.2', 'PYTORCH', '1.9.0', null, now()),
       (17, '10.2', 'PYTORCH', '1.8.1', null, now()),
       (18, '10.2', 'PYTORCH', '1.8.0', null, now()),
       (19, '9.2', 'PYTORCH', '1.7.1', null, now()),
       (20, '9.2', 'PYTORCH', '1.7.0', null, now()),
       (21, '9.2', 'PYTORCH', '1.6.0', null, now()),
       (22, '9.2', 'PYTORCH', '1.5.1', null, now()),
       (23, '9.2', 'PYTORCH', '1.5.0', null, now()),
       (24, '9.2', 'PYTORCH', '1.4.0', null, now()),
       (25, '9.2', 'PYTORCH', '1.2.0', null, now()),
       (26, '9', 'PYTORCH', '1.1.0', null, now()),
       (27, '9', 'PYTORCH', '1.0.1', null, now()),
       (28, '8', 'PYTORCH', '1.0.0', null, now())
ON DUPLICATE KEY UPDATE FRAMEWORK_VERSION_ID = FRAMEWORK_VERSION_ID;

-- 호환 가능한 프레임워크 버전 초기값
INSERT INTO TB_COMPATIBLE_FRAMEWORK_VERSION (COMPATIBLE_FRAMEWORK_VERSION_ID, MOD_DATE, REG_DATE, FRAMEWORK_VERSION_ID)
VALUES (43155, null, now(), 1),
       (43151, null, now(), 2),
       (43152, null, now(), 3),
       (43153, null, now(), 4),
       (43154, null, now(), 5)
ON DUPLICATE KEY UPDATE COMPATIBLE_FRAMEWORK_VERSION_ID = COMPATIBLE_FRAMEWORK_VERSION_ID;

-- 알림 초기값
INSERT INTO TB_ALERT (ALERT_ID, ALERT_NAME, ALERT_TYPE, ALERT_ROLE, ALERT_EVENT_TYPE)
VALUES (1, '유저 회원가입 알림', 'USER', 'ADMIN', 'USER'),
       (2, '유저 업데이트 알림', 'USER', 'USER', 'USER'),
       (3, '워크스페이스 생성 알림', 'WORKSPACE', 'ADMIN', 'NOTIFICATION'),
       (4, '워크스페이스 생성 알림', 'WORKSPACE', 'OWNER', 'NOTIFICATION'),
       (5, '워크스페이스 리소스 초과 알림', 'WORKSPACE', 'ADMIN', 'NOTIFICATION'),
       (6, '워크스페이스 리소스 요청 알림', 'WORKSPACE', 'ADMIN', 'NOTIFICATION'),
       (7, '워크스페이스 리소스 요청 알림', 'WORKSPACE', 'OWNER', 'NOTIFICATION'),
       (8, '워크스페이스 리소스 요청 결과 알림', 'WORKSPACE', 'OWNER', 'NOTIFICATION'),
       (9, '워크스페이스 삭제 알림', 'WORKSPACE', 'USER', 'DELETE'),
       (10, '워크스페이스 회원 변경 알림', 'WORKSPACE', 'OWNER', 'NOTIFICATION'),
       (11, '워크로드 시작 알림', 'WORKLOAD', 'USER', 'NOTIFICATION'),
       (12, '워크로드 종료 알림', 'WORKLOAD', 'USER', 'NOTIFICATION'),
       (13, '워크로드 에러 알림', 'WORKLOAD', 'USER', 'ERROR'),
       (14, '워크로드 삭제 알림', 'WORKLOAD', 'USER', 'DELETE'),
       (15, '라이센스 만료 경고 알림', 'LICENSE', 'ADMIN', 'NOTIFICATION'),
       (16, '노드 장애 알림', 'NODE', 'ADMIN', 'ERROR'),
       (17, 'MIG 적용 알림', 'NODE', 'ADMIN', 'NOTIFICATION'),
       (18, 'MIG 장애 알림', 'NODE', 'ADMIN', 'ERROR')
ON DUPLICATE KEY UPDATE ALERT_ID = ALERT_ID;

-- 이미지 초기값
insert into TB_IMAGE (MULTI_NODE, IMAGE_ID, MOD_DATE, REG_DATE, IMAGE_TYPE, IMAGE_NAME, REG_USER_ID, REG_USER_NAME,
                      REG_USER_REAL_NAME, DELETE_YN, REPOSITORY_AUTH_TYPE, WORKLOAD_TYPE)
values (false, 1, null, '2024-07-08 08:49:58.000000', 'HUB', 'xiilab/astrago:hub-yolov8-1.1', 'SYSTEM', 'SYSTEM',
        'SYSTEM', 'N', 'PUBLIC', 'BATCH'),
       (false, 2, null, '2024-07-08 08:49:58.000000', 'HUB', 'xiilab/astrago:hub-yolov8-1.1', 'SYSTEM', 'SYSTEM',
        'SYSTEM', 'N', 'PUBLIC', 'BATCH'),
       (false, 3, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:pytorch-23.07-cuda12.1', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 4, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:pytorch-22.12-cuda11.8', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 5, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:pytorch-22.08-cuda11.7', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 6, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:pytorch-22.04-cuda11.6', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 7, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:pytorch-21.06-cuda11.3',
        'b7b2a599-9ca0-4bed-b2bd-a015a568bd6d', 'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 8, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:pytorch-20.12-cuda11.1',
        'b7b2a599-9ca0-4bed-b2bd-a015a568bd6d', 'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 9, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:pytorch-23.07-cuda12.1', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 10, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:pytorch-22.12-cuda11.8', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 11, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:pytorch-22.08-cuda11.7', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 12, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:pytorch-22.04-cuda11.6', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 13, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:pytorch-21.06-cuda11.3',
        'b7b2a599-9ca0-4bed-b2bd-a015a568bd6d', 'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 14, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:pytorch-20.12-cuda11.1',
        'b7b2a599-9ca0-4bed-b2bd-a015a568bd6d', 'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 15, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:tensorflow-23.07-cuda12.1',
        'b7b2a599-9ca0-4bed-b2bd-a015a568bd6d', 'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 16, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:tensorflow-22.12-cuda11.8', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 17, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:tensorflow-22.08-cuda11.7', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 18, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:tensorflow-22.04-cuda11.6', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 19, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:tensorflow-21.06-cuda11.3', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 20, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:tensorflow-20.12-cuda11.1',
        'b7b2a599-9ca0-4bed-b2bd-a015a568bd6d', 'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 21, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:tensorflow-23.07-cuda12.1',
        'b7b2a599-9ca0-4bed-b2bd-a015a568bd6d', 'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 22, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:tensorflow-22.12-cuda11.8', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 23, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:tensorflow-22.08-cuda11.7', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 24, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:tensorflow-22.04-cuda11.6', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 25, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:tensorflow-21.06-cuda11.3', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 26, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:tensorflow-20.12-cuda11.1',
        'b7b2a599-9ca0-4bed-b2bd-a015a568bd6d', 'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'INTERACTIVE'),
       (false, 27, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:pytorch-23.07-cuda12.1', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'BATCH'),
       (false, 28, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:pytorch-22.12-cuda11.8', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'BATCH'),
       (false, 29, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:pytorch-22.08-cuda11.7', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'BATCH'),
       (false, 30, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:pytorch-22.04-cuda11.6', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'BATCH'),
       (false, 31, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:pytorch-21.06-cuda11.3', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'BATCH'),
       (false, 32, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:pytorch-20.12-cuda11.1', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'BATCH'),
       (false, 33, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:tensorflow-23.07-cuda12.1', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'BATCH'),
       (false, 34, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:tensorflow-22.12-cuda11.8', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'BATCH'),
       (false, 35, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:tensorflow-22.08-cuda11.7', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'BATCH'),
       (false, 36, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:tensorflow-22.04-cuda11.6', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'BATCH'),
       (false, 37, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:tensorflow-21.06-cuda11.3', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'BATCH'),
       (false, 38, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/astrago:tensorflow-20.12-cuda11.1', 'SYSTEM',
        'SYSTEM', 'SYSTEM', 'N', 'PUBLIC', 'BATCH'),
       (true, 39, null, '2024-07-08 08:49:58.000000', 'BUILT', 'xiilab/horovod', 'SYSTEM', 'SYSTEM', 'SYSTEM', 'N',
        'PUBLIC', 'DISTRIBUTED'),
       (true, 40, null, '2024-07-08 08:49:58.000000', 'BUILT', 'nvcr.io/nvidia/tritonserver:23.02-py3', 'SYSTEM', 'SYSTEM', 'SYSTEM', 'N',
        'PUBLIC', 'DEPLOY')
ON DUPLICATE KEY UPDATE IMAGE_ID = IMAGE_ID;

-- 허브 이미지 초기값
INSERT INTO TB_HUB_IMAGE (IMAGE_ID)
VALUES (1),
       (2)
ON DUPLICATE KEY UPDATE IMAGE_ID = IMAGE_ID;

-- 빌트인 이미지 초기값
insert into TB_BUILT_IN_IMAGE (IMAGE_ID, COMMAND, CUDA_VERSION, DESCRIPTION, FRAMEWORK_VERSION, PORTS, TITLE, FRAMEWORK_TYPE)
values  (3, 'code-server --cert=false --auth=none --bind-addr=0.0.0.0:8080', '12.1', 'NVIDIA의 nvcr.io/nvidia/pytorch:23.07-py 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 12.1을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '2.1.0', '{"vscode": 8080}', 'vs-code:xiilab/astrago:pytorch-23.07-cuda12.1', 'VSCODE'),
        (4, 'code-server --cert=false --auth=none --bind-addr=0.0.0.0:8080', '11.8', 'NVIDIA의 nvcr.io/nvidia/pytorch:22.12-py 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.8을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '1.14.0', '{"vscode": 8080}','vs-code:xiilab/astrago:pytorch-22.12-cuda11.8', 'VSCODE'),
        (5, 'code-server --cert=false --auth=none --bind-addr=0.0.0.0:8080', '11.7', 'NVIDIA의 nvcr.io/nvidia/pytorch:22.08-py 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.7을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '1.13.0', '{"vscode": 8080}', 'vs-code:xiilab/astrago:pytorch-22.08-cuda11.7', 'VSCODE'),
        (6, 'code-server --cert=false --auth=none --bind-addr=0.0.0.0:8080', '11.6', 'NVIDIA의 nvcr.io/nvidia/pytorch:22.04-py 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.6을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '1.12.0', '{"vscode": 8080}', 'vs-code:xiilab/astrago:pytorch-22.04-cuda11.6', 'VSCODE'),
        (7, 'code-server --cert=false --auth=none --bind-addr=0.0.0.0:8080', '11.3', 'NVIDIA의 nvcr.io/nvidia/pytorch:21.06-py 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.3을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '1.9.0', '{"vscode": 8080}',  'vs-code:xiilab/astrago:pytorch-21.06-cuda11.3', 'VSCODE'),
        (8, 'code-server --cert=false --auth=none --bind-addr=0.0.0.0:8080', '11.1', 'NVIDIA의 nvcr.io/nvidia/pytorch:20.12-py 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.1을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '1.8.0', '{"vscode": 8080}',  'vs-code:xiilab/astrago:pytorch-20.12-cuda11.1', 'VSCODE'),
        (9, 'jupyter lab --allow-root --ip=* --port=8888 --NotebookApp.token='''' --NotebookApp.allow_origin=''*'' --notebook-dir=/', '12.1', 'NVIDIA의 nvcr.io/nvidia/pytorch:23.07-py 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 12.1을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '2.1.0', '{"jupyter": 8888}',  'jupyter-lab:xiilab/astrago:pytorch-23.07-cuda12.1', 'JUPYTER'),
        (10, 'jupyter lab --allow-root --ip=* --port=8888 --NotebookApp.token='''' --NotebookApp.allow_origin=''*'' --notebook-dir=/', '11.8', 'NVIDIA의 nvcr.io/nvidia/pytorch:22.12-py 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.8을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '1.14.0', '{"jupyter": 8888}',  'jupyter-lab:xiilab/astrago:pytorch-22.12-cuda11.8', 'JUPYTER'),
        (11, 'jupyter lab --allow-root --ip=* --port=8888 --NotebookApp.token='''' --NotebookApp.allow_origin=''*'' --notebook-dir=/', '11.7', 'NVIDIA의 nvcr.io/nvidia/pytorch:22.08-py 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.7을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '1.13.0', '{"jupyter": 8888}',  'jupyter-lab:xiilab/astrago:pytorch-22.08-cuda11.7', 'JUPYTER'),
        (12, 'jupyter lab --allow-root --ip=* --port=8888 --NotebookApp.token='''' --NotebookApp.allow_origin=''*'' --notebook-dir=/', '11.6', 'NVIDIA의 nvcr.io/nvidia/pytorch:22.04-py 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.6을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '1.12.0', '{"jupyter": 8888}',  'jupyter-lab:xiilab/astrago:pytorch-22.04-cuda11.6', 'JUPYTER'),
        (13, 'jupyter lab --allow-root --ip=* --port=8888 --NotebookApp.token='''' --NotebookApp.allow_origin=''*'' --notebook-dir=/', '11.3', 'NVIDIA의 nvcr.io/nvidia/pytorch:21.06-py 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.3을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '1.9.0', '{"jupyter": 8888}',  'jupyter-lab:xiilab/astrago:pytorch-21.06-cuda11.3', 'JUPYTER'),
        (14, 'jupyter lab --allow-root --ip=* --port=8888 --NotebookApp.token='''' --NotebookApp.allow_origin=''*'' --notebook-dir=/', '11.1', 'NVIDIA의 nvcr.io/nvidia/pytorch:20.12-py 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.1을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '1.8.0', '{"jupyter": 8888}',  'jupyter-lab:xiilab/astrago:pytorch-20.12-cuda11.1', 'JUPYTER'),
        (15, 'code-server --cert=false --auth=none --bind-addr=0.0.0.0:8080', '12.1', 'NVIDIA의 nvcr.io/nvidia/tensorflow:23.07-tf2-py3 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 12.1을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '2.12.0', '{"vscode": 8080}',  'vs-code:xiilab/astrago:pytorch-23.07-cuda12.1', 'VSCODE'),
        (16, 'code-server --cert=false --auth=none --bind-addr=0.0.0.0:8080', '11.8', 'NVIDIA의 nvcr.io/nvidia/tensorflow:22.12-tf2-py3 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.8을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '2.10.1', '{"vscode": 8080}',  'vs-code:xiilab/astrago:tensorflow-22.12-cuda11.8', 'VSCODE'),
        (17, 'code-server --cert=false --auth=none --bind-addr=0.0.0.0:8080', '11.7', 'NVIDIA의 nvcr.io/nvidia/tensorflow:22.08-tf2-py3 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.7을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '2.9.1', '{"vscode": 8080}',  'vs-code:xiilab/astrago:tensorflow-22.08-cuda11.7', 'VSCODE'),
        (18, 'code-server --cert=false --auth=none --bind-addr=0.0.0.0:8080', '11.6', 'NVIDIA의 nvcr.io/nvidia/tensorflow:22.04-tf2-py3 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.6을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '2.8.0', '{"vscode": 8080}',  'vs-code:xiilab/astrago:tensorflow-22.04-cuda11.6', 'VSCODE'),
        (19, 'code-server --cert=false --auth=none --bind-addr=0.0.0.0:8080', '11.3', 'NVIDIA의 nvcr.io/nvidia/tensorflow:21.06-tf2-py3 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.3을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '2.5.0', '{"vscode": 8080}',  'vs-code:xiilab/astrago:tensorflow-21.06-cuda11.3', 'VSCODE'),
        (20, 'code-server --cert=false --auth=none --bind-addr=0.0.0.0:8080', '11.1', 'NVIDIA의 nvcr.io/nvidia/tensorflow:20.12-tf2-py3 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.1을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '2.3.1', '{"vscode": 8080}',  'vs-code:xiilab/astrago:tensorflow-21.06-cuda11.3', 'VSCODE'),
        (21, 'jupyter lab --allow-root --ip=* --port=8888 --NotebookApp.token='''' --NotebookApp.allow_origin=''*'' --notebook-dir=/', '12.1', 'NVIDIA의 nvcr.io/nvidia/tensorflow:23.07-tf2-py3 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 12.1을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '2.12.0', '{"jupyter": 8888}',  'jupyter-lab:xiilab/astrago:tensorflow-23.07-cuda12.1', 'JUPYTER'),
        (22, 'jupyter lab --allow-root --ip=* --port=8888 --NotebookApp.token='''' --NotebookApp.allow_origin=''*'' --notebook-dir=/', '11.8', 'NVIDIA의 nvcr.io/nvidia/tensorflow:22.12-tf2-py3 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.8을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '2.10.1', '{"jupyter": 8888}',  'jupyter-lab:xiilab/astrago:tensorflow-22.12-cuda11.8', 'JUPYTER'),
        (23, 'jupyter lab --allow-root --ip=* --port=8888 --NotebookApp.token='''' --NotebookApp.allow_origin=''*'' --notebook-dir=/', '11.7', 'NVIDIA의 nvcr.io/nvidia/tensorflow:22.08-tf2-py3 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.7을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '2.9.1', '{"jupyter": 8888}',  'jupyter-lab:xiilab/astrago:tensorflow-22.08-cuda11.7', 'JUPYTER'),
        (24, 'jupyter lab --allow-root --ip=* --port=8888 --NotebookApp.token='''' --NotebookApp.allow_origin=''*'' --notebook-dir=/', '11.6', 'NVIDIA의 nvcr.io/nvidia/tensorflow:22.04-tf2-py3 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.6을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '2.8.0', '{"jupyter": 8888}',  'jupyter-lab:xiilab/astrago:tensorflow-22.04-cuda11.6', 'JUPYTER'),
        (25, 'jupyter lab --allow-root --ip=* --port=8888 --NotebookApp.token='''' --NotebookApp.allow_origin=''*'' --notebook-dir=/', '11.3', 'NVIDIA의 nvcr.io/nvidia/tensorflow:21.06-tf2-py3 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.3을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '2.5.0', '{"jupyter": 8888}',  'jupyter-lab:xiilab/astrago:tensorflow-21.06-cuda11.3', 'JUPYTER'),
        (26, 'jupyter lab --allow-root --ip=* --port=8888 --NotebookApp.token='''' --NotebookApp.allow_origin=''*'' --notebook-dir=/', '11.1', 'NVIDIA의 nvcr.io/nvidia/tensorflow:20.12-tf2-py3 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.1을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '2.3.1', '{"jupyter": 8888}',  'jupyter-lab:xiilab/astrago:tensorflow-20.12-cuda11.1', 'JUPYTER'),
        (27, null, '12.1', 'NVIDIA의 nvcr.io/nvidia/pytorch:23.07-py 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 12.1을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '2.1.0', null,  'xiilab/astrago:pytorch-23.07-cuda12.1', 'PYTORCH'),
        (28, null, '11.8', 'NVIDIA의 nvcr.io/nvidia/pytorch:22.12-py 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.8을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '1.14.0', null, 'xiilab/astrago:pytorch-22.12-cuda11.8', 'PYTORCH'),
        (29, null, '11.7', 'NVIDIA의 nvcr.io/nvidia/pytorch:22.08-py 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.7을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '1.13.0', null, 'xiilab/astrago:pytorch-22.08-cuda11.7', 'PYTORCH'),
        (30, null, '11.6', 'NVIDIA의 nvcr.io/nvidia/pytorch:22.04-py 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.6을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '1.12.0', null, 'xiilab/astrago:pytorch-22.04-cuda11.6', 'PYTORCH'),
        (31, null, '11.3', 'NVIDIA의 nvcr.io/nvidia/pytorch:21.06-py 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.3을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '1.9.0', null,  'xiilab/astrago:pytorch-21.06-cuda11.3', 'PYTORCH'),
        (32, null, '11.1', 'NVIDIA의 nvcr.io/nvidia/pytorch:20.12-py 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.1을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '1.8.0', null,  'xiilab/astrago:pytorch-20.12-cuda11.1', 'PYTORCH'),
        (33, null, '12.1', 'NVIDIA의 nvcr.io/nvidia/tensorflow:23.07-tf2-py3 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 12.1을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '2.12.0', null, 'xiilab/astrago:tensorflow-23.07-cuda12.1', 'TENSORFLOW'),
        (34, null, '11.8', 'NVIDIA의 nvcr.io/nvidia/tensorflow:22.12-tf2-py3 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.8을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '2.10.1', null, 'xiilab/astrago:tensorflow-22.12-cuda11.8', 'TENSORFLOW'),
        (35, null, '11.7', 'NVIDIA의 nvcr.io/nvidia/tensorflow:22.08-tf2-py3 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.7을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '2.9.1', null,  'xiilab/astrago:tensorflow-22.08-cuda11.7', 'TENSORFLOW'),
        (36, null, '11.6', 'NVIDIA의 nvcr.io/nvidia/tensorflow:22.04-tf2-py3 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.6을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '2.8.0', null,  'xiilab/astrago:tensorflow-22.04-cuda11.6', 'TENSORFLOW'),
        (37, null, '11.3', 'NVIDIA의 nvcr.io/nvidia/tensorflow:21.06-tf2-py3 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.3을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '2.5.0', null,  'xiilab/astrago:tensorflow-21.06-cuda11.3', 'TENSORFLOW'),
        (38, null, '11.1', 'NVIDIA의 nvcr.io/nvidia/tensorflow:20.12-tf2-py3 이미지를 기반으로 하여, PyTorch를 활용한 딥러닝 연구 및 개발을 더욱 효율적으로 수행할 수 있도록 설계된 이미지입니다. 이 이미지에는 Jupyter Lab과 Visual Studio Code (VS Code)가 사전 설치되어 있으며, CUDA 11.1을 지원하여 최신 GPU 가속 기능을 최대한 활용할 수 있습니다.', '2.3.1', null,  'xiilab/astrago:tensorflow-20.12-cuda11.1', 'TENSORFLOW'),
        (39, null, '11.3', 'horovod 실행을 위한 이미지 입니다.', '1.12.1', null, 'torch1.12.1-horovod-cuda11.3', 'PYTORCH')
ON DUPLICATE KEY UPDATE IMAGE_ID = IMAGE_ID;

-- 허브 초기값
INSERT INTO TB_HUB (HUB_ID, MOD_DATE, REG_DATE, ENVS, PORTS, COMMAND, DATASET_MOUNT_PATH, DESCRIPTION, MODEL_MOUNT_PATH,
                    REG_USER_ID, REG_USER_NAME, REG_USER_REAL_NAME,
                    TITLE, IMAGE_ID, WORKLOAD_TYPE, PARAMETER, README_FILE_NAME, THUMBNAIL_FILE_NAME)
VALUES (1, null, now(),
        '{     "DB_HOST": "astrago-mariadb.astrago.svc",     "DB_PORT": "3306",     "DB_USER": "root",     "DB_PASSWORD": "root",     "DB_DATABASE": "astrago" }',
        null,
        'python /usr/src/ultralytics/train.py',
        '/tmp/datasets',
        'YOLO(You Only Look Once)는 Object detection 모델 중 하나로, 높은 속도와 정확도를 가집니다. YOLO의 핵심 아이디어는 이미지를 단 한번만 보고 (Object detection을 위해 이미지를 단일 네트워크 패스를 통해 처리함으로써) 객체의 위치와 분류를 동시에 예측하는 것입니다. 해당 모델은 실시간 처리가 필요한 응용 프로그램에서 특히 유용합니다.',
        '/tmp/models', 'SYSTEM', 'SYSTEM', 'SYSTEM', 'YOLOv8', 1,
        'BATCH',
        '{ "data_dir": "./ultralytics/cfg/datasets/coco128.yaml", "save_model_dir": "./default_train_result", "flops": 14, "t_img_num": 100, "v_img_num": 100, "image_size": 640, "batch": 16, "param_cnt": 43643718, "epoch": 120, "learning_rate": 0.01 }'
           , 'YOLOv8_README.md', 'YOLOv8_Thumbnail.gif'),
       (2, null, now(),
        '{     "DB_HOST": "astrago-mariadb.astrago.svc",     "DB_PORT": "3306",     "DB_USER": "root",     "DB_PASSWORD": "root",     "DB_DATABASE": "astrago" }',
        null,
        'python /usr/src/ultralytics/train.py', '/tmp/datasets',
        'YOLO(You Only Look Once)는 Object detection 모델 중 하나로, 높은 속도와 정확도를 가집니다. YOLO의 핵심 아이디어는 이미지를 단 한번만 보고 (Object detection을 위해 이미지를 단일 네트워크 패스를 통해 처리함으로써) 객체의 위치와 분류를 동시에 예측하는 것입니다. 해당 모델은 실시간 처리가 필요한 응용 프로그램에서 특히 유용합니다.',
        '/tmp/models', 'SYSTEM', 'SYSTEM', 'SYSTEM',
        'YOLOv5', 2,
        'BATCH',
        '{ "data_dir": "./ultralytics/cfg/datasets/coco128.yaml", "save_model_dir": "./default_train_result", "flops": 14, "t_img_num": 100, "v_img_num": 100, "image_size": 640, "batch": 16, "param_cnt": 53177222, "epoch": 120, "learning_rate": 0.01 }'
           , 'YOLOv8_README.md', 'YOLOv8_Thumbnail.gif')
ON DUPLICATE KEY UPDATE HUB_ID = HUB_ID;

-- 허브 카테고리 초기값
INSERT INTO TB_HUB_CATEGORY (HUB_CATEGORY_ID, MOD_DATE, REG_DATE, REG_USER_ID, REG_USER_NAME, REG_USER_REAL_NAME,
                             LABEL_TYPE)
VALUES (1, null, now(), 'SYSTEM', 'SYSTEM', 'SYSTEM', 'OBJECT_DETECTION'),
       (2, null, now(), 'SYSTEM', 'SYSTEM', 'SYSTEM', 'IMAGE_SEGMENTATION')
ON DUPLICATE KEY UPDATE HUB_CATEGORY_ID= HUB_CATEGORY_ID;

INSERT INTO TB_NETWORK
(NETWORK_ID, INIT_CONTAINER_IMAGE_URL, LOCAL_VOLUME_IMAGE_URL, NETWORK_CLOSE_YN)
VALUES (1, 'xiilab/git-sync:v3.6.0', 'xiilab/astrago-dataset-nginx', 'N')
ON DUPLICATE KEY UPDATE NETWORK_ID= NETWORK_ID;

insert into TB_RESOURCE_PRESET (RESOURCE_PRESET_ID, TITLE, DESCRIPTION, LAUNCHER_CPU_USAGE, LAUNCHER_MEM_USAGE, GPU_USAGE, CPU_USAGE, MEM_USAGE, NODE_TYPE, REG_DATE, MOD_DATE, REG_USER_ID, REG_USER_NAME, REG_USER_REAL_NAME)
values  (1, 'SMALL', 'SMALL', null, null, 1, 2.0, 4.0, 'SINGLE', now(), null, 'SYSTEM', 'SYSTEM', 'SYSTEM'),
        (2, 'MEDIUM', 'MEDIUM', null, null, 1, 4.0, 8.0, 'SINGLE', now(), null, 'SYSTEM', 'SYSTEM', 'SYSTEM'),
        (3, 'LARGE', 'LARGE', null, null, 2, 8.0, 16.0, 'SINGLE', now(), null, 'SYSTEM', 'SYSTEM', 'SYSTEM'),
        (5, 'LARGE', 'LARGE', 5.0, 10.0, 4, 8.0, 16.0, 'MULTI', now(), null, 'SYSTEM', 'SYSTEM', 'SYSTEM'),
        (6, 'MEDIUM', 'MEDIUM', 3.0, 7.0, 3, 4.0, 8.0, 'MULTI', now(), null, 'SYSTEM', 'SYSTEM', 'SYSTEM'),
        (8, 'SMALL', 'SMALL', 2.0, 5.0, 2, 2.0, 4.0, 'MULTI', now(), null, 'SYSTEM', 'SYSTEM', 'SYSTEM')
    ON DUPLICATE KEY UPDATE RESOURCE_PRESET_ID = RESOURCE_PRESET_ID;
