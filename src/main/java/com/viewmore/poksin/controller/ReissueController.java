package com.viewmore.poksin.controller;

import com.viewmore.poksin.code.ErrorCode;
import com.viewmore.poksin.code.SuccessCode;
import com.viewmore.poksin.dto.response.ResponseDTO;
import com.viewmore.poksin.entity.RefreshEntity;
import com.viewmore.poksin.jwt.JWTUtil;
import com.viewmore.poksin.repository.RefreshRedisRepository;
import com.viewmore.poksin.util.TokenErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/reissue")
@RequiredArgsConstructor
public class ReissueController implements ReissueAPI {
    private final JWTUtil jwtUtil;
    private final RefreshRedisRepository refreshRedisRepository;

    @PostMapping
    public ResponseEntity<ResponseDTO> reissue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 헤더에서 refresh 키에 담긴 토큰을 꺼냄
        String refreshToken = request.getHeader("refresh");

        if (refreshToken == null) {
            TokenErrorResponse.sendErrorResponse(response, ErrorCode.TOKEN_MISSING);
            return null;
        }

        try {
            if (jwtUtil.isExpired(refreshToken)) {
                TokenErrorResponse.sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
                return null;
            }

            String type = jwtUtil.getType(refreshToken);
            if (!type.equals("refreshToken")) {
                TokenErrorResponse.sendErrorResponse(response, ErrorCode.INVALID_REFRESH_TOKEN);
                return null;
            }

            // DB에 저장되어 있는지 확인
            Optional<RefreshEntity> isExist = refreshRedisRepository.findById(refreshToken);
            if (isExist.isEmpty()) {
                TokenErrorResponse.sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
                return null;
            }

            String username = jwtUtil.getUsername(refreshToken);
            String role = jwtUtil.getRole(refreshToken);

            // 새로운 Access token과 refreshToken 생성
            String newAccessToken = jwtUtil.createJwt("accessToken", username, role, 600000L);
            String newRefreshToken = jwtUtil.createJwt("refreshToken", username, role, 600000L);

            refreshRedisRepository.deleteById(refreshToken);
            addRefreshEntity(newRefreshToken, username);

            response.setHeader("accessToken", "Bearer " + newAccessToken);
            response.setHeader("refreshToken", "Bearer " + newRefreshToken);

            return ResponseEntity
                    .status(HttpStatus.OK.value())
                    .body(new ResponseDTO<>(SuccessCode.SUCCESS_REISSUE, null));

        } catch (ExpiredJwtException e) {
            TokenErrorResponse.sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
            return null;
        } catch (Exception e) {
            TokenErrorResponse.sendErrorResponse(response, ErrorCode.INVALID_REFRESH_TOKEN);
            return null;
        }
    }

    private void addRefreshEntity(String refresh, String username) {
        RefreshEntity refreshEntity = new RefreshEntity(refresh, username);
        refreshRedisRepository.save(refreshEntity);
    }
}
