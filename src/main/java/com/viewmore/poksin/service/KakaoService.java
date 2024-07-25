package com.viewmore.poksin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viewmore.poksin.dto.user.KakaoRegisterDTO;
import com.viewmore.poksin.entity.UserEntity;
import com.viewmore.poksin.exception.DuplicateKakaoIdException;
import com.viewmore.poksin.exception.DuplicateUsernameException;
import com.viewmore.poksin.repository.CounselorRepository;
import com.viewmore.poksin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
@RequiredArgsConstructor
public class KakaoService {
    private final UserRepository userRepository;
    private final CounselorRepository counselorRepository;
    private Long getKakaoId(String token) {
        // Bearer 토큰 준비
        String bearerToken = "Bearer " + token;

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", bearerToken);

        // HTTP 엔티티 생성
        HttpEntity<String> entity = new HttpEntity<>("", headers);

        // RestTemplate을 사용하여 POST 요청 보내기
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://kapi.kakao.com/v2/user/me";
        System.out.println(entity);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("조회 시 문제 발생");
        }

        // 응답 JSON 파싱하여 id 추출
        ObjectMapper objectMapper = new ObjectMapper();
        Long id;
        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            id = rootNode.path("id").asLong();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("응답 파싱 시 문제 발생");
        }

        return id;
    }

    public void kakaoRegister(KakaoRegisterDTO kakaoRegisterDTO) {
        Long id = getKakaoId(kakaoRegisterDTO.getKakaoAccessToken());

        String username = kakaoRegisterDTO.getUsername();

        // 상담사, 일반 유저 아이디 중복 검사
        Boolean isExist = false;

        isExist = userRepository.existsByKakaoUserId(id);

        if (isExist) {
            throw new DuplicateKakaoIdException("이미 카카오 계정으로 로그인하였습니다.");
        }

        isExist = userRepository.existsByUsername(username);

        if (isExist) {
            throw new DuplicateUsernameException("중복된 아이디가 존재합니다.");
        }

        isExist = counselorRepository.existsByUsername(username);

        if (isExist) {
            throw new DuplicateUsernameException("중복된 아이디가 존재합니다.");
        }

        UserEntity user = UserEntity.userEntityBuilder()
                .username(username)
                .password(null)
                .phoneNum(kakaoRegisterDTO.getPhoneNum())
                .emergencyNum(kakaoRegisterDTO.getEmergencyNum())
                .address(kakaoRegisterDTO.getAddress())
                .phoneOpen(kakaoRegisterDTO.getphoneOpen())
                .emergencyOpen(kakaoRegisterDTO.getEmergencyOpen())
                .addressOpen(kakaoRegisterDTO.getAddressOpen())
                .role("ROLE_USER")
                .kakaoUserId(id)
                .build();

        userRepository.save(user);
    }

    public UserEntity isSignedUp(String token) {
        // Bearer 토큰 준비
        String bearerToken = "Bearer " + token;

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", bearerToken);

        // HTTP 엔티티 생성
        HttpEntity<String> entity = new HttpEntity<>("", headers);

        // RestTemplate을 사용하여 POST 요청 보내기
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://kapi.kakao.com/v2/user/me";
        System.out.println(entity);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("조회 시 문제 발생");
        }

        // 응답 JSON 파싱하여 id 추출
        ObjectMapper objectMapper = new ObjectMapper();
        Long id;
        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            id = rootNode.path("id").asLong();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("응답 파싱 시 문제 발생");
        }

        UserEntity user = userRepository.findByKakaoUserId(id)
                .orElseThrow(() -> new UsernameNotFoundException("해당 카카오 id를 가진 사용자를 찾을 수 없습니다: " + String.valueOf(id)));

        return user;
    }
}
