create table if not exists TB_ADMIN_ALERT_MAPPING
(
    ADMIN_ALERT_MAPPING_ID bigint auto_increment
    primary key,
    ALERT_ID               bigint             null,
    MOD_DATE               datetime(6)        null,
    REG_DATE               datetime(6)        null,
    ADMIN_ID               varchar(255)       null,
    REG_USER_ID            varchar(255)       null,
    REG_USER_NAME          varchar(255)       null,
    REG_USER_REAL_NAME     varchar(255)       null,
    EMAIL_ALERT_STATUS     enum ('ON', 'OFF') null,
    SYSTEM_ALERT_STATUS    enum ('ON', 'OFF') null
    );

create index if not exists FKeg0s0dscviqh2xu5bc1u8mys7
    on TB_ADMIN_ALERT_MAPPING (ALERT_ID);

create table if not exists TB_ALERT
(
    ALERT_ID         bigint auto_increment
    primary key,
    ALERT_NAME       varchar(255)                                                                    null,
    ALERT_EVENT_TYPE enum ('NOTIFICATION', 'ERROR', 'DELETE', 'USER')                                null,
    ALERT_ROLE       enum ('USER', 'ADMIN', 'OWNER')                                                 null,
    ALERT_TYPE       enum ('USER', 'WORKSPACE', 'WORKLOAD', 'LICENSE', 'NODE', 'RESOURCE', 'MEMBER') null
    );

create table if not exists TB_ALERT_MANAGER
(
    ID           bigint auto_increment
    primary key,
    NAME         varchar(255) null,
    EMAIL_YN     bit          null,
    SYSTEM_YN    bit          null,
    ALERT_ENABLE bit          null
    );

create table if not exists TB_ALERT_MANAGER_CATEGORY
(
    ALERT_MANAGER_CATEGORY_ID bigint auto_increment
    primary key,
    alertManager_ID           bigint                                                                                  null,
    durationTime              varchar(255)                                                                            null,
    maximum                   varchar(255)                                                                            null,
    operator                  varchar(255)                                                                            null,
    alertManagerCategoryType  enum ('GPU_TEMP', 'GPU_MEMORY', 'GPU_USAGE', 'MEMORY_USAGE', 'CPU_USAGE', 'DISK_USAGE') null,
    constraint FK55n56bek1dj2pobhv9umcdatu
    foreign key (alertManager_ID) references TB_ALERT_MANAGER (ID)
    );

create table if not exists TB_ALERT_MANAGER_NODE
(
    ALERT_MANAGER_NODE_ID bigint auto_increment
    primary key,
    alertManager_ID       bigint       null,
    nodeIp                varchar(255) null,
    nodeName              varchar(255) null,
    constraint FKspahac46sw0u3cqxhggn5mt4d
    foreign key (alertManager_ID) references TB_ALERT_MANAGER (ID)
    );

create table if not exists TB_ALERT_MANAGER_RECEIVE
(
    result          bit                                                                                     not null,
    alertManager_ID bigint                                                                                  null,
    id              bigint auto_increment
    primary key,
    realTime        datetime(6)                                                                             null,
    alertName       varchar(255)                                                                            null,
    currentTime     varchar(255)                                                                            null,
    nodeIp          varchar(255)                                                                            null,
    nodeName        varchar(255)                                                                            null,
    threshold       varchar(255)                                                                            null,
    categoryType    enum ('GPU_TEMP', 'GPU_MEMORY', 'GPU_USAGE', 'MEMORY_USAGE', 'CPU_USAGE', 'DISK_USAGE') null,
    constraint FKah11gbwo9ps5897tx0jbcsn4d
    foreign key (alertManager_ID) references TB_ALERT_MANAGER (ID)
    );

create table if not exists TB_ALERT_MANAGER_USER
(
    ALERT_MANAGER_USER_ID bigint auto_increment
    primary key,
    alertManager_ID       bigint       null,
    USER_EMAIL            varchar(255) null,
    USER_FIRST_NAME       varchar(255) null,
    USER_ID               varchar(255) null,
    USER_LAST_NAME        varchar(255) null,
    USER_NAME             varchar(255) null,
    constraint FKdj022e27gu72yhf0oyp7x0g0
    foreign key (alertManager_ID) references TB_ALERT_MANAGER (ID)
    );

create table if not exists TB_ALERT_SETTING
(
    ALERT_SETTING_ID        bigint auto_increment
    primary key,
    RESOURCE_APPROVAL_ALERT bit          null,
    WORKLOAD_END_ALERT      bit          null,
    WORKLOAD_START_ALERT    bit          null,
    WORKLOAD_ERROR_ALERT    bit          null,
    WORKSPACE_NAME          varchar(255) null
    );

create table if not exists TB_ASTRAGO_DATASET
(
    DATASET_ID   bigint       not null
    primary key,
    STORAGE_ID   bigint       null,
    DATASET_PATH varchar(255) null
    );

create index if not exists FKpaj2vxklkof11g5sqlcbdwlo2
    on TB_ASTRAGO_DATASET (STORAGE_ID);

create table if not exists TB_ASTRAGO_MODEL
(
    MODEL_ID   bigint       not null
    primary key,
    STORAGE_ID bigint       null,
    MODEL_PATH varchar(255) null
    );

create index if not exists FKl0nh2hu20h8u5j9xw7wqiwm16
    on TB_ASTRAGO_MODEL (STORAGE_ID);

create table if not exists TB_BUILT_IN_IMAGE
(
    IMAGE_ID                bigint                                              not null
    primary key,
    DESCRIPTION             varchar(255)                                        null,
    THUMBNAIL_SAVE_FILENAME varchar(255)                                        null,
    THUMBNAIL_SAVE_PATH     varchar(255)                                        null,
    TITLE                   varchar(255)                                        null,
    FRAMEWORK_TYPE          enum ('PYTORCH', 'TENSORFLOW', 'JUPYTER', 'VSCODE') null,
    CUDA_VERSION            varchar(255)                                        null,
    FRAMEWORK_VERSION       varchar(255)                                        null,
    PORTS                   varchar(1000)                                       null,
    COMMAND                 varchar(255)                                        null
    );

create table if not exists TB_CODE
(
    CODE_ID                 bigint auto_increment
    primary key,
    CREDENTIAL_ID           bigint                       null,
    MOD_DATE                datetime(6)                  null,
    CODE_ARGS               varchar(1000)                null,
    REG_DATE                datetime(6)                  null,
    CODE_URL                varchar(255)                 null,
    REG_USER_ID             varchar(255)                 null,
    REG_USER_NAME           varchar(255)                 null,
    REG_USER_REAL_NAME      varchar(255)                 null,
    TITLE                   varchar(255)                 null,
    WORKSPACE_NAME          varchar(255)                 null,
    CODE_TYPE               enum ('GIT_HUB', 'GIT_LAB')  null,
    CODE_CMD                varchar(255)                 null,
    DELETE_YN               enum ('Y', 'N')              null,
    REPOSITORY_TYPE         enum ('WORKSPACE', 'USER')   null,
    CODE_DEFAULT_MOUNT_PATH varchar(255) charset utf8mb4 null
    );

create index if not exists FKfhqjmmbm3u5illhwv2axw3q5m
    on TB_CODE (CREDENTIAL_ID);

create table if not exists TB_CODE_WORKLOAD_MAPPING
(
    CODE_ID                  bigint          null,
    CODE_WORKLOAD_MAPPING_ID bigint auto_increment
    primary key,
    WORKLOAD_ID              bigint          null,
    branch                   varchar(255)    null,
    mountPath                varchar(255)    null,
    DELETE_YN                enum ('Y', 'N') null
    );

create index if not exists FK19uw3ftyw8nvw1hqh9ce8rj1s
    on TB_CODE_WORKLOAD_MAPPING (CODE_ID);

create index if not exists FK74triv6qxjapj1wr1oensbpc5
    on TB_CODE_WORKLOAD_MAPPING (WORKLOAD_ID);

create table if not exists TB_CREDENTIAL
(
    CREDENTIAL_CREATED_AT  datetime(6)            null,
    CREDENTIAL_ID          bigint auto_increment
    primary key,
    MOD_DATE               datetime(6)            null,
    REG_DATE               datetime(6)            null,
    CREDENTIAL_DESCRIPTION varchar(255)           null,
    CREDENTIAL_LOGIN_ID    varchar(255)           null,
    CREDENTIAL_LOGIN_PW    varchar(255)           null,
    CREDENTIAL_NAME        varchar(255)           null,
    REG_USER_ID            varchar(255)           null,
    REG_USER_NAME          varchar(255)           null,
    REG_USER_REAL_NAME     varchar(255)           null,
    CREDENTIAL_TYPE        enum ('GIT', 'DOCKER') null
    );

create table if not exists TB_CUSTOM_IMAGE
(
    CREDENTIAL_ID bigint null,
    IMAGE_ID      bigint not null
    primary key
);

create index if not exists FKgm34fhdfqxsac9x4hi11ox1g6
    on TB_CUSTOM_IMAGE (CREDENTIAL_ID);

create table if not exists TB_DATASET
(
    DATASET_ID                 bigint auto_increment
    primary key,
    DATASET_SIZE               bigint                       null,
    MOD_DATE                   datetime(6)                  null,
    REG_DATE                   datetime(6)                  null,
    DIVISION                   varchar(31)                  not null,
    DATASET_NAME               varchar(255)                 null,
    REG_USER_ID                varchar(255)                 null,
    REG_USER_NAME              varchar(255)                 null,
    REG_USER_REAL_NAME         varchar(255)                 null,
    DELETE_YN                  enum ('Y', 'N')              null,
    DATASET_DEFAULT_MOUNT_PATH varchar(255) charset utf8mb4 null
    );

create table if not exists TB_DATASET_WORKLOAD_MAPPING
(
    DATASET_ID                  bigint          null,
    DATASET_WORKLOAD_MAPPING_ID bigint auto_increment
    primary key,
    WORKLOAD_ID                 bigint          null,
    MOUNT_PATH                  varchar(255)    null,
    DELETE_YN                   enum ('Y', 'N') null
    );

create index if not exists FK7x1haqbxbxconk5wsx5a6whig
    on TB_DATASET_WORKLOAD_MAPPING (DATASET_ID);

create index if not exists FKp298p516g0qdn27nnkrx6f794
    on TB_DATASET_WORKLOAD_MAPPING (WORKLOAD_ID);

create table if not exists TB_DATASET_WORKSPACE_MAPPING
(
    DATASET_ID                   bigint                       null,
    DATASET_WORKSPACE_MAPPING_ID bigint auto_increment
    primary key,
    MOD_DATE                     datetime(6)                  null,
    REG_DATE                     datetime(6)                  null,
    REG_USER_ID                  varchar(255)                 null,
    REG_USER_NAME                varchar(255)                 null,
    REG_USER_REAL_NAME           varchar(255)                 null,
    WORKSPACE_RESOURCE_NAME      varchar(255)                 null,
    DATASET_DEFAULT_MOUNT_PATH   varchar(255) charset utf8mb4 null
    );

create index if not exists FK9qby3ffl2wm930r3c8tita38k
    on TB_DATASET_WORKSPACE_MAPPING (DATASET_ID);

create table if not exists TB_ENV
(
    ENV_ID               bigint auto_increment
    primary key,
    workload_WORKLOAD_ID bigint       null,
    ENV_KEY              varchar(255) null,
    ENV_VALUE            varchar(255) null
    );

create index if not exists FKd82wkd6wxxlhcrb8ia9ivfwg0
    on TB_ENV (workload_WORKLOAD_ID);

create table if not exists TB_FRAMEWORK_VERSION
(
    FRAMEWORK_VERSION_ID bigint auto_increment
    primary key,
    MOD_DATE             datetime(6)                    null,
    REG_DATE             datetime(6)                    null,
    CUDA_VERSION         varchar(255)                   null,
    FRAMEWORK_VERSION    varchar(255)                   null,
    FRAMEWORK_TYPE       enum ('PYTORCH', 'TENSORFLOW') null
    );

create table if not exists TB_COMPATIBLE_FRAMEWORK_VERSION
(
    COMPATIBLE_FRAMEWORK_VERSION_ID bigint auto_increment
    primary key,
    FRAMEWORK_VERSION_ID            bigint      null,
    MOD_DATE                        datetime(6) null,
    REG_DATE                        datetime(6) null,
    constraint UK_94e1oh8vgefyxvfpjmr9hn068
    unique (FRAMEWORK_VERSION_ID),
    constraint FK9e3k31x0nru6ligbq65098di7
    foreign key (FRAMEWORK_VERSION_ID) references TB_FRAMEWORK_VERSION (FRAMEWORK_VERSION_ID)
    );

create table if not exists TB_HUB
(
    HUB_ID                 bigint auto_increment
    primary key,
    MOD_DATE               datetime(6)                             null,
    REG_DATE               datetime(6)                             null,
    ENVS                   varchar(1000)                           null,
    PORTS                  varchar(1000)                           null,
    COMMAND                varchar(255)                            null,
    DATASET_MOUNT_PATH     varchar(255)                            null,
    DESCRIPTION            varchar(255)                            null,
    MODEL_MOUNT_PATH       varchar(255)                            null,
    REG_USER_ID            varchar(255)                            null,
    REG_USER_NAME          varchar(255)                            null,
    REG_USER_REAL_NAME     varchar(255)                            null,
    SOURCE_CODE_BRANCH     varchar(255)                            null,
    SOURCE_CODE_MOUNT_PATH varchar(255)                            null,
    SOURCE_CODE_URL        varchar(255)                            null,
    TITLE                  varchar(255)                            null,
    IMAGE_ID               bigint                                  null,
    README_URL             varchar(255)                            null,
    THUMBNAIL_URL          varchar(255)                            null,
    WORKLOAD_TYPE          enum ('BATCH', 'INTERACTIVE', 'DEPLOY') null,
    HYPER_PARAMS           varchar(1000)                           null
    );

create index if not exists FKqu0nc3vfvgnvh9ro3pyuqwd9f
    on TB_HUB (IMAGE_ID);

create table if not exists TB_HUB_CATEGORY
(
    HUB_CATEGORY_ID    bigint auto_increment
    primary key,
    MOD_DATE           datetime(6)                                                             null,
    REG_DATE           datetime(6)                                                             null,
    REG_USER_ID        varchar(255)                                                            null,
    REG_USER_NAME      varchar(255)                                                            null,
    REG_USER_REAL_NAME varchar(255)                                                            null,
    LABEL_TYPE         enum ('OBJECT_DETECTION', 'IMAGE_SEGMENTATION', 'IMAGE_CLASSIFICATION') not null,
    constraint UK_4y2flmsx5l3xd6o64rydhg87h
    unique (LABEL_TYPE)
    );

create table if not exists TB_HUB_CATEGORY_MAPPING
(
    HUB_CATEGORY_MAPPING_ID           bigint auto_increment
    primary key,
    hubCategoryEntity_HUB_CATEGORY_ID bigint null,
    hubEntity_HUB_ID                  bigint null
);

create index if not exists FK32vijspxobuoejq0g0my8w29b
    on TB_HUB_CATEGORY_MAPPING (hubEntity_HUB_ID);

create index if not exists FKc3hnkqbex16rpudygnf2xdk91
    on TB_HUB_CATEGORY_MAPPING (hubCategoryEntity_HUB_CATEGORY_ID);

create table if not exists TB_HUB_IMAGE
(
    IMAGE_ID bigint not null
    primary key
);

create table if not exists TB_IMAGE
(
    IMAGE_ID             bigint auto_increment
    primary key,
    MOD_DATE             datetime(6)                             null,
    REG_DATE             datetime(6)                             null,
    IMAGE_TYPE           varchar(31)                             not null,
    IMAGE_NAME           varchar(255)                            null,
    REG_USER_ID          varchar(255)                            null,
    REG_USER_NAME        varchar(255)                            null,
    REG_USER_REAL_NAME   varchar(255)                            null,
    DELETE_YN            enum ('Y', 'N')                         null,
    REPOSITORY_AUTH_TYPE enum ('PRIVATE', 'PUBLIC')              null,
    WORKLOAD_TYPE        enum ('BATCH', 'INTERACTIVE', 'DEPLOY') null
    );

create table if not exists TB_IMAGE_WORKLOAD_MAPPING
(
    IMAGE_ID                  bigint          null,
    IMAGE_WORKLOAD_MAPPING_ID bigint auto_increment
    primary key,
    WORKLOAD_ID               bigint          null,
    DELETE_YN                 enum ('Y', 'N') null,
    constraint UK_ruj2raagdamg8ku562cjuhaw3
    unique (WORKLOAD_ID)
    );

create index if not exists FK88rj8cwqq8bcxh7s83vxafsvf
    on TB_IMAGE_WORKLOAD_MAPPING (IMAGE_ID);

create table if not exists TB_JOB_PREDICTION
(
    idnew_table int auto_increment
    primary key
);

create table if not exists TB_JOB_PREDICTION_EPOCH_LOG
(
    JOB_PREDICTION_EPOCH_LOG_ID int auto_increment
    primary key,
    JOB_PREDICTION_PARAMETER_ID int         not null,
    MODEL_NAME                  varchar(45) null,
    MODEL_PARAM_NUM             int         null,
    GPU_TYPE                    varchar(45) null,
    GPU_FLOPS                   varchar(45) null,
    CLASS_NUM                   varchar(45) null,
    TRAIN_IMG_NUM               varchar(45) null,
    TRAIN_INSTANCE_NUM          varchar(45) null,
    VALID_IMG_NUM               varchar(45) null,
    VALID_INSTANCE_NUM          varchar(45) null,
    IMG_SIZE                    varchar(45) null,
    BATCH_SIZE                  varchar(45) null,
    EPOCH_CNT                   varchar(45) null,
    PREPROCESS_TIME             varchar(45) null,
    TRAIN_TIME                  varchar(45) null,
    VALID_TIME                  varchar(45) null,
    TIME_PER_EPOCH              varchar(45) null,
    SCHEDULER_TIME              varchar(45) null,
    EPOCH_TIME                  varchar(45) null,
    ELAPSED_TIME                varchar(45) null,
    REMAINING_TIME              varchar(45) null,
    GPU_USAGE                   varchar(45) null,
    CPU_USAGE                   varchar(45) null
    );

create table if not exists TB_JOB_PREDICTION_PARAMETER
(
    JOB_PREDICTION_PARAMETER_ID int auto_increment
    primary key,
    MODEL                       varchar(50)                           not null comment 'model name',
    MODEL_PT                    varchar(50)                           null comment 'pre-trained model path',
    DATA_DIR                    varchar(500)                          null comment 'yaml file',
    IMAGE_SIZE                  int                                   null comment 'input image scale',
    EPOCH                       int                                   null comment 'num epochs',
    BATCH_SIZE                  int                                   null comment 'number of images per batch',
    LEARNING_RATE               decimal(10, 2)                        null comment 'output checkpoint',
    SAVE_MODEL_DIR              varchar(500)                          null,
    PATIENCE                    int                                   null comment 'EarlyStopping patience',
    WORKER                      int                                   null comment 'number of worker threads for data loading',
    OPT                         varchar(50)                           null comment 'optimizer to use choices=[''SGD'', ''Adam'', ''AdamW'', ''RMSProp'']',
    SINGLE_CLS                  varchar(50)                           null comment 'train multi-class data as single-class',
    LABEL_SMOOTHING             decimal(10, 2)                        null comment 'Label smoothing epsilon',
    PRETRAINED                  varchar(5)                            null comment 'whether to use a pretrained model',
    INS_DTM                     timestamp default current_timestamp() null
    );

create table if not exists TB_LICENSE
(
    LICENSE_ID         bigint auto_increment
    primary key,
    LICENSE_END_DATE   date         null,
    LICENSE_GPU_COUNT  int          null,
    LICENSE_KEY        varchar(255) null,
    LICENSE_REG_DATE   datetime(6)  null,
    LICENSE_START_DATE date         null,
    LICENSE_VERSION    varchar(255) null
    );

create table if not exists TB_LOCAL_DATASET
(
    DATASET_ID      bigint                        not null
    primary key,
    DEPLOYMENT_NAME varchar(255)                  null,
    DNS             varchar(255)                  null,
    PVC_NAME        varchar(255)                  null,
    PV_NAME         varchar(255)                  null,
    STORAGE_IP      varchar(255)                  null,
    STORAGE_PATH    varchar(255)                  null,
    SVC_NAME        varchar(255)                  null,
    STORAGE_TYPE    enum ('NFS', 'CLOUD', 'PURE') null
    );

create table if not exists TB_LOCAL_MODEL
(
    MODEL_ID        bigint                        not null
    primary key,
    DEPLOYMENT_NAME varchar(255)                  null,
    DNS             varchar(255)                  null,
    PVC_NAME        varchar(255)                  null,
    PV_NAME         varchar(255)                  null,
    STORAGE_IP      varchar(255)                  null,
    STORAGE_PATH    varchar(255)                  null,
    SVC_NAME        varchar(255)                  null,
    STORAGE_TYPE    enum ('NFS', 'CLOUD', 'PURE') null
    );

create table if not exists TB_MAX_CUDA_VERSION
(
    MAX_CUDA_VERSION_ID bigint auto_increment
    primary key,
    MOD_DATE            datetime(6)  null,
    REG_DATE            datetime(6)  null,
    CUDA_VERSION        varchar(255) null,
    MAJOR_VERSION       varchar(255) null,
    MINOR_VERSION       varchar(255) null,
    REV                 varchar(255) null
    );

create table if not exists TB_MIN_CUDA_VERSION
(
    MIN_CUDA_VERSION_ID bigint auto_increment
    primary key,
    MOD_DATE            datetime(6)  null,
    REG_DATE            datetime(6)  null,
    CUDA_VERSION        varchar(255) null,
    MAX_VERSION         varchar(255) null,
    MIN_VERSION         varchar(255) null
    );

create table if not exists TB_MODEL
(
    MODEL_ID                 bigint auto_increment
    primary key,
    MODEL_SIZE               bigint                       null,
    MOD_DATE                 datetime(6)                  null,
    REG_DATE                 datetime(6)                  null,
    DIVISION                 varchar(31)                  not null,
    MODEL_NAME               varchar(255)                 null,
    REG_USER_ID              varchar(255)                 null,
    REG_USER_NAME            varchar(255)                 null,
    REG_USER_REAL_NAME       varchar(255)                 null,
    DELETE_YN                enum ('Y', 'N')              null,
    MODEL_DEFAULT_MOUNT_PATH varchar(255) charset utf8mb4 null
    );

create table if not exists TB_MODEL_WORKLOAD_MAPPING
(
    MODEL_ID                  bigint          null,
    MODEL_WORKLOAD_MAPPING_ID bigint auto_increment
    primary key,
    WORKLOAD_ID               bigint          null,
    MOUNT_PATH                varchar(255)    null,
    DELETE_YN                 enum ('Y', 'N') null
    );

create index if not exists FKbx5u6rm12hfi9gb735rheuec1
    on TB_MODEL_WORKLOAD_MAPPING (MODEL_ID);

create index if not exists FKjm2vqqcro72a8p4axiucwj6q2
    on TB_MODEL_WORKLOAD_MAPPING (WORKLOAD_ID);

create table if not exists TB_MODEL_WORKSPACE_MAPPING
(
    MODEL_ID                   bigint                       null,
    MODEL_WORKSPACE_MAPPING_ID bigint auto_increment
    primary key,
    MOD_DATE                   datetime(6)                  null,
    REG_DATE                   datetime(6)                  null,
    REG_USER_ID                varchar(255)                 null,
    REG_USER_NAME              varchar(255)                 null,
    REG_USER_REAL_NAME         varchar(255)                 null,
    WORKSPACE_RESOURCE_NAME    varchar(255)                 null,
    MODEL_DEFAULT_MOUNT_PATH   varchar(255) charset utf8mb4 null
    );

create index if not exists FK3e07wn6p2ciyp1vmk58vbc67t
    on TB_MODEL_WORKSPACE_MAPPING (MODEL_ID);

create table if not exists TB_PIN
(
    MOD_DATE           datetime(6)                    null,
    PIN_ID             bigint auto_increment
    primary key,
    REG_DATE           datetime(6)                    null,
    PIN_RESOURCE_NAME  varchar(255)                   null,
    REG_USER_ID        varchar(255)                   null,
    REG_USER_NAME      varchar(255)                   null,
    REG_USER_REAL_NAME varchar(255)                   null,
    PIN_TYPE           enum ('WORKLOAD', 'WORKSPACE') null
    );

create table if not exists TB_PORT
(
    PORT_NUM             int          null,
    PORT_TARTGET_NUM     int          null,
    PORT_ID              bigint auto_increment
    primary key,
    workload_WORKLOAD_ID bigint       null,
    PORT_NAME            varchar(255) null
    );

create index if not exists FKa6gnqfjb69gi5p5akpenxxb7v
    on TB_PORT (workload_WORKLOAD_ID);

create table if not exists TB_REPORT_RESERVATION
(
    enable             bit                                                                           not null,
    ID                 bigint auto_increment
    primary key,
    MOD_DATE           datetime(6)                                                                   null,
    REG_DATE           datetime(6)                                                                   null,
    endDate            datetime(6)                                                                   null,
    startDate          datetime(6)                                                                   null,
    REG_USER_ID        varchar(255)                                                                  null,
    isScheduler        bit                                                                           null,
    REG_USER_NAME      varchar(255)                                                                  null,
    REG_USER_REAL_NAME varchar(255)                                                                  null,
    explanation        varchar(255)                                                                  null,
    name               varchar(255)                                                                  null,
    sendCycle          bigint                                                                        null,
    reportType         enum ('WEEKLY_CLUSTER', 'MONTHLY_CLUSTER', 'WEEKLY_SYSTEM', 'MONTHLY_SYSTEM') null
    );

create table if not exists TB_REPORT_RESERVATION_HISTORY
(
    result       bit          null,
    ID           bigint auto_increment
    primary key,
    report_ID    bigint       null,
    transferDate datetime(6)  null,
    email        varchar(255) null,
    userName     varchar(255) null,
    lastName     varchar(255) null,
    firstName    varchar(255) null,
    constraint TB_REPORT_RESERVATION_HISTORY_TB_REPORT_RESERVATION_ID_fk
    foreign key (report_ID) references TB_REPORT_RESERVATION (ID)
    );

create table if not exists TB_REPORT_RESERVATION_USER
(
    REPORT_USER_ID  bigint auto_increment
    primary key,
    report_ID       bigint       null,
    USER_EMAIL      varchar(255) null,
    USER_FIRST_NAME varchar(255) null,
    USER_ID         varchar(255) null,
    USER_LAST_NAME  varchar(255) null,
    USER_NAME       varchar(255) null,
    constraint FKtl4res3uros0p50gadq654f8g
    foreign key (report_ID) references TB_REPORT_RESERVATION (ID)
    );

create table if not exists TB_RESOURCE_QUOTA
(
    RESOURCE_QUOTA_CPU_REQ        int                                   null,
    RESOURCE_QUOTA_GPU_REQ        int                                   null,
    RESOURCE_QUOTA_MEM_REQ        int                                   null,
    MOD_DATE                      datetime(6)                           null,
    REG_DATE                      datetime(6)                           null,
    RESOURCE_QUOTA_ID             bigint auto_increment
    primary key,
    REG_USER_ID                   varchar(255)                          null,
    REG_USER_NAME                 varchar(255)                          null,
    REG_USER_REAL_NAME            varchar(255)                          null,
    RESOURCE_QUOTA_REJECT_REASON  varchar(255)                          null,
    RESOURCE_QUOTA_REQUEST_REASON varchar(255)                          null,
    WORKSPACE_NAME                varchar(255)                          null,
    WORKSPACE_RESOURCE_NAME       varchar(255)                          null,
    RESOURCE_QUOTA_STATUS         enum ('WAITING', 'APPROVE', 'REJECT') null
    );

create table if not exists TB_RESOURCE_SCHEDULER
(
    cpu     int                                                             null,
    gpu     int                                                             null,
    hour    int                                                             null,
    mem     int                                                             null,
    running bit                                                             null,
    id      bigint auto_increment
    primary key,
    jobType enum ('BATCH_JOB_OPTIMIZATION', 'INTERACTIVE_JOB_OPTIMIZATION') null
    );

create table if not exists TB_STORAGE
(
    STORAGE_REQUEST_VOLUME  int                           null,
    MOD_DATE                datetime(6)                   null,
    REG_DATE                datetime(6)                   null,
    STORAGE_ID              bigint auto_increment
    primary key,
    ASTRAGO_DEPLOYMENT_NAME varchar(255)                  null,
    DESCRIPTION             varchar(255)                  null,
    HOST_PATH               varchar(255)                  null,
    NAME_SPACE              varchar(255)                  null,
    PVC_NAME                varchar(255)                  null,
    PV_NAME                 varchar(255)                  null,
    REG_USER_ID             varchar(255)                  null,
    REG_USER_NAME           varchar(255)                  null,
    REG_USER_REAL_NAME      varchar(255)                  null,
    STORAGE_IP              varchar(255)                  null,
    STORAGE_NAME            varchar(255)                  null,
    STORAGE_PATH            varchar(255)                  null,
    VOLUME_NAME             varchar(255)                  null,
    STORAGE_TYPE            enum ('NFS', 'CLOUD', 'PURE') null
    );

create table if not exists TB_SYSTEM_ALERT
(
    ALERT_ID           bigint auto_increment
    primary key,
    MOD_DATE           datetime(6)                                                                     null,
    REG_DATE           datetime(6)                                                                     null,
    MESSAGE            varchar(255)                                                                    null,
    RECIPIENT_ID       varchar(255)                                                                    null,
    REG_USER_ID        varchar(255)                                                                    null,
    REG_USER_NAME      varchar(255)                                                                    null,
    REG_USER_REAL_NAME varchar(255)                                                                    null,
    SENDER_ID          varchar(255)                                                                    null,
    TITLE              varchar(255)                                                                    null,
    ALERT_EVENT_TYPE   enum ('NOTIFICATION', 'ERROR', 'DELETE', 'USER')                                null,
    ALERT_TYPE         enum ('USER', 'WORKSPACE', 'WORKLOAD', 'LICENSE', 'NODE', 'RESOURCE', 'MEMBER') null,
    READ_YN            enum ('Y', 'N')                                                                 null,
    ALERT_ROLE         enum ('USER', 'ADMIN', 'OWNER')                                                 null,
    PAGE_NAVI_PARAM    varchar(1000)                                                                   null
    );

create table if not exists TB_SYSTEM_ALERT_SETTING
(
    LICENSE_EMAIL_YN             bit null,
    LICENSE_SYSTEM_YN            bit null,
    NODE_EMAIL_YN                bit null,
    NODE_SYSTEM_YN               bit null,
    RESOURCE_OVER_EMAIL_YN       bit null,
    RESOURCE_OVER_SYSTEM_YN      bit null,
    USER_EMAIL_YN                bit null,
    USER_SYSTEM_YN               bit null,
    WORKSPACE_PRODUCE_EMAIL_YN   bit null,
    WORKSPACE_PRODUCE_SYSTEM_YN  bit null,
    WORKSPACE_RESOURCE_EMAIL_YN  bit null,
    WORKSPACE_RESOURCE_SYSTEM_YN bit null,
    ID                           bigint auto_increment
    primary key
);

create table if not exists TB_VOLUME
(
    VOLUME_ID            bigint auto_increment
    primary key,
    workload_WORKLOAD_ID bigint       null,
    VOLUME_NAME          varchar(255) null
    );

create index if not exists FKklfdhstveisq6o9ddn3buybdv
    on TB_VOLUME (workload_WORKLOAD_ID);

create table if not exists TB_WORKLOAD
(
    WORKLOAD_ID                bigint auto_increment
    primary key,
    WORKLOAD_NAME              varchar(255)                            null,
    WORKLOAD_REQ_CPU           decimal(10, 1)                          null,
    WORKLOAD_REQ_GPU           int                                     null,
    WORKLOAD_REQ_MEM           decimal(10, 1)                          null,
    WORKLOAD_CREATED_AT        datetime(6)                             null,
    WORKLOAD_DELETED_AT        datetime(6)                             null,
    image_IMAGE_ID             bigint                                  null,
    DTYPE                      varchar(31)                             not null,
    WORKLOAD_CMD               varchar(255)                            null,
    WORKLOAD_CREATOR           varchar(255)                            null,
    WORKLOAD_CREATOR_ID        varchar(255)                            null,
    WORKLOAD_CREATOR_REAL_NAME varchar(255)                            null,
    WORKLOAD_DESCRIPTION       varchar(255)                            null,
    WORKING_DIR                varchar(255)                            null comment '명령어를 실행 할 위치',
    WORKLOAD_CMD_ARGS          varchar(1000)                           null,
    WORKLOAD_RESOURCE_NAME     varchar(255)                            null,
    WORKSPACE_NAME             varchar(255)                            null,
    WORKSPACE_RESOURCE_NAME    varchar(255)                            null,
    WORKLOAD_TYPE              enum ('BATCH', 'INTERACTIVE', 'DEPLOY') null,
    WORKLOAD_UID               varchar(255)                            null,
    DELETE_YN                  enum ('Y', 'N')                         null
    );

create index if not exists FK9pb3yd1r6r91gcs8crjhkhy8u
    on TB_WORKLOAD (image_IMAGE_ID);

create table if not exists TB_WORKLOAD_JOB
(
    WORKLOAD_ID  bigint       not null
    primary key,
    WORKLOAD_IDE varchar(255) null
    );

create table if not exists TB_WORKSPACE_ALERT_MAPPING
(
    ALERT_ID                   bigint             null,
    WORKSPACE_ALERT_MAPPING_ID bigint auto_increment
    primary key,
    USER_ID                    varchar(255)       null,
    WORKSPACE_RESOURCE_NAME    varchar(255)       null,
    EMAIL_ALERT_STATUS         enum ('ON', 'OFF') null,
    SYSTEM_ALERT_STATUS        enum ('ON', 'OFF') null
    );

create index if not exists FKencj9gal5dqg8uxgr67dbyp39
    on TB_WORKSPACE_ALERT_MAPPING (ALERT_ID);

create table if not exists TB_WORKSPACE_ALERT_SETTING
(
    RESOURCE_APPROVAL_ALERT bit          null,
    WORKLOAD_END_ALERT      bit          null,
    WORKLOAD_ERROR_ALERT    bit          null,
    WORKLOAD_START_ALERT    bit          null,
    ID                      bigint auto_increment
    primary key,
    WORKSPACE_NAME          varchar(255) null
    );

create table if not exists TB_WORKSPACE_SETTING
(
    cpu double not null,
    gpu int    not null,
    mem double not null,
    id  bigint auto_increment
    primary key
);



-- /**
--   초기 환경설정 값
--  */
-- INSERT
-- INTO TB_SYSTEM_ALERT_SETTING
-- (ID, LICENSE_SYSTEM_YN, LICENSE_EMAIL_YN, USER_SYSTEM_YN, USER_EMAIL_YN, NODE_SYSTEM_YN, NODE_EMAIL_YN,
--  WORKSPACE_PRODUCE_SYSTEM_YN, WORKSPACE_PRODUCE_EMAIL_YN, RESOURCE_OVER_SYSTEM_YN, RESOURCE_OVER_EMAIL_YN,
--  WORKSPACE_RESOURCE_SYSTEM_YN, WORKSPACE_RESOURCE_EMAIL_YN
-- )
-- VALUES
--     (1, false, false, false, false, false, false, false, false, false, false, false, false)
--     ON DUPLICATE
--         key update id =1;
INSERT INTO TB_WORKSPACE_SETTING (cpu, gpu, mem) values (2, 2, 2);
