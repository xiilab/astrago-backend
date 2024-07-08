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
INSERT INTO TB_IMAGE (IMAGE_TYPE, IMAGE_ID, MOD_DATE, REG_DATE, REG_USER_ID, REG_USER_NAME, REG_USER_REAL_NAME,
                      DELETE_YN, IMAGE_NAME, REPOSITORY_AUTH_TYPE, WORKLOAD_TYPE, MULTI_NODE)
VALUES ('HUB', 1, null, now(), 'SYSTEM', 'SYSTEM', 'SYSTEM', 'N', 'xiilab/astrago:hub-yolov8', 'PUBLIC', 'BATCH', false),
       ('HUB', 2, null, now(), 'SYSTEM', 'SYSTEM', 'SYSTEM', 'N', 'xiilab/astrago:hub-yolov8', 'PUBLIC', 'BATCH', false),
       ('BUILT', 3, null, now(), 'SYSTEM', 'SYSTEM', 'SYSTEM', 'N',
        'xiilab/astrago:torch1.7.1-tensorflow2.4.1-cuda11.0', 'PUBLIC', 'INTERACTIVE', false),
       ('BUILT', 4, null, now(), 'SYSTEM', 'SYSTEM', 'SYSTEM', 'N',
        'xiilab/astrago:torch2.0.1-tensorflow2.11.0-cuda11.7', 'PUBLIC', 'INTERACTIVE', false),
       ('BUILT', 5, null, now(), 'SYSTEM', 'SYSTEM', 'SYSTEM', 'N',
        'xiilab/astrago:torch2.2.1-tensorflow2.14.0-cuda12.1', 'PUBLIC', 'INTERACTIVE', false),
       ('BUILT', 6, null, now(), 'SYSTEM', 'SYSTEM', 'SYSTEM', 'N',
        'xiilab/astrago:torch2.2.1-tensorflow2.14.0-cuda11.8', 'PUBLIC', 'INTERACTIVE', false),
       ('BUILT', 7, null, now(), 'b7b2a599-9ca0-4bed-b2bd-a015a568bd6d', 'SYSTEM', 'SYSTEM', 'N',
        'xiilab/astrago:torch1.7.1-tensorflow2.4.1-cuda11.0', 'PUBLIC', 'INTERACTIVE', false),
       ('BUILT', 8, null, now(), 'b7b2a599-9ca0-4bed-b2bd-a015a568bd6d', 'SYSTEM', 'SYSTEM', 'N',
        'xiilab/astrago:torch2.0.1-tensorflow2.11.0-cuda11.7', 'PUBLIC', 'INTERACTIVE', false),
       ('BUILT', 9, null, now(), 'b7b2a599-9ca0-4bed-b2bd-a015a568bd6d', 'SYSTEM', 'SYSTEM', 'N',
        'xiilab/astrago:torch2.2.1-tensorflow2.14.0-cuda12.1', 'PUBLIC', 'INTERACTIVE', false),
       ('BUILT', 10, null, now(), 'b7b2a599-9ca0-4bed-b2bd-a015a568bd6d', 'SYSTEM', 'SYSTEM', 'N',
        'xiilab/astrago:torch2.2.1-tensorflow2.14.0-cuda11.8', 'PUBLIC', 'INTERACTIVE', false),
       ('BUILT', 11, null, now(), 'SYSTEM', 'SYSTEM', 'SYSTEM', 'N',
        'xiilab/astrago:torch1.7.1-tensorflow2.4.1-cuda11.0', 'PUBLIC', 'BATCH', false),
       ('BUILT', 12, null, now(), 'SYSTEM', 'SYSTEM', 'SYSTEM', 'N',
        'xiilab/astrago:torch2.0.1-tensorflow2.11.0-cuda11.7', 'PUBLIC', 'BATCH', false),
       ('BUILT', 13, null, now(), 'SYSTEM', 'SYSTEM', 'SYSTEM', 'N',
        'xiilab/astrago:torch2.2.1-tensorflow2.14.0-cuda12.1', 'PUBLIC', 'BATCH', false),
       ('BUILT', 14, null, now(), 'SYSTEM', 'SYSTEM', 'SYSTEM', 'N',
        'xiilab/astrago:torch2.2.1-tensorflow2.14.0-cuda11.8', 'PUBLIC', 'BATCH', false),
       ('BUILT', 15, null, now(), 'SYSTEM', 'SYSTEM', 'SYSTEM', 'N',
        'xiilab/horovod', 'PUBLIC', 'DISTRIBUTED', false)
ON DUPLICATE KEY UPDATE IMAGE_ID = IMAGE_ID;

-- 허브 이미지 초기값
INSERT INTO TB_HUB_IMAGE (IMAGE_ID)
VALUES (1),
       (2)
ON DUPLICATE KEY UPDATE IMAGE_ID = IMAGE_ID;

-- 빌트인 이미지 초기값
INSERT INTO TB_BUILT_IN_IMAGE
(IMAGE_ID, DESCRIPTION, THUMBNAIL_SAVE_FILENAME, THUMBNAIL_SAVE_PATH, TITLE, FRAMEWORK_TYPE, CUDA_VERSION,
 FRAMEWORK_VERSION, PORTS, COMMAND)
VALUES (3, 'vs-code:torch1.7.1-tensorflow2.4.1-cuda11.0', null, null, 'vs-code:torch1.7.1-tensorflow2.4.1-cuda11.0',
        'VSCODE', '11.0', '1.7.1', '{"vscode": 8080}', 'code-server'),
       (4, 'vs-code:torch2.0.1-tensorflow2.11.0-cuda11.7', null, null, 'vs-code:torch2.0.1-tensorflow2.11.0-cuda11.7',
        'VSCODE', '11.7', '2.0.1', '{"vscode": 8080}', 'code-server'),
       (5, 'vs-code:torch2.2.1-tensorflow2.14.0-cuda12.1', null, null, 'vs-code:torch2.2.1-tensorflow2.14.0-cuda12.1',
        'VSCODE', '12.1', '2.2.1', '{"vscode": 8080}', 'code-server'),
       (6, 'vs-code:torch2.2.1-tensorflow2.14.0-cuda11.8', null, null, 'vs-code:torch2.2.1-tensorflow2.14.0-cuda11.8',
        'VSCODE', '11.8', '2.2.1', '{"vscode": 8080}', 'code-server'),
       (7, 'jupyter-notebook:torch1.7.1-tensorflow2.4.1-cuda11.0', null, null,
        'jupyter-notebook:torch1.7.1-tensorflow2.4.1-cuda11.0', 'JUPYTER', '11.0', '1.7.1', '{"jupyter": 8888}',
        'jupyter notebook'),
       (8, 'jupyter-notebook:torch2.0.1-tensorflow2.11.0-cuda11.7', null, null,
        'jupyter-notebook:torch2.0.1-tensorflow2.11.0-cuda11.7', 'JUPYTER', '11.7', '2.0.1', '{"jupyter": 8888}',
        'jupyter notebook'),
       (9, 'jupyter-notebook:torch2.2.1-tensorflow2.14.0-cuda12.1', null, null,
        'jupyter-notebook:torch2.2.1-tensorflow2.14.0-cuda12.1', 'JUPYTER', '12.1', '2.2.1', '{"jupyter": 8888}',
        'jupyter notebook'),
       (10, 'jupyter-notebook:torch2.2.1-tensorflow2.14.0-cuda11.8', null, null,
        'jupyter-notebook:torch2.2.1-tensorflow2.14.0-cuda11.8', 'JUPYTER', '11.8', '2.2.1', '{"jupyter": 8888}',
        'jupyter notebook'),
       (11, 'torch1.7.1-tensorflow2.4.1-cuda11.0', null, null, 'torch1.7.1-tensorflow2.4.1-cuda11.0', 'PYTORCH', '11.0',
        '1.7.1', null, null),
       (12, 'torch2.0.1-tensorflow2.11.0-cuda11.7', null, null, 'torch2.0.1-tensorflow2.11.0-cuda11.7', 'PYTORCH',
        '11.7', '2.0.1', null, null),
       (13, 'torch2.2.1-tensorflow2.14.0-cuda12.1', null, null, 'torch2.2.1-tensorflow2.14.0-cuda12.1', 'PYTORCH',
        '12.1', '2.2.1', null, null),
       (14, 'torch2.2.1-tensorflow2.14.0-cuda11.8', null, null, 'torch2.2.1-tensorflow2.14.0-cuda11.8', 'PYTORCH',
        '11.8', '2.2.1', null, null)
ON DUPLICATE KEY UPDATE IMAGE_ID = IMAGE_ID;

-- 허브 초기값
INSERT INTO TB_HUB (HUB_ID, MOD_DATE, REG_DATE, ENVS, PORTS, COMMAND, DATASET_MOUNT_PATH, DESCRIPTION, MODEL_MOUNT_PATH,
                    REG_USER_ID, REG_USER_NAME, REG_USER_REAL_NAME, SOURCE_CODE_BRANCH, SOURCE_CODE_MOUNT_PATH,
                    SOURCE_CODE_URL, TITLE, IMAGE_ID, README_URL,
                    THUMBNAIL_URL, WORKLOAD_TYPE, PARAMETER)
VALUES (1, null, now(),
        '{     "DB_HOST": "astrago-mariadb.astrago.svc",     "DB_PORT": "3306",     "DB_USER": "root",     "DB_PASSWORD": "root",     "DB_DATABASE": "astrago" }',
        null,
        'python train.py',
        '/tmp/datasets',
        'YOLO(You Only Look Once)는 Object detection 모델 중 하나로, 높은 속도와 정확도를 가집니다. YOLO의 핵심 아이디어는 이미지를 단 한번만 보고 (Object detection을 위해 이미지를 단일 네트워크 패스를 통해 처리함으로써) 객체의 위치와 분류를 동시에 예측하는 것입니다. 해당 모델은 실시간 처리가 필요한 응용 프로그램에서 특히 유용합니다.',
        '/tmp/models', 'SYSTEM', 'SYSTEM', 'SYSTEM', 'master', '/usr/src/ultralytics',
        'https://github.com/xiilab/astrago-ultralytics-yolov8-train.git', 'YOLOv8', 1,
        'https://raw.githubusercontent.com/xiilab/astrago-hub/master/YOLOv8/Readme.md',
        'https://raw.githubusercontent.com/xiilab/astrago-hub/master/YOLOv8/images/YOLOv8_Thumbnail.gif',
        'BATCH',
        '{ "data_dir": "./ultralytics/cfg/datasets/coco128.yaml", "save_model_dir": "./default_train_result", "flops": 14, "t_img_num": 100, "v_img_num": 100, "image_size": 640, "batch": 16, "param_cnt": 43643718, "epoch": 120, "learning_rate": 0.01 }'),
       (2, null, now(),
        '{     "DB_HOST": "astrago-mariadb.astrago.svc",     "DB_PORT": "3306",     "DB_USER": "root",     "DB_PASSWORD": "root",     "DB_DATABASE": "astrago" }',
        null,
        'python train.py', '/tmp/datasets',
        'YOLO(You Only Look Once)는 Object detection 모델 중 하나로, 높은 속도와 정확도를 가집니다. YOLO의 핵심 아이디어는 이미지를 단 한번만 보고 (Object detection을 위해 이미지를 단일 네트워크 패스를 통해 처리함으로써) 객체의 위치와 분류를 동시에 예측하는 것입니다. 해당 모델은 실시간 처리가 필요한 응용 프로그램에서 특히 유용합니다.',
        '/tmp/models', 'SYSTEM', 'SYSTEM', 'SYSTEM', 'master',
        '/usr/src/ultralytics',
        'https://github.com/xiilab/astrago-ultralytics-yolov8-train.git', 'YOLOv5', 2,
        'https://raw.githubusercontent.com/xiilab/astrago-hub/master/YOLOv5/Readme.md',
        'https://raw.githubusercontent.com/xiilab/astrago-hub/master/YOLOv8/images/YOLOv8_Thumbnail.gif',
        'BATCH',
        '{ "data_dir": "./ultralytics/cfg/datasets/coco128.yaml", "save_model_dir": "./default_train_result", "flops": 14, "t_img_num": 100, "v_img_num": 100, "image_size": 640, "batch": 16, "param_cnt": 53177222, "epoch": 120, "learning_rate": 0.01 }')
ON DUPLICATE KEY UPDATE HUB_ID = HUB_ID;

-- 허브 카테고리 초기값
INSERT INTO TB_HUB_CATEGORY (HUB_CATEGORY_ID, MOD_DATE, REG_DATE, REG_USER_ID, REG_USER_NAME, REG_USER_REAL_NAME,
                             LABEL_TYPE)
VALUES (1, null, now(), 'SYSTEM', 'SYSTEM', 'SYSTEM', 'OBJECT_DETECTION'),
       (2, null, now(), 'SYSTEM', 'SYSTEM', 'SYSTEM', 'IMAGE_SEGMENTATION')
ON DUPLICATE KEY UPDATE HUB_CATEGORY_ID= HUB_CATEGORY_ID;

INSERT INTO TB_NETWORK
(NETWORK_ID, PRIVATE_REPOSITORY_URL, INIT_CONTAINER_IMAGE_URL, LOCAL_VOLUME_IMAGE_URL, NETWORK_CLOSE_YN)
VALUES (1, null, 'k8s.gcr.io/git-sync/git-sync:v3.6.0', 'xiilab/astrago-dataset-nginx', 'N')
ON DUPLICATE KEY UPDATE NETWORK_ID= NETWORK_ID;
