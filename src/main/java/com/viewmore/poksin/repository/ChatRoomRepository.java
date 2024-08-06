package com.viewmore.poksin.repository;

import com.viewmore.poksin.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
    Optional<ChatRoomEntity> findByRoomId(String roomId);
    Optional<ChatRoomEntity> findByName(String name);
}
