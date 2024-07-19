package com.xiilab.modulek8sdb.image.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.xiilab.modulek8sdb.common.fixtures.ImageFixtures;
import com.xiilab.modulek8sdb.config.TestConfig;
import com.xiilab.modulek8sdb.image.entity.BuiltInImageEntity;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;

@DataJpaTest
@Import(TestConfig.class)
class ImageRepositoryTest {
	@Autowired
	ImageRepository imageRepository;

	@Test
	@DisplayName("이미지 저장 테스트")
	public void saveImage() {
		BuiltInImageEntity builtInImageEntity = ImageFixtures.FIRST_BUILT_IN_IMAGE();
		imageRepository.save(builtInImageEntity);

		List<ImageEntity> imageEntities = imageRepository.findAll();

		assertThat(imageEntities.size()).isEqualTo(1);
		assertThat(imageEntities.get(0).getId()).isEqualTo(1);
		assertThat(imageEntities.get(0).getImageName()).isEqualTo("test:v1");
		assertThat(((BuiltInImageEntity)imageEntities.get(0)).getTitle()).isEqualTo("nginx 이미지");
		assertThat(((BuiltInImageEntity)imageEntities.get(0)).getThumbnailSavePath()).isEqualTo("/usr/local/[uuid].png");
		assertThat(((BuiltInImageEntity)imageEntities.get(0)).getThumbnailSaveFileName()).isEqualTo("test.png");
	}
}
