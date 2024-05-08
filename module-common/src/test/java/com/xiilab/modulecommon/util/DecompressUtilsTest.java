// package com.xiilab.modulecommon.util;
//
// import static org.junit.jupiter.api.Assertions.*;
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
// class DecompressUtilsTest {
// 	@Test
// 	void decompressZip() {
// 		ClassLoader classLoader = this.getClass().getClassLoader();
// 		String target = "coco2.zip";
// 		URL srcZip = classLoader.getResource(target);
//
// 		try {
// 			long startTime = System.currentTimeMillis();
// 			DecompressUtils.saveDecompressFile(Path.of(srcZip.toURI()), null);
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
//
// }
