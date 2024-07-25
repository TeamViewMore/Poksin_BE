package com.viewmore.poksin.repository;

import com.viewmore.poksin.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Boolean existsByUsername(String username);
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByKakaoUserId(Long kakaoUserId);
    Boolean existsByKakaoUserId(Long KakaoUserId);
}
