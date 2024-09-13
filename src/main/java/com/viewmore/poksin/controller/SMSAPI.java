package com.viewmore.poksin.controller;

import com.viewmore.poksin.dto.response.ErrorResponseDTO;
import com.viewmore.poksin.dto.response.ResponseDTO;
import com.viewmore.poksin.dto.sms.SMSResponseDTO;
import com.viewmore.poksin.dto.sms.SendSMSDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "SMS 발송 API", description = "SMS 발송 관련 모든 API")
public interface SMSAPI {
    @Operation(
            summary = "[일반 유저] 현재 위치 긴급 연락처로 SMS 전송",
            description = "현재 위치를 긴급 연락처로 SMS로 전송합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "메세지를 성공적으로 전송했을 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SMSResponseDTO.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "    \"status\": 200,\n" +
                                            "    \"code\": \"SUCCESS_SEND_MESSAGE\",\n" +
                                            "    \"message\": \"메세지를 성공적으로 전송했습니다.\",\n" +
                                            "    \"totalCount\": null,\n" +
                                            "    \"totalDuration\": null,\n" +
                                            "    \"data\": {\n" +
                                            "        \"groupId\": \"G4V20240913224342HRLTACGHMS8SWZH\",\n" +
                                            "        \"to\": \"01022292512\",\n" +
                                            "        \"from\": \"01082302512\",\n" +
                                            "        \"type\": \"SMS\",\n" +
                                            "        \"statusMessage\": null,\n" +
                                            "        \"country\": \"82\",\n" +
                                            "        \"messageId\": \"M4V20240913224342G1KIWSJVBTPXOKO\",\n" +
                                            "        \"statusCode\": \"2000\",\n" +
                                            "        \"accountId\": \"24091346662872\"\n" +
                                            "    }\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청일 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = "{ \"status\": 400, \"code\": \"BAD_REQUEST\", \"message\": \"잘못된 요청입니다.\", \"data\": null }"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "권한이 없는 요청일 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(
                                    value = "{ \"status\": 401, \"code\": \"UNAUTHORIZED\", \"message\": \"권한이 없습니다.\", \"data\": null }"
                            )
                    )
            )
    })
    public ResponseEntity<ResponseDTO> sendLocation(@RequestBody SendSMSDTO sendSMSDTO);
}
