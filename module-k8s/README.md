# K8S Resource 생성 규칙

## Workspace(Namespace)

- meta name
    - ws-UUID
- annotation
    - 이름 : name
    - 설명 : description
    - 생성시각 : created-at
    - 생성자 username : creator-user-name
    - 생성자 이름(실명) : creator-full-name
- label
    - 생성자 id : creator-id

## Workload(Job, Service, Deployment, Statefulset)

- meta name
    - wl-UUID
- annotation
    - 이름 : name
    - 설명 : description
    - 워크스페이스 리소스명: ws-name
    - 생성시각 : created-at
    - 생성자 username : creator-user-name
    - 생성자 이름(실명) : creator-full-name
    - 잡 타입: type(batch, interactive)
    - 이미지명 : image-name
    - 이미지 태그명: image-tag
    - 데이터셋 IDS(= dataset-ids): 1,2,3
    - 모델 IDS(=model-ids): 1,2,3
- label
    - 생성자 id : creator-id
    - 사용한 볼륨은 label에 list형을 못넣기때문에 row로 나열(볼륨의 meta name : true)
    - vo-uuid1 : "true"
    - vo-uuid2 : "true"
    - 사용한 이미지 : image
    - 사용한 codes : codes
    - 생성 플랫폼: control-by (astra)
    - 잡 이름(pod에만 존재): job-name
    - ds-[DATASET_ID[DB PK]]: "true"
    - md-[MDOEL_ID[DB PK]]: "true"

## Volume(PV,PVC)

- meta name
    - vo-UUID
- annotation
    - 이름 : name
    - 생성자 username : creator-user-name
    - 생성자 이름(실명) : creator-full-name
    - 생성시각 : created-at
    - 생성자 이름(실명) : creator-user-name
- label
    - 생성자 id : creator-id
    - storage type : storage-type
    - 스토리지 실제 이름 : storage-name

## Storage(Storage Class)

- meta name
    - st-UUID
- annotation
    - 이름 : name
    - 설명 : description
    - 생성자 username : creator-user-name
    - 생성자 이름(실명) : creator-full-name
    - 생성시각 : created-at
- label
    - 생성자 id : creator-id
    - 스토리지 타입 : storage-type

## Plugin(CSI)

- meta name
    - pg-UUID
- annotation
    - 이름 : name
    - 설명 : description
    - 생성자 username : creator-user-name
    - 생성자 이름(실명) : creator-full-name
    - 생성시각 : created-at
    - 생성자 이름(실명) : creator-user-name
- label
    - 생성자 id : creator-id

# Keycloak <-> Backend flow

## 토큰 규칙

- token에 group depth를 통하여 ws role을 조회
    - 최상위 depth : user, ws
        - /user/service2 -> 사용자 그룹
        - /ws/Anamespace/owner -> ws 그룹
        - /ws/Anamespace/user -> ws 그룹
    - mapper를 통하여 token에 추가
- token key
    - userFullName(firstName + lastName)
    - groups(사용자 그룹 + ws 그룹)
    - systemRole(Admin,User)
