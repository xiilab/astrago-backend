package com.xiilab.modulek8sdb.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xiilab.modulek8sdb.board.entity.BoardEntity;

public interface BoardRepository extends JpaRepository<BoardEntity, Long>, BoardRepositoryCustom{
}
