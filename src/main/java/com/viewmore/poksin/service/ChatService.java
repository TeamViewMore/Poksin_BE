package com.viewmore.poksin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viewmore.poksin.entity.ChatMessageEntity;
import com.viewmore.poksin.entity.ChatRoomEntity;
import com.viewmore.poksin.repository.ChatMessageRepository;
import com.viewmore.poksin.repository.ChatRoomRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ObjectMapper objectMapper;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public List<ChatRoomEntity> findAllRoom() {
        return chatRoomRepository.findAll();
    }

    public ChatRoomEntity findRoomById(String roomId) {
        return chatRoomRepository.findByRoomId(roomId).orElse(null);
    }

    public ChatRoomEntity findRoomByUserName(String userName) {
        return chatRoomRepository.findByName(userName).orElse(null);
    }

    public ChatRoomEntity createRoom(String name) {
        String randomId = UUID.randomUUID().toString();
        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .roomId(randomId)
                .name(name)
                .build();
        return chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public ChatMessageEntity saveChatMessage(ChatMessageEntity message) {
        return chatMessageRepository.save(message);
    }

    public <T> void sendMessage(WebSocketSession session, T message) {
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
            } catch (IOException e) {
                log.error("Error sending message: {}", e.getMessage(), e);
            }
        } else {
            log.warn("Session is null or closed, cannot send message.");
        }
    }

    public List<ChatMessageEntity> findMessagesByRoomId(String roomId) {
        log.info("Finding messages for roomId: {}", roomId);
        return chatMessageRepository.findByRoomId(roomId);
    }

    // 상담 종료 + 차단
    public void closeRoom(String roomId) {
        ChatRoomEntity room = findRoomById(roomId);
        if (room != null) {
            room.setConsultationActive(false);
            room.setBlocked(true);
            chatRoomRepository.save(room);
        }
    }

    // 상담 재개 + 활성화
    public void openRoom(String roomId) {
        ChatRoomEntity room = findRoomById(roomId);
        if (room != null) {
            room.setConsultationActive(true);
            room.setBlocked(false);
            chatRoomRepository.save(room);
        }
    }

    // 파일 업로드
    @Transactional
    public ChatMessageEntity uploadFile(byte[] fileData, String fileName, String roomId, String username) {
        String uniqueFileName = UUID.randomUUID().toString() + "-" + fileName;
        try {
            s3Client.putObject(new PutObjectRequest(bucketName, uniqueFileName, new ByteArrayInputStream(fileData), null));
            String fileUrl = s3Client.getUrl(bucketName, uniqueFileName).toString();

            ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                    .fileUrl(fileUrl)
                    .fileName(fileName)
                    .roomId(roomId)
                    .sender(username)
                    .type(ChatMessageEntity.MessageType.FILE)
                    .timestamp(LocalDateTime.now())
                    .build();

            return saveChatMessage(chatMessage);
        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }
}
