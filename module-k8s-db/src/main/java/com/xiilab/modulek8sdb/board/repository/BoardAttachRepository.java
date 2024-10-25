package com.xiilab.modulek8sdb.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xiilab.modulek8sdb.board.entity.BoardAttachEntity;

public interface BoardAttachRepository extends JpaRepository<BoardAttachEntity, Long>, BoardRepositoryCustom{
}
