package com.viewmore.poksin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viewmore.poksin.code.SuccessCode;
import com.viewmore.poksin.dto.response.ResponseDTO;
import com.viewmore.poksin.dto.user.CustomUserDetails;
import com.viewmore.poksin.dto.user.KaKaoTokenDTO;
import com.viewmore.poksin.dto.user.KakaoRegisterDTO;
import com.viewmore.poksin.entity.RefreshEntity;
import com.viewmore.poksin.entity.UserEntity;
import com.viewmore.poksin.jwt.JWTUtil;
import com.viewmore.poksin.repository.RefreshRedisRepository;
import com.viewmore.poksin.service.KakaoService;
import com.viewmore.poksin.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth/kakao")
@RequiredArgsConstructor
public class KakaoController {
    private final JWTUtil jwtUtil;
    private final RefreshRedisRepository refreshRedisRepository;
    private final KakaoService kakaoService;


    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> kakaoRegister(@RequestBody KakaoRegisterDTO kakaoRegisterDTO) {
        kakaoService.kakaoRegister(kakaoRegisterDTO);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_REGISTER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_REGISTER, null));
    }


    @PostMapping("/login")
    public ResponseEntity<?> kakaoLogin(@RequestBody KaKaoTokenDTO kaKaoTokenDTO, HttpServletResponse response) throws IOException {
        UserEntity user = kakaoService.isSignedUp(kaKaoTokenDTO.getToken());
        // CustomUserDetails 생성
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        String role = customUserDetails.getAuthorities().iterator().next().getAuthority();
        String username = customUserDetails.getUsername();

        // JWT 토큰 생성 및 응답 헤더에 추가
        String accesstoken = jwtUtil.createJwt("accessToken", username, role, 86400000L);
        String refreshToken = jwtUtil.createJwt("refreshToken", username, role, 86400000L);

        RefreshEntity refreshEntity = new RefreshEntity(refreshToken, customUserDetails.getUsername());
        refreshRedisRepository.save(refreshEntity);


        response.addHeader("accessToken", "Bearer " + accesstoken);
        response.addHeader("refreshToken", "Bearer " + refreshToken);

        ResponseDTO responseDTO = new ResponseDTO<>(SuccessCode.SUCCESS_LOGIN, null);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(responseDTO);
        response.getWriter().write(jsonResponse);

        return ResponseEntity.ok().build(); // 빈 응답 반환
    }
}
