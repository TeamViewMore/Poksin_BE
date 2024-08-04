package com.viewmore.poksin.dto.user;

import com.viewmore.poksin.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    // 아이디
    private String username;
    // 전화번호
    private String phoneNum;
    // 긴급 연락처
    private String emergencyNum;
    // 주소
    private String address;
    // 상담사인지 일반 유저인지 구분
    private String role;
    // 가입일
    private LocalDateTime createdAt;

    public static UserResponseDTO toDto(UserEntity entity) {
        return UserResponseDTO.builder()
                .username(entity.getUsername())
                .phoneNum(entity.getPhoneOpen() ? entity.getPhoneNum() : null)
                .emergencyNum(entity.getEmergencyOpen() ? entity.getEmergencyNum() : null)
                .address(entity.getAddressOpen() ? entity.getAddress() : null)
                .role(entity.getRole())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
