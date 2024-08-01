package com.viewmore.poksin.controller;

import com.viewmore.poksin.code.ErrorCode;
import com.viewmore.poksin.code.SuccessCode;
import com.viewmore.poksin.dto.response.ErrorResponseDTO;
import com.viewmore.poksin.dto.user.*;
import com.viewmore.poksin.dto.response.ResponseDTO;
import com.viewmore.poksin.entity.RefreshEntity;
import com.viewmore.poksin.jwt.JWTUtil;
import com.viewmore.poksin.repository.RefreshRedisRepository;
import com.viewmore.poksin.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController implements UserAPI{
    private final JWTUtil jwtUtil;
    private final UserService userService;
    private final RefreshRedisRepository refreshRedisRepository;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerUser(@RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_REGISTER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_REGISTER, null));
    }

    @GetMapping("/mypage")
    public ResponseEntity<ResponseDTO> mypage() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserResponseDTO response = userService.mypage(username);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_USER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_USER, response));
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDTO> updateUser(@RequestBody  UpdateUserDTO updateUserDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserResponseDTO response = userService.updateUser(username, updateUserDTO);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_UPDATE_USER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_UPDATE_USER, response));
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseDTO> getAllUsers() {
        List<UserResponseDTO> users = userService.findAllUsers();
        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_USER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_USER, users));
    }

    @DeleteMapping("/delete")
    public ResponseEntity deleteUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 헤더에서 refresh키에 담긴 토큰을 꺼냄
        String refreshToken = request.getHeader("refresh");

        ErrorCode errorCode = null;
        boolean flag = true;

        if (refreshToken == null) {
            errorCode = ErrorCode.TOKEN_MISSING;
            flag = false;
        }

        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            errorCode = ErrorCode.TOKEN_EXPIRED;
            flag = false;
        }

        String type = jwtUtil.getType(refreshToken);
        if (flag && !type.equals("refreshToken")) {
            errorCode = ErrorCode.INVALID_REFRESH_TOKEN;
            flag = false;
        }

        // DB에 저장되어 있는지 확인
        Optional<RefreshEntity> isExist = refreshRedisRepository.findById(refreshToken);

        if (flag && isExist.isEmpty()) {
            errorCode = ErrorCode.TOKEN_NOT_FOUND;
        }

        if(errorCode != null) {
            return ResponseEntity
                    .status(errorCode.getStatus().value())
                    .body(new ErrorResponseDTO(errorCode));
        }

        String username = jwtUtil.getUsername(refreshToken);

        // user 삭제
        userService.deleteUser(username);

        // refresh token 삭제
        refreshRedisRepository.deleteById(refreshToken);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_DELETE_USER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_DELETE_USER, null));
    }
}
