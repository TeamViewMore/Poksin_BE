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


    @Operation(summary = "[상담사] 정보 조회", description = "상담사의 아이디, 전화번호, 전문 분야, 경력을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입을 성공했을 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_COUNSELOR_REGISTER\", \"message\": \"상담사 회원가입을 성공했습니다.\",\n" +
                                            "\"data\": {\n" +
                                            "     \"username\": \"구름빵\",\n" +
                                            "     \"phoneNum\": \"02-1234-5678\",\n" +
                                            "     \"specialty\": \"부부/가족상담\",\n" +
                                            "     \"career\": [\n" +
                                            "          \"폭신폭신 상담센터 원장\",\n" +
                                            "          \"마음돌봄 상담센터 대표\",\n" +
                                            "          \"덕성여자대학교 상담치료학과 석사\"\n" +
                                            "     ],\n" +
                                            "     \"count\": 13,\n" +
                                            "     \"role\": \"ROLE_ADMIN\"\n" +
                                            "     }\n" +
                                            "}"))),

            @ApiResponse(responseCode = "401", description = "잘못된 토큰으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(name = "INVALID_ACCESS_TOKEN", value = "{ \"status\": 401, \"code\": \"INVALID_ACCESS_TOKEN\", \"message\": \"유효하지 않은 토큰입니다.\", \"data\": null }"),
                                    @ExampleObject(name = "TOKEN_EXPIRED", value = "{ \"status\": 401, \"code\": \"TOKEN_EXPIRED\", \"message\": \"토큰이 만료되었습니다.\", \"data\": null }"),
                                    @ExampleObject(name = "TOKEN_MISSING", value = "{ \"status\": 401, \"code\": \"TOKEN_MISSING\", \"message\": \"요청 헤더에 토큰이 없습니다.\", \"data\": null }")
                            })),

            @ApiResponse(responseCode = "404", description = "존재하지 않는 access Token으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = "{ \"status\": 404, \"code\": \"USER_NOT_FOUND\", \"message\": \"사용자를 찾을 수 없습니다.\", \"data\": null }"))),

    })
    public ResponseEntity<ResponseDTO> counselorMypage();
}
