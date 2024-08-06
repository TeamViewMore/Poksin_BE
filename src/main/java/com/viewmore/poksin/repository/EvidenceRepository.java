package com.viewmore.poksin.repository;

import com.viewmore.poksin.entity.CategoryEntity;
import com.viewmore.poksin.entity.CategoryTypeEnum;
import com.viewmore.poksin.entity.EvidenceEntity;
import com.viewmore.poksin.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EvidenceRepository extends JpaRepository<EvidenceEntity, Integer> {
    List<EvidenceEntity> findAllByUserAndCategory(UserEntity user, CategoryEntity category);
    Optional<EvidenceEntity> findById(Integer id);
    @Query("SELECT e FROM EvidenceEntity e WHERE e.user = :user AND YEAR(e.evidencdCreatedAt) = :year AND MONTH(e.evidencdCreatedAt) = :month")
    List<EvidenceEntity> findByUserAndYearAndMonth(@Param("user") UserEntity user, @Param("year") int year, @Param("month") int month);
    @Query("SELECT e FROM EvidenceEntity e WHERE e.user = :user AND YEAR(e.evidencdCreatedAt) = :year AND MONTH(e.evidencdCreatedAt) = :month AND DAY(e.evidencdCreatedAt) = :day AND e.category = :category")
    List<EvidenceEntity> findByUserAndYearAndMonthAndDay(@Param("user") UserEntity user, @Param("year") int year, @Param("month") int month, @Param("day") int day, @Param("category") CategoryEntity category);

}