package com.viewmore.poksin.dto.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// https://velog.io/@win-luck/Springboot-%EC%B9%B4%EC%B9%B4%EC%98%A4-%EC%86%8C%EC%85%9C%EB%A1%9C%EA%B7%B8%EC%9D%B8-Jwt-%ED%86%A0%ED%81%B0-%EB%B0%9C%EA%B8%89-%EB%B0%8F-API-%EA%B2%80%EC%A6%9D
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoRegisterDTO {
    // 아이디
    private String username;
    // 전화번호
    private String phoneNum;
    // 긴급 연락처
    private String emergencyNum;
    // 주소
    private String address;
    // 전화번호 공개 비공개 여부
    private boolean phoneOpen;
    // 긴급 연락처 공개 비공개 여부
    private boolean emergencyOpen;
    // 주소 공개 비공개 여부
    private boolean addressOpen;
    // 카카오 소셜 로그인을 위한 kakaoUserId
    private String kakaoAccessToken;

    public boolean getphoneOpen() {
        return phoneOpen;
    }

    public boolean getEmergencyOpen() {
        return emergencyOpen;
    }

    public boolean getAddressOpen() {
        return addressOpen;
    }
}
