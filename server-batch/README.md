# Informer
- Batch서버에서는 Informer를 통해 종료된 workload에 대한 history를 저장
- 종료된 log를 file로 저장
- 추후 해당 informer를 통해 기능을 추가

# Resource Optimization Job
- Quartz로 동작
  - Job(Interactive, Batch Resource Optimization)
    - 실제 자원최적화 로직을 수행
    - 추후 다음 클래스를 수정
      - [BatchResourceOptimizationJob.java](src%2Fmain%2Fjava%2Fcom%2Fxiilab%2Fserverbatch%2Fjob%2FBatchResourceOptimizationJob.java)
      - [InteractiveResourceOptimizationJob.java](src%2Fmain%2Fjava%2Fcom%2Fxiilab%2Fserverbatch%2Fjob%2FInteractiveResourceOptimizationJob.java)
  - trigger
    - Job을 어떤 조건으로 수행 할지 조건을 추가
    - 현재 1시간 마다 수행되도록 cron expression으로 작성되어 있으며 추후 수정시 다음 클래스의 메소드를 수정할 것 
    - [ResourceSchedulerService.java](src%2Fmain%2Fjava%2Fcom%2Fxiilab%2Fserverbatch%2FschedulerService%2FResourceSchedulerService.java) - createTrigger를 수정할 것
- 자원최적화를 수행
- API 서버 형태로 동작하여 사용자가 직접 Job을 추가 수정 삭제 조회 할 수 있도록 구현함
