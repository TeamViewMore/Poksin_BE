package com.viewmore.poksin.dto.user;

import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^\\d{11}$", message = "전화번호는 11개의 숫자로만 구성되어야 합니다.")
    private String phoneNum;
    // 긴급 연락처
    @Pattern(regexp = "^\\d{11}$", message = "긴급 연락처는 11개의 숫자로만 구성되어야 합니다.")
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
