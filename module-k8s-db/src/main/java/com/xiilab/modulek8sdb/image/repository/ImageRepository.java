package com.xiilab.modulek8sdb.image.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulek8sdb.dataset.entity.Dataset;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long>, ImageRepositoryCustom{
}
