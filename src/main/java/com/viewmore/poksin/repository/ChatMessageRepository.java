package com.viewmore.poksin.repository;

import com.viewmore.poksin.entity.ChatMessageEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findByRoomId(String roomId);

    @Query("SELECT c FROM ChatMessageEntity c WHERE c.sender = :username ORDER BY c.timestamp DESC")
    List<ChatMessageEntity> findTopBySenderOrderByTimestampDesc(@Param("username") String username);

    List<ChatMessageEntity> findBySender(String username);

    default Optional<ChatMessageEntity> findLatestBySender(String username) {
        List<ChatMessageEntity> results = findTopBySenderOrderByTimestampDesc(username);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    List<ChatMessageEntity> findBySenderAndTimestampBetween(String sender, LocalDateTime start, LocalDateTime end);

}
