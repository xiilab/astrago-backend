package com.xiilab.modulek8s.facade.workload;

//@SpringBootTest
class WorkloadModuleFacadeServiceImplTest {

	// @Autowired
	// WorkloadService workloadService;
	// @Autowired
	// VolumeService volumeService;
	// @Autowired
	// StorageClassService storageClassService;
	// @Autowired
	// SvcService svcService;
	// @Autowired
	// WorkloadModuleFacadeServiceImpl workloadModuleFacadeService;
	//
	// @Test
	// @DisplayName("워크로드 - 배치잡 생성")
	// void createBatchJobWorkload() {
	// 	List<EnvReqDTO> envList = new ArrayList<>();
	// 	envList.add(new EnvReqDTO("TEST", "TEST"));
	//
	// 	List<PortReqDTO> portList = new ArrayList<>();
	// 	portList.add(new PortReqDTO("port1", 8080));
	// 	portList.add(new PortReqDTO("port3", 8089));
	//
	// 	List<VolumeReqDTO> envReqDtoList = new ArrayList<>();
	// 	envReqDtoList.add(
	// 		new VolumeReqDTO(StorageType.NFS, VolumeSelectionType.NEW, "volume1", null, 5, "/usr/local/etc", "nfs-csi")
	// 	);
	// 	envReqDtoList.add(
	// 		new VolumeReqDTO(StorageType.NFS, VolumeSelectionType.NEW, "volume2", null, 5, "/usr/local/src/code", "nfs-csi")
	// 	);
	//
	// 	List<CodeReqDTO> codeReqs = new ArrayList<>();
	// 	codeReqs.add(new CodeReqDTO(CodeRepositoryType.PUBLIC, "https://github.com/mfvanek/spring5-mvc-opentracing.git",
	// 		"master", "/mnt"));
	//
	// 	CreateWorkloadReqDTO createWorkloadReqDTO = CreateWorkloadReqDTO.builder()
	// 		.varName("사용자 지정 이름")
	// 		.description("Hello world!")
	// 		.workloadType(WorkloadType.BATCH)
	// 		.creatorName("SJO")
	// 		.workspace("yc-test-ns")
	// 		.creator("test1234")
	// 		.image("ubuntu")
	// 		.ports(portList)
	// 		.envs(envList)
	// 		.command("while true; do echo hello; sleep 10;done")
	// 		.volumes(envReqDtoList)
	// 		.cpuRequest(0.5123312132f)
	// 		.gpuRequest(1)
	// 		.memRequest(0.5123312132132f)
	// 		.codes(codeReqs)
	// 		.build();
	//
	// 	workloadModuleFacadeService.createBatchJobWorkload(createWorkloadReqDTO);
	// 	System.out.println("createWorkloadReqDTO = " + createWorkloadReqDTO);
	// }
	//
	// @Test
	// @DisplayName("워크로드 - 인터렉티브 잡 생성")
	// void createInteractiveJobWorkload() {
	// 	List<EnvReqDTO> envList = new ArrayList<>();
	// 	envList.add(new EnvReqDTO("TEST", "TEST"));
	//
	// 	List<PortReqDTO> portList = new ArrayList<>();
	// 	portList.add(new PortReqDTO("port1", 8080));
	// 	portList.add(new PortReqDTO("port3", 8089));
	//
	// 	List<VolumeReqDTO> envReqDtoList = new ArrayList<>();
	// 	envReqDtoList.add(
	// 		new VolumeReqDTO(StorageType.NFS, VolumeSelectionType.NEW, "volume1", null, 5, "/usr/local/etc", "nfs-csi")
	// 	);
	// 	envReqDtoList.add(
	// 		new VolumeReqDTO(StorageType.NFS, VolumeSelectionType.NEW, "volume2", null, 5, "/usr/local/src/code", "nfs-csi")
	// 	);
	//
	// 	List<CodeReqDTO> codeReqs = new ArrayList<>();
	// 	codeReqs.add(new CodeReqDTO(CodeRepositoryType.PUBLIC, "https://github.com/mfvanek/spring5-mvc-opentracing.git",
	// 		"master", "/mnt"));
	//
	// 	CreateWorkloadReqDTO createWorkloadReqDTO = CreateWorkloadReqDTO.builder()
	// 		.varName("사용자 지정 이름")
	// 		.description("Hello world!")
	// 		.workloadType(WorkloadType.INTERACTIVE)
	// 		.creatorName("SJO")
	// 		.workspace("yc-test-ns")
	// 		.creator("test1234")
	// 		.image("ubuntu")
	// 		.ports(portList)
	// 		.envs(envList)
	// 		.command("while true; do echo hello; sleep 10;done")
	// 		.volumes(envReqDtoList)
	// 		.cpuRequest(0.5123312132f)
	// 		.gpuRequest(1)
	// 		.memRequest(0.5123312132132f)
	// 		.codes(codeReqs)
	// 		.build();
	//
	// 	workloadModuleFacadeService.createInteractiveJobWorkload(createWorkloadReqDTO);
	// 	System.out.println("createWorkloadReqDTO = " + createWorkloadReqDTO);
	// }
}
