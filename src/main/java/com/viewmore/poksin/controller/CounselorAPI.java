package com.viewmore.poksin.controller;

import com.viewmore.poksin.dto.response.ErrorResponseDTO;
import com.viewmore.poksin.dto.response.ResponseDTO;
import com.viewmore.poksin.dto.user.CounselorRegisterDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "유저 API", description = "유저와 관련된 모든 API")
public interface CounselorAPI {

    @Operation(summary = "[상담사] 회원가입", description = "username, password, 전화번호, 전공, 경력을 입력하여 상담사 회원가입을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입을 성공했을 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{ \"status\": 201, \"code\": \"SUCCESS_COUNSELOR_REGISTER\", \"message\": \"상담사 회원가입을 성공했습니다.\", \"data\": null }"))),

            @ApiResponse(responseCode = "409", description = "데이베이스에 존재하는 아이디로 아이디를 생성하고자 할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = "{ \"status\": 409, \"code\": \"DUPLICATE_USERNAME\", \"message\": \"중복된 유저 이름입니다.\", \"data\": null }"))),
    })
    public ResponseEntity<ResponseDTO> registerCounselor(@RequestBody CounselorRegisterDTO counselorRegisterDTO);
}
