package com.viewmore.poksin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.viewmore.poksin.service.ChatService;
import com.viewmore.poksin.service.SessionManager;
import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomEntity {
    private static final Logger logger = LoggerFactory.getLogger(ChatRoomEntity.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomId;
    private String name;

    @JsonIgnore
    @Builder.Default
    private Set<String> sessionIds = new HashSet<>();

    @Setter
    private String lastMessage; // 가장 최근에 보낸 메시지

    @Setter
    @Builder.Default
    private LocalDateTime lastUpdated = LocalDateTime.now(); // 가장 최근 메시지를 보낸 시간

    private boolean isBlocked; // 방 차단 상태

    @Setter
    private boolean isConsultationActive; // 상담 활성화 상태

    @Setter
    private String admin; // 채팅방 관리자

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public <T> void sendMessage(T message, ChatService chatService) {
        sessionIds.forEach(sessionId -> {
            WebSocketSession session = SessionManager.getSession(sessionId);
            if (session != null && session.isOpen()) {
                try {
                    chatService.sendMessage(session, message);
                } catch (Exception e) {
                    logger.error("메시지 전송 중 오류 발생: {}", e.getMessage(), e);
                }
            }
        });
    }
}
