package com.viewmore.poksin.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {
    // 전화번호
    private String phoneNum;
    // 긴급 연락처
    private String emergencyNum;
    // 주소
    private String address;
    // 전화번호 공개 비공개 여부
    private Boolean phoneOpen;
    // 긴급 연락처 공개 비공개 여부
    private Boolean emergencyOpen;
    // 주소 공개 비공개 여부
    private Boolean addressOpen;
}
