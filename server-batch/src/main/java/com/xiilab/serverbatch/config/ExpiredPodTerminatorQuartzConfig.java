package com.xiilab.serverbatch.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;
import com.xiilab.modulek8sdb.workload.history.repository.WorkloadHistoryRepoCustom;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 종료시간이 된 파드 & 디폴이리언먼트 들을 종료시키는 클래스
 */
@EnableScheduling
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ExpiredPodTerminatorQuartzConfig {
    
    private final WorkloadHistoryRepoCustom workloadHistoryRepoCustom;
    private final WorkloadModuleFacadeService workloadModuleFacadeService;
    /**
     * 5초마다 종료시간이 된 파드들을 종료시킨다
     */
    @Scheduled(cron = "*/5 * * * * ?") 
    @Transactional
    public void collectExpiredPods(){
        List<WorkloadEntity> list = workloadHistoryRepoCustom.getExpiredTimeWorkloadList();
        log.info("end time containers : {}" , list.size());
        list.stream().forEach(o -> {
            log.info("삭제 이벤트 발생\n워크스페이스: {}\n워크로드: {}\n삭제 시작" , o.getWorkspaceResourceName(), o.getResourceName());
            workloadModuleFacadeService.deleteInteractiveJobWorkload(
                o.getWorkspaceResourceName(),
                o.getResourceName()
            );
        });
    }
}
