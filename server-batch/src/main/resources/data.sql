INSERT INTO TB_RESOURCE_SCHEDULER (id, cpu, gpu, mem, hour, running, jobType)
values (1, 10, 10, 10, 24, false, 'BATCH_JOB_OPTIMIZATION') ON DUPLICATE key update id = 1;
INSERT INTO TB_RESOURCE_SCHEDULER (id, cpu, gpu, mem, hour, running, jobType)
values (2, 10, 10, 10, 24, false, 'INTERACTIVE_JOB_OPTIMIZATION') ON DUPLICATE key update id = 2;

