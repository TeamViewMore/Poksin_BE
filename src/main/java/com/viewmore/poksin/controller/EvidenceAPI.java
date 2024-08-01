package com.viewmore.poksin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.viewmore.poksin.dto.evidence.CreateEvidenceDTO;
import com.viewmore.poksin.dto.response.ErrorResponseDTO;
import com.viewmore.poksin.dto.response.ResponseDTO;
import com.viewmore.poksin.entity.CategoryTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "증거 API", description = "증거와 관련된 모든 API")
public interface EvidenceAPI {
    @Operation(summary = "[일반 유저] 증거 업로드", description = "여러 개의 증거 파일을 내용과 함께 업로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "증거 업로드를 성공했을 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 201, \"code\": \"SUCCESS_CREATE_EVIDENCE\", \"message\": \"증거가 성공적으로 생성되었습니다.\",\n" +
                                            "\"data\": {\n" +
                                            "     \"id\": 1,\n" +
                                            "     \"category\": \"AUDIO\",\n" +
                                            "     \"title\": \"욕설, 협박 통화 녹음\",\n" +
                                            "     \"description\": \"통화하면서 남자친구가 욕설, 협박한 녹음 파일. 목소리로 들었지만 너무 무서웠고, 이때부터가 시작이었다...\",\n" +
                                            "     \"fileUrls\": [\n" +
                                            "          \"https://poksin.s3.ap-northeast-2.amazonaws.com/audio/uuid1\",\n" +
                                            "          \"https://poksin.s3.ap-northeast-2.amazonaws.com/audio/uuid2\"\n" +
                                            "     ],\n" +
                                            "     \"created_at\": \"2024-07-30T23:55:45.233128\"\n" +
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

            @ApiResponse(responseCode = "404", description = "존재하지 않는 access Token 혹은 카테고리로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(name = "USER_NOT_FOUND", value = "{ \"status\": 404, \"code\": \"USER_NOT_FOUND\", \"message\": \"사용자를 찾을 수 없습니다.\", \"data\": null }"),
                                    @ExampleObject(name = "CATEGORY_NOT_FOUND", value = "{ \"status\": 404, \"code\": \"CATEGORY_NOT_FOUND\", \"message\": \"카테고리를 찾을 수 없습니다.\", \"data\": null }"),
                            })),

    })
    public ResponseEntity<ResponseDTO> createListFile(
            @RequestPart("createEvidenceDTO")  CreateEvidenceDTO createEvidenceDTO,
            @RequestPart("fileUrls") List<MultipartFile> fileUrls
    ) throws IOException;

    @Operation(summary = "[일반 유저] 증거 월별 조회", description = "월별로 업로드한 증거 내용을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업로드한 증거를 조회할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_RETRIEVE_MONTH_EVIDENCE\", \"message\": \"월별 증거를 성공적으로 조회했습니다.\",\n" +
                                            "\"data\": [\n" +
                                            "     {\n" +
                                            "          \"evidenceCount\": 23\n" +
                                            "     }\n" +
                                            "]\n" +
                                            "}"))),

            @ApiResponse(responseCode = "401", description = "잘못된 토큰으로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(name = "INVALID_ACCESS_TOKEN", value = "{ \"status\": 401, \"code\": \"INVALID_ACCESS_TOKEN\", \"message\": \"유효하지 않은 토큰입니다.\", \"data\": null }"),
                                    @ExampleObject(name = "TOKEN_EXPIRED", value = "{ \"status\": 401, \"code\": \"TOKEN_EXPIRED\", \"message\": \"토큰이 만료되었습니다.\", \"data\": null }"),
                                    @ExampleObject(name = "TOKEN_MISSING", value = "{ \"status\": 401, \"code\": \"TOKEN_MISSING\", \"message\": \"요청 헤더에 토큰이 없습니다.\", \"data\": null }")
                            })),

            @ApiResponse(responseCode = "404", description = "존재하지 않는 access Token 혹은 evidence id로 요청할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(value = "{ \"status\": 404, \"code\": \"USER_NOT_FOUND\", \"message\": \"사용자를 찾을 수 없습니다.\", \"data\": null }"),
                            })),

    })
    public ResponseEntity<ResponseDTO> findAllEvidenceByMonth(
            @RequestParam("year") String year,
            @RequestParam("month") String month
    );

    @Operation(summary = "[일반 유저] 증거 일별 조회", description = "요청한 날(day)에 업로드한 증거를 모두 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업로드한 증거를 조회할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_RETRIEVE_DAY_EVIDENCE\", \"message\": \"일별 증거를 성공적으로 조회했습니다.\",\n" +
                                            "\"data\": [\n" +
                                            "     {\n" +
                                            "          \"id\": 1,\n" +
                                            "          \"category\": \"IMAGE\",\n" +
                                            "          \"title\": \"제목1\",\n" +
                                            "          \"description\": \"설명1 \",\n" +
                                            "          \"fileUrls\": [\n" +
                                            "               \"https://poksin.s3.ap-northeast-2.amazonaws.com/image/uuid\"\n" +
                                            "          ],\n" +
                                            "          \"created_at\": \"2024-07-30T23:24:39.959183\"\n" +
                                            "     },\n" +
                                            "     {\n" +
                                            "          \"id\": 2,\n" +
                                            "          \"category\": \"AUDIO\",\n" +
                                            "          \"title\": \"제목2\",\n" +
                                            "          \"description\": \"설명2 \",\n" +
                                            "          \"fileUrls\": [\n" +
                                            "               \"https://poksin.s3.ap-northeast-2.amazonaws.com/audio/uuid\"\n" +
                                            "          ],\n" +
                                            "          \"created_at\": \"2024-07-30T23:27:40.316267\"\n" +
                                            "     },\n" +
                                            "     {\n" +
                                            "          \"id\": 3,\n" +
                                            "          \"category\": \"CONSULTATION\",\n" +
                                            "          \"title\": \"제목3\",\n" +
                                            "          \"detection\": null,\n" +
                                            "          \"done\": false,\n" +
                                            "          \"description\": \"설명3 \",\n" +
                                            "          \"fileUrls\": [\n" +
                                            "               \"https://poksin.s3.ap-northeast-2.amazonaws.com/consultation/uuid\"\n" +
                                            "          ],\n" +
                                            "          \"created_at\": \"2024-07-30T23:39:50.30258\"\n" +
                                            "     }\n" +
                                            "]\n" +
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
    public ResponseEntity<ResponseDTO> findAllEvidenceByDay(
            @RequestParam("year") String year,
            @RequestParam("month") String month,
            @RequestParam("day") String day,
            @RequestParam("category") CategoryTypeEnum category
    );

    @Operation(summary = "[일반 유저] 업로드한 증거 삭제", description = "증거 id를 지정하여 업로드한 증거를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업로드한 증거를 삭제할 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class),
                            examples = @ExampleObject(value =
                                    "{ \"status\": 200, \"code\": \"SUCCESS_DELETE_EVIDENCE\", \"message\": \"증거를 성공적으로 삭제했습니다.\", \"data\": null }"))),

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
                                    @ExampleObject(name = "USER_NOT_FOUND", value = "{ \"status\": 404, \"code\": \"USER_NOT_FOUND\", \"message\": \"사용자를 찾을 수 없습니다.\", \"data\": null }"),
                                    @ExampleObject(name = "EVIDENCE_NOT_FOUND", value = "{ \"status\": 404, \"code\": \"EVIDENCE_NOT_FOUND\", \"message\": \"증거를 찾을 수 없습니다.\", \"data\": null }"),
                            })),

    })
    public ResponseEntity<ResponseDTO> deleteEvidence(
            @PathVariable("id") Integer id
    ) throws JsonProcessingException;

    // 추가해야 함
    @Operation(summary = "[일반 유저] 분석한 영상 증거 조회")
    public ResponseEntity<ResponseDTO> detailVideoEvidence(
            @PathVariable("id") Integer id
    ) throws JsonProcessingException;
}
