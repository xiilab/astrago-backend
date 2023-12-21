# K8S Resource 생성 규칙
## Workspace(Namespace)
- meta name
    - ws-UUID
- annotation
    - 이름 : name
    - 설명 : description
    - 생성자 username : creator
    - 생성시각 : created-at
    - 생성자 이름(실명) : creator-name
- label
    - 생성자 username : creator
## Workload(Job, Service, Deployment, Statefulset)
- meta name
    - wl-UUID
- annotation
    - 이름 : name
    - 설명 : description
    - 생성자 username : creator
    - 생성시각 : created-at
    - 생성자 이름(실명) : creator-name
- label
    - 생성자 username : creator
    - 사용한 볼륨은 label에 list형을 못넣기때문에 row로 나열(볼륨의 meta name : true)
    - vo-uuid1 : "true"
    - vo-uuid2 : "true"
    - 사용한 이미지 : image
    - 사용한 codes : codes
## Volume(PV,PVC)
- meta name
    - vo-UUID
- annotation
    - 이름 : name
    - 생성자 username : creator
    - 생성시각 : created-at
    - 생성자 이름(실명) : creator-name
- label
    - 생성자 username : creator
    - storage type : storage-type
    - 스토리지 실제 이름 : storage-name
## Storage(Storage Class)
- meta name
    - st-UUID
- annotation
    - 이름 : name
    - 설명 : description
    - 생성자 username : creator
    - 생성시각 : created-at
    - 생성자 이름(실명) : creator-name
- label
    - 생성자 username : creator
    - 스토리지 타입 : storage-type
## Plugin(CSI)
- meta name
    - pg-UUID
- annotation
    - 이름 : name
    - 설명 : description
    - 생성자 username : creator
    - 생성시각 : created-at
    - 생성자 이름(실명) : creator-name
- label
    - 생성자 username : creator

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
