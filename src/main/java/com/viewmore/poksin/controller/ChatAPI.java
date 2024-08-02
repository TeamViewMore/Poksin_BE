package com.viewmore.poksin.controller;

import com.viewmore.poksin.dto.response.ErrorResponseDTO;
import com.viewmore.poksin.dto.response.ResponseDTO;
import com.viewmore.poksin.dto.user.UserResponseDTO;
import com.viewmore.poksin.entity.ChatMessageEntity;
import com.viewmore.poksin.entity.ChatRoomEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "채팅 API", description = "채팅과 관련된 모든 API")
public interface ChatAPI {

    @Operation(summary = "채팅방 만들기", description = "사용자의 username으로 채팅방을 생성합니다. ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "해당 URL을 최초로 호출할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{ \"status\": 201, \"code\": \"SUCCESS_CREATE_CHATROOM\", \"message\": \"채팅방이 성공적으로 생성되었습니다.\", \"data\": { \"id\": 3, \"roomId\": \"4a9dcbfe-d529-48a4-baa0-908015470b6d\", \"name\": \"gyuri2\", \"lastMessage\": null, \"lastUpdated\": \"2024-07-28T02:10:35.2584752\", \"admin\": null, \"consultationActive\": false, \"blocked\": false } }"))),
            @ApiResponse(responseCode = "200", description = "해당 URL을 재호출할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{ \"status\": 200, \"code\": \"SUCCESS_EXIST_CHATROOM\", \"message\": \"이미 해당 username으로 채팅방이 존재합니다.\", \"data\": { \"id\": 3, \"roomId\": \"4a9dcbfe-d529-48a4-baa0-908015470b6d\", \"name\": \"gyuri2\", \"lastMessage\": null, \"lastUpdated\": \"2024-07-28T02:10:35.258475\", \"admin\": null, \"consultationActive\": false, \"blocked\": false } }"))),
    })
    public ResponseEntity<?> createRoom();

    @Operation(summary = "[상담사] 모든 채팅방 목록 조회", description = "모든 채팅방 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 채팅방 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{ \"status\": 200, \"code\": \"SUCCESS_FIND_CHATROOM\", \"message\": \"모든 채팅방을 조회했습니다.\", \"data\": [ { \"id\": 1, \"roomId\": \"e08429e4-d19b-439f-ba15-69f4f5a7fd61\", \"name\": \"gyuri1\", \"lastMessage\": \"vv\", \"lastUpdated\": \"2024-07-25T01:45:31.758362\", \"admin\": null, \"consultationActive\": false, \"blocked\": false }, { \"id\": 2, \"roomId\": \"f3231aff-68f6-4029-b679-cb38004a3a8b\", \"name\": \"gyuri\", \"lastMessage\": \"f\", \"lastUpdated\": \"2024-07-26T18:45:49.835338\", \"admin\": null, \"consultationActive\": false, \"blocked\": true } ] }"))),
    })
    public ResponseEntity<ResponseDTO<List<ChatRoomEntity>>> findAllChatRooms();

    @Operation(summary = "채팅 기록 조회", description = "특정 채팅방의 지난 채팅 기록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅 기록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ChatMessageEntity.class),
                            examples = @ExampleObject(value = "[ { \"id\": \"1\", \"type\": \"TALK\", \"roomId\": \"f3231aff-68f6-4029-b679-cb38004a3a8b\", \"sender\": \"gyuri\", \"message\": \"hello~\", \"timestamp\": \"2024-07-25T01:44:43.273071\" } ]"))),
            @ApiResponse(responseCode = "403", description = "유저가 '상담 종료'를 누른 뒤, 상담사가 채팅을 시도할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = "{ \"error\": \"채팅방 입장이 차단되었습니다.\"}"))),
    })
    public ResponseEntity<?> getMessagesByRoomId(@PathVariable String roomId);

    @Operation(summary = "메시지 보내기", description = "특정 채팅방에 메시지를 보냅니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 전송 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ChatMessageEntity.class),
                            examples = @ExampleObject(value = "{ \"roomId\": \"12345\", \"sender\": \"username\", \"message\": \"Hello!\", \"timestamp\": \"2023-07-31T12:34:56\" }"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = "{ \"status\": 400, \"code\": \"INVALID_REQUEST\", \"message\": \"잘못된 요청 데이터입니다.\", \"data\": null }"))),
    })
    public ChatMessageEntity sendMessage(ChatMessageEntity chatMessage);

    @Operation(summary = "[일반 유저] 상담 종료", description = "상담을 종료하고, 상담사 접속 차단 상태로 만듭니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 종료 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "{ 상담이 종료되고, 채팅방이 차단 상태가 되었습니다. }"))),
    })
    public ResponseEntity<String> closeRoom(@PathVariable String roomId);

    @Operation(summary = "[일반 유저] 상담 재개", description = "상담을 재개하고, 상담사 접속 허용 상태로 만듭니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 활성화 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "{ 상담이 재개되고, 채팅방이 활성화 상태가 되었습니다. }"))),
    })
    public ResponseEntity<String> openRoom(@PathVariable String roomId);

    @Operation(summary = "채팅방에 파일 전송", description = "사용자가 상담사에게 파일을 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파일 전송 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{ \"id\": 19, \"type\": \"FILE\", \"roomId\": \"f3231aff-68f6-4029-b679-cb38004a3a8b\", \"sender\": \"System\", \"message\": null, \"timestamp\": \"2024-07-27T03:48:27.4651604\", \"fileUrl\": \"https://poksin.s3.ap-northeast-2.amazonaws.com/82c26b4e-4c14-44ed-9597-687dde8539f0-myRefrigerator.png\", \"fileName\": \"myRefrigerator.png\" }")))
    })
    public ResponseEntity<ChatMessageEntity> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("roomId") String roomId
    ) throws IOException;

    @Operation(summary = "(테스트용) 모든 사용자의 username 목록 조회", description = "사용 X")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 사용자의 username 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{ x }"))),
    })
    public ResponseEntity<ResponseDTO<List<UserResponseDTO>>> findAllUsers();
}
