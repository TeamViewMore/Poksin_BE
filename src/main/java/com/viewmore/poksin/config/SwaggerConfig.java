package com.viewmore.poksin.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .description("토큰값을 입력하여 인증을 활성화할 수 있습니다.")
                .bearerFormat("JWT")
        );

        OpenAPI openAPI = new OpenAPI()
                .components(components)
                .info(new Info()
                        .title("2024 중앙 해커톤 백엔드 poksin API")
                        .description("2024 중앙 해커톤 백엔드 poksin API 명세서")
                        .version("1.0.0"))
                .addSecurityItem(securityRequirement);

        addLoginPath(openAPI); // 로그인 경로 추가
        addLogoutPath(openAPI); // 로그아웃 경로 추가

        return openAPI;
    }

    private void addLoginPath(OpenAPI openAPI) {
        RequestBody requestBody = new RequestBody()
                .content(new Content()
                        .addMediaType("multipart/form-data", new MediaType()
                                .schema(new Schema<>()
                                        .addProperty("username", new Schema<String>().type("string").description("사용자 이름").example("poksin"))
                                        .addProperty("password", new Schema<String>().type("string").description("비밀번호").example("poksin1234")))))
                .required(true); // 필수 설정

        ApiResponse successResponse = new ApiResponse()
                .description("성공적으로 로그인된 경우")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<>()
                                        .addProperty("status", new Schema<Integer>().type("integer"))
                                        .addProperty("code", new Schema<String>().type("string"))
                                        .addProperty("message", new Schema<String>().type("string"))
                                        .addProperty("data", new Schema<Object>().type("object"))
                                        .example("{ \"status\": 200, \"code\": \"SUCCESS_LOGIN\", \"message\": \"로그인을 성공했습니다. 헤더 토큰을 확인하세요.\", \"data\": null }"))));

        ApiResponse unauthorizedResponse = new ApiResponse()
                .description("아이디 혹은 비밀번호를 잘못 입력했을 경우")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<>()
                                        .addProperty("status", new Schema<Integer>().type("integer"))
                                        .addProperty("code", new Schema<String>().type("string"))
                                        .addProperty("message", new Schema<String>().type("string"))
                                        .addProperty("data", new Schema<String>().type("string").nullable(true)))
                                .example("{ \"status\": 401, \"code\": \"USER_NOT_FOUND\", \"message\": \"사용자를 찾을 수 없습니다\", \"data\": null }")));

        ApiResponses apiResponses = new ApiResponses();
        apiResponses.addApiResponse("200", successResponse);
        apiResponses.addApiResponse("401", unauthorizedResponse);

        PathItem pathItem = new PathItem()
                .post(new io.swagger.v3.oas.models.Operation()
                        .addTagsItem("유저 API")
                        .summary("[일반 유저, 상담사] 로그인")
                        .description("username과 password를 입력하여 로그인을 진행합니다.")
                        .requestBody(requestBody)
                        .responses(apiResponses));

        openAPI.path("/login", pathItem); // 경로 추가
    }

    private void addLogoutPath(OpenAPI openAPI) {
        Parameter refreshParameter = new Parameter()
                .name("refresh")
                .in("header")
                .required(true)
                .schema(new Schema<String>().type("string"))
                .description("리프레시 토큰");

        ApiResponse successResponse = new ApiResponse()
                .description("성공적으로 로그아웃된 경우")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<>()
                                        .addProperty("status", new Schema<Integer>().type("integer"))
                                        .addProperty("code", new Schema<String>().type("string"))
                                        .addProperty("message", new Schema<String>().type("string"))
                                        .addProperty("data", new Schema<String>().type("string").nullable(true)))
                                .example("{ \"status\": 200, \"code\": \"SUCCESS_LOGOUT\", \"message\": \"성공적으로 로그아웃했습니다.\", \"data\": null }")));

        ApiResponse invalidRequestResponse = new ApiResponse()
                .description("인증 정보가 유효하지 않을 경우")
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<>()
                                        .addProperty("status", new Schema<Integer>().type("integer"))
                                        .addProperty("code", new Schema<String>().type("string"))
                                        .addProperty("message", new Schema<String>().type("string"))
                                        .addProperty("data", new Schema<String>().type("string").nullable(true)))
                                .addExamples("TOKEN_EXPIRED", new Example()
                                        .value("{ \"status\": 401, \"code\": \"TOKEN_EXPIRED\", \"message\": \"토큰이 만료되었습니다.\", \"data\": null }"))
                                .addExamples("TOKEN_MISSING", new Example()
                                        .value("{ \"status\": 401, \"code\": \"TOKEN_MISSING\", \"message\": \"요청 헤더에 토큰이 없습니다.\", \"data\": null }"))
                                .addExamples("INVALID_REFRESH_TOKEN", new Example()
                                        .value("{ \"status\": 401, \"code\": \"INVALID_REFRESH_TOKEN\", \"message\": \"유효하지 않은 리프레시 토큰입니다.\", \"data\": null }"))));

        ApiResponses apiResponses = new ApiResponses();
        apiResponses.addApiResponse("200", successResponse);
        apiResponses.addApiResponse("401", invalidRequestResponse);

        PathItem pathItem = new PathItem()
                .post(new io.swagger.v3.oas.models.Operation()
                        .addTagsItem("유저 API")
                        .summary("[일반 유저, 상담사] 로그아웃")
                        .description("refresh 토큰을 헤더에 추가하여 로그아웃을 진행합니다.")
                        .addParametersItem(refreshParameter)
                        .responses(apiResponses));

        openAPI.path("/logout", pathItem); // 경로 추가
    }
}