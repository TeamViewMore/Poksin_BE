package com.viewmore.poksin.controller;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import com.viewmore.poksin.entity.ChatMessageEntity;
import com.viewmore.poksin.entity.ChatRoomEntity;
import com.viewmore.poksin.dto.user.UserResponseDTO;
import com.viewmore.poksin.dto.response.ResponseDTO;
import com.viewmore.poksin.service.ChatService;
import com.viewmore.poksin.service.UserService;
import com.viewmore.poksin.code.SuccessCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import com.viewmore.poksin.service.S3Uploader;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController implements ChatAPI{

    private final ChatService chatService;
    private final UserService userService;

    @Autowired
    private S3Uploader s3Uploader;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    // 채팅방 만들기
    @PostMapping
    public ResponseEntity<ResponseDTO<ChatRoomEntity>> createRoom() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        ChatRoomEntity existingRoom = chatService.findRoomByUserName(username);
        if (existingRoom != null) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDTO<>(SuccessCode.SUCCESS_EXIST_CHATROOM, existingRoom));
        }

        ChatRoomEntity newRoom = chatService.createRoom(username);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_CREATE_CHATROOM, newRoom));
    }

    // 모든 유저 조회
    @GetMapping("/users")
    public ResponseEntity<ResponseDTO<List<UserResponseDTO>>> findAllUsers() {
        List<UserResponseDTO> users = userService.findAllUsers();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_ALL_USERS, users));
    }

    // 메시지 보내기
    @MessageMapping("/chat.sendMessage")
    public ChatMessageEntity sendMessage(ChatMessageEntity chatMessage) {
        log.info("Received message to send: {}", chatMessage);

        ChatRoomEntity chatRoom = chatService.findRoomById(chatMessage.getRoomId());
        if (chatRoom != null) {
            if (chatRoom.isBlocked()) {
                throw new IllegalArgumentException("This room is blocked.");
            }
            chatRoom.sendMessage(chatMessage, chatService);

            chatMessage.setTimestamp(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime());
            chatService.saveChatMessage(chatMessage);

            messagingTemplate.convertAndSend("/topic/" + chatMessage.getRoomId(), chatMessage);
        }
        return chatMessage;
    }

    // 모든 채팅방 조회
    @GetMapping("/rooms")
    public ResponseEntity<ResponseDTO<List<ChatRoomEntity>>> findAllChatRooms() {
        List<ChatRoomEntity> rooms = chatService.findAllRoom();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_FIND_CHATROOM, rooms));
    }

    // 채팅 내역 조회
    @GetMapping("/rooms/messages/{roomId}")
    public ResponseEntity<?> getMessagesByRoomId(@PathVariable String roomId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        ChatRoomEntity room = chatService.findRoomById(roomId);
        if (room == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "채팅방을 찾을 수 없습니다."));
        }

        // 로그인된 사용자의 username = 채팅방 name => 채팅방 blocked 상태여도 메시지 조회 허용
        if (room.isBlocked() && !room.getName().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "채팅방 입장이 차단되었습니다."));
        }

        List<ChatMessageEntity> messages = chatService.findMessagesByRoomId(roomId);
        if (messages.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of("error", "채팅방에 메시지가 없습니다."));
        }

        return ResponseEntity.ok(messages);
    }

    // 방 종료 (상담 종료 + 방 차단)
    @PostMapping("/rooms/{roomId}/close")
    public ResponseEntity<String> closeRoom(@PathVariable String roomId) {
        ChatRoomEntity room = chatService.findRoomById(roomId);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        chatService.closeRoom(roomId);
        return ResponseEntity.ok("상담이 종료되고, 채팅방이 차단 상태가 되었습니다.");
    }

    // 방 열기 (상담 시작 + 방 활성화)
    @PostMapping("/rooms/{roomId}/open")
    public ResponseEntity<String> openRoom(@PathVariable String roomId) {
        ChatRoomEntity room = chatService.findRoomById(roomId);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        chatService.openRoom(roomId);
        return ResponseEntity.ok("상담이 재개되고, 채팅방이 활성화 상태가 되었습니다.");
    }

    //파일 업로드
    @PostMapping("/upload")
    public ResponseEntity<ChatMessageEntity> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("roomId") String roomId) {
        try {
            // S3에 파일 업로드
            String fileUrl = s3Uploader.upload(file, "chat-uploads"); // S3의 디렉토리 이름

            // ChatMessageEntity를 생성하여 반환
            ChatMessageEntity chatMessage = chatService.uploadFile(file.getBytes(), file.getOriginalFilename(), roomId); // byte[]를 전달
            log.info("File uploaded successfully: {}", file.getOriginalFilename());

            return ResponseEntity.ok(chatMessage);
        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
