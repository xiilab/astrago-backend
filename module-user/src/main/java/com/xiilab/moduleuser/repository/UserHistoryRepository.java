package com.xiilab.moduleuser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.moduleuser.entity.UserHistoryEntity;

@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistoryEntity, String> {
}
