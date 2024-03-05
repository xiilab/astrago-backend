# Informer
- Batch서버에서는 Informer를 통해 종료된 workload에 대한 history를 저장
- 종료된 log를 file로 저장
- 추후 해당 informer를 통해 기능을 추가

# Resource Optimization Job
- Quartz로 동작
- 자원최적화를 수행
- API 서버 형태로 동작하여 사용자가 직접 Job을 추가 수정 삭제 조회 할 수 있도록 구현함
