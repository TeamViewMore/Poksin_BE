package com.viewmore.poksin.controller;

import com.viewmore.poksin.dto.response.ErrorResponseDTO;
import com.viewmore.poksin.dto.response.ResponseDTO;
import com.viewmore.poksin.dto.user.RegisterDTO;
import com.viewmore.poksin.dto.user.UpdateUserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

@Tag(name = "유저 API", description = "유저와 관련된 모든 API")
public interface UserAPI {
    @Operation (summary = "[일반 유저] 회원가입", description = "아이디, 비밀번호, 전화번호, 긴급 전화번호, 주소, 공개 여부를 입력하여 회원가입을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입을 성공했을 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value = "{ \"status\": 201, \"code\": \"SUCCESS_REGISTER\", \"message\": \"회원가입을 성공했습니다.\", \"data\": null }"))),

            @ApiResponse(responseCode = "409", description = "데이베이스에 존재하는 아이디로 아이디를 생성하고자 할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = "{ \"status\": 409, \"code\": \"DUPLICATE_USERNAME\", \"message\": \"중복된 유저 이름입니다.\", \"data\": null }"))),
    })
    public ResponseEntity<ResponseDTO> registerUser(@RequestBody RegisterDTO registerDTO);

    @Operation(summary = "[일반 유저] 유저 정보 조회", description = "유저가 공개를 설정한 범위에 한해서 유저의 정보를 조회합니다. 공개하지 않은 항목의 경우 null로 응답합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 정보를 성공적으로 조회했을 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_RETRIEVE_USER\", \"message\": \"유저 정보를 성공적으로 조회했습니다.\", \"data\": { \"username\": \"poksin\", \"phoneNum\": \"010-1234-5678\", \"emergencyNum\": null, \"address\": \"경기도 과천시\", \"role\": \"ROLE_USER\", \"createdAt\": \"2024-07-30T20:32:10.441113\" } }"))),

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
                            examples = {
                                    @ExampleObject(value = "{ \"status\": 404, \"code\": \"USER_NOT_FOUND\", \"message\": \"사용자를 찾을 수 없습니다.\", \"data\": null }"),
                            })),
    })
    public ResponseEntity<ResponseDTO> mypage();

    @Operation(summary = "[일반 유저] 유저 정보 수정", description = "유저의 전화번호, 긴급 전화번호, 주소, 공개 여부 수정을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 정보를 성공적으로 수정했을 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_UPDATE_USER\", \"message\": \"유저 정보를 성공적으로 수정했습니다.\", \"data\": { \"username\": \"poksin\", \"phoneNum\": \"010-1234-5678\", \"emergencyNum\": \"010-1111-2222\", \"address\": \"null\", \"role\": \"ROLE_USER\", \"createdAt\": \"2024-07-30T20:32:10.441113\" } }"))),

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
                            examples = {
                                    @ExampleObject(value = "{ \"status\": 404, \"code\": \"USER_NOT_FOUND\", \"message\": \"사용자를 찾을 수 없습니다.\", \"data\": null }"),
                            })),
    })
    public ResponseEntity<ResponseDTO> updateUser(@RequestBody UpdateUserDTO updateUserDTO);

    @Operation(summary = "[일반 유저] 회원 탈퇴",
            parameters =
            @Parameter(name = "refresh", description = "Refresh token", required = true, in = ParameterIn.HEADER, example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."),
            description = "refresh Token을 입력하여 회원 탈퇴를 진행합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 정보를 성공적으로 수정했을 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_DELETE_USER\", \"message\": \"유저가 성공적으로 삭제되었습니다.\", \"data\": null }"))),

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
                            examples = {
                                    @ExampleObject(value = "{ \"status\": 404, \"code\": \"USER_NOT_FOUND\", \"message\": \"사용자를 찾을 수 없습니다.\", \"data\": null }"),
                            })),
    })
    public ResponseEntity deleteUser(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
