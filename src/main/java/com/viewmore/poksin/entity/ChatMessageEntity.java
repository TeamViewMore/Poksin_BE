package com.viewmore.poksin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ChatMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MessageType type;
    private String roomId;
    private String sender;
    private String message;
    private LocalDateTime timestamp;

    private String fileUrl;
    private String fileName;

    public enum MessageType {
        ENTER, TALK, PRIVATE, FILE
    }
}