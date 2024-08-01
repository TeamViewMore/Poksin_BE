package com.viewmore.poksin.controller;

import com.viewmore.poksin.dto.response.ResponseDTO;
import com.viewmore.poksin.dto.user.UserResponseDTO;
import com.viewmore.poksin.entity.ChatMessageEntity;
import com.viewmore.poksin.entity.ChatRoomEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "채팅 API", description = "채팅과 관련된 모든 API")
public interface ChatAPI {
    @Operation(summary = "[api 대상] 설명 써주세요")
    public ResponseEntity<ResponseDTO<ChatRoomEntity>> createRoom();

    @Operation(summary = "[api 대상] 설명 써주세요")
    public ResponseEntity<ResponseDTO<List<UserResponseDTO>>> findAllUsers();

    @Operation(summary = "[api 대상] 설명 써주세요")
    public ChatMessageEntity sendMessage(ChatMessageEntity chatMessage);

    @Operation(summary = "[api 대상] 설명 써주세요")
    public ResponseEntity<ResponseDTO<List<ChatRoomEntity>>> findAllChatRooms();

    @Operation(summary = "[api 대상] 설명 써주세요")
    public ResponseEntity<?> getMessagesByRoomId(@PathVariable String roomId);

    @Operation(summary = "[api 대상] 설명 써주세요")
    public ResponseEntity<String> closeRoom(@PathVariable String roomId);

    @Operation(summary = "[api 대상] 설명 써주세요")
    public ResponseEntity<String> openRoom(@PathVariable String roomId);

    @Operation(summary = "[api 대상] 설명 써주세요")
    public ResponseEntity<ChatMessageEntity> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("roomId") String roomId);
}
