// package com.xiilab.modulecommon.util;
//
// import static org.junit.jupiter.api.Assertions.*;
//
// import java.io.IOException;
// import java.net.URISyntaxException;
// import java.net.URL;
// import java.nio.file.Path;
// import java.util.ArrayList;
// import java.util.List;
//
// import org.apache.commons.compress.compressors.CompressorException;
// import org.junit.jupiter.api.Test;
//
// import com.xiilab.modulecommon.enums.CompressFileType;
// import com.xiilab.modulecommon.exception.ErrorCode;
// import com.xiilab.modulecommon.exception.RestApiException;
//
// class CompressUtilsTest {
//
// 	@Test
// 	void compressFolder() {
// 		ClassLoader classLoader = this.getClass().getClassLoader();
//
// 		String targetFolder1 = "zip/";
// 		String targetFolder2 = "zip2/";
// 		String targetFolder3 = "zip3/";
// 		String targetFolder4 = "zip4/";
// 		URL srcZip1 = classLoader.getResource(targetFolder1);
// 		URL srcZip2 = classLoader.getResource(targetFolder2);
// 		URL srcZip3 = classLoader.getResource(targetFolder3);
// 		URL srcZip4 = classLoader.getResource(targetFolder4);
//
// 		try {
// 			List<Path> pathList = new ArrayList<>();
// 			pathList.add(Path.of(srcZip1.toURI()));
// 			pathList.add(Path.of(srcZip2.toURI()));
// 			pathList.add(Path.of(srcZip3.toURI()));
// 			pathList.add(Path.of(srcZip4.toURI()));
//
// 			CompressUtils.compress(pathList, null, CompressFileType.TAR);
// 		} catch (Exception e) {
// 			if (e instanceof RestApiException) {
// 				System.out.println("errorCode.getMessage() = " + ((RestApiException)e).getErrorCode().getMessage());
// 			}
// 			e.printStackTrace();
// 		}
// 	}
// }
