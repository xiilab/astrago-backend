package com.xiilab.modulek8sdb.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.image.entity.ImageEntity;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long>, ImageRepositoryCustom{
	@Query("""
	select ie
	from ImageEntity ie 
	where ie.deleteYN = 'N'
	and ie.imageName = :imageName
	and ie.imageType = 'BUILT'
""")
	ImageEntity findBuiltImageByName(@Param("imageName") String imageName);
}
