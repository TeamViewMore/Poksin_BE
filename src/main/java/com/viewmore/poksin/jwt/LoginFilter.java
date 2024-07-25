package com.viewmore.poksin.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viewmore.poksin.code.ErrorCode;
import com.viewmore.poksin.code.SuccessCode;
import com.viewmore.poksin.dto.user.CustomUserDetails;
import com.viewmore.poksin.dto.response.ErrorResponseDTO;
import com.viewmore.poksin.dto.response.ResponseDTO;
import com.viewmore.poksin.entity.RefreshEntity;
import com.viewmore.poksin.repository.RefreshRedisRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;


@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRedisRepository refreshRedisRepository;


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        // 추후 삭제 필요, 확인 용
        System.out.println(username);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();


        String accesstoken = jwtUtil.createJwt("accessToken", username, role, 86400000L);
        String refreshToken = jwtUtil.createJwt("refreshToken", username, role, 86400000L);

        addRefreshEntity(refreshToken, username);


        response.addHeader("accessToken", "Bearer " + accesstoken);
        response.addHeader("refreshToken", "Bearer " + refreshToken);

        ResponseDTO responseDTO = new ResponseDTO<>(SuccessCode.SUCCESS_LOGIN, null);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(responseDTO);
        response.getWriter().write(jsonResponse);

    }

    private void addRefreshEntity(String refresh, String username) {
        RefreshEntity refreshEntity = new RefreshEntity(refresh, username);
        refreshRedisRepository.save(refreshEntity);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {

        response.setStatus(401);

        ErrorResponseDTO responseDTO = new ErrorResponseDTO(ErrorCode.USER_NOT_FOUND);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(responseDTO);
        response.getWriter().write(jsonResponse);
    }

    public void addTokensToResponse(HttpServletResponse response, String username, String role) throws IOException {
        String accesstoken = jwtUtil.createJwt("accessToken", username, role, 86400000L);
        String refreshToken = jwtUtil.createJwt("refreshToken", username, role, 86400000L);

        addRefreshEntity(refreshToken, username);

        response.addHeader("accessToken", "Bearer " + accesstoken);
        response.addHeader("refreshToken", "Bearer " + refreshToken);

        ResponseDTO responseDTO = new ResponseDTO<>(SuccessCode.SUCCESS_LOGIN, null);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(responseDTO);
        response.getWriter().write(jsonResponse);
    }
}

