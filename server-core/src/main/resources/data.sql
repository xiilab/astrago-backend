/**
  초기 환경설정 값
 */
INSERT
INTO TB_SYSTEM_ALERT_SETTING
(id, LICENSE_SYSTEM_YN, LICENSE_EMAIL_YN, USER_SYSTEM_YN, USER_EMAIL_YN, NODE_SYSTEM_YN, NODE_EMAIL_YN,
 WORKSPACE_PRODUCE_SYSTEM_YN, WORKSPACE_PRODUCE_EMAIL_YN, RESOURCE_OVER_SYSTEM_YN, RESOURCE_OVER_EMAIL_YN,
 WORKSPACE_RESOURCE_SYSTEM_YN, WORKSPACE_RESOURCE_EMAIL_YN
)
VALUES
    (1, false, false, false, false, false, false, false, false, false, false, false, false)
    ON DUPLICATE
        key update id =1;