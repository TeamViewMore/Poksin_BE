package com.viewmore.poksin.repository;

import com.viewmore.poksin.entity.RefreshEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshRepository extends CrudRepository<RefreshEntity, String> {
}