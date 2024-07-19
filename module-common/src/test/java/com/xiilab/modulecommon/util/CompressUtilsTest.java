// package com.xiilab.modulecommon.util;
//
// import java.net.URL;
// import java.nio.file.Path;
// import java.util.ArrayList;
// import java.util.List;
//
// import org.junit.jupiter.api.Test;
//
// import com.xiilab.modulecommon.enums.CompressFileType;
// import com.xiilab.modulecommon.exception.RestApiException;
//
// class CompressUtilsTest {
//
// 	@Test
// 	void compressFolder() {
// 		ClassLoader classLoader = this.getClass().getClassLoader();
//
// 		String targetFolder1 = "zip";
// 		// String targetFolder2 = "zip2/";
// 		// String targetFolder3 = "zip3/";
// 		// String targetFolder4 = "zip4/";
// 		URL srcZip1 = classLoader.getResource(targetFolder1);
// 		// URL srcZip2 = classLoader.getResource(targetFolder2);
// 		// URL srcZip3 = classLoader.getResource(targetFolder3);
// 		// URL srcZip4 = classLoader.getResource(targetFolder4);
//
// 		try {
// 			List<Path> pathList = new ArrayList<>();
// 			pathList.add(Path.of(srcZip1.toURI()));
// 			// pathList.add(Path.of(srcZip2.toURI()));
// 			// pathList.add(Path.of(srcZip3.toURI()));
// 			// pathList.add(Path.of(srcZip4.toURI()));
//
// 			long startTime = System.currentTimeMillis();
// 			CompressUtils.saveCompressFile(pathList, null, CompressFileType.ZIP);
// 			long endTime = System.currentTimeMillis();
// 			long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
//
// 			System.out.println("---------------버퍼 스트림 전송시간--------------------");
// 			System.out.println("메모리 사용량: " + usedMemory + " bytes");
// 			System.out.println("복사 시간: " + (endTime - startTime) / 1000 + " 초");
// 		} catch (Exception e) {
// 			if (e instanceof RestApiException) {
// 				System.out.println("errorCode.getMessage() = " + ((RestApiException)e).getErrorCode().getMessage());
// 			}
// 			e.printStackTrace();
// 		}
// 	}
// }
