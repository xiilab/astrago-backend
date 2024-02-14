// package com.xiilab.servercore.image.Repository;
//
// import java.util.Optional;
//
// import org.assertj.core.api.Assertions;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
// import com.xiilab.modulek8sdb.enums.ImageType;
// import com.xiilab.servercore.image.entity.BuiltInImageEntity;
// import com.xiilab.servercore.image.repository.BuiltInImageRepository;
//
// @DataJpaTest
// public class BuiltInImageRepositoryTest {
// 	@Autowired
// 	BuiltInImageRepository builtInImageRepository;
//
// 	@Test
// 	@DisplayName("이미지 저장 테스트")
// 	public void saveImage() {
// 		BuiltInImageEntity build = BuiltInImageEntity.builder()
// 			.id(1L)
// 			.title("eqweqweqw")
// 			.description("qweeqweqw")
// 			.imageType(ImageType.BUILT)
// 			.imageName("1ewe1")
// 			.thumbnailSaveFileName("qweeqweqw")
// 			.thumbnailSavePath("eqweqweqw")
// 			.build();
//
// 		builtInImageRepository.save(build);
// 		BuiltInImageEntity image = builtInImageRepository.findById(build.getId()).get();
// 		Assertions.assertThat(image.getTitle()).isEqualTo("eqweqweqw");
// 	}
// }
