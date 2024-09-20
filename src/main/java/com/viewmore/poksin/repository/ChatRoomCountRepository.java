package com.viewmore.poksin.repository;

import com.viewmore.poksin.entity.ChatRoomCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomCountRepository extends JpaRepository<ChatRoomCountEntity, Long> {
}
