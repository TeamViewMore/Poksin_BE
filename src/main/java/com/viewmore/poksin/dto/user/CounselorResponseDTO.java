package com.viewmore.poksin.dto.user;

import com.viewmore.poksin.entity.CounselorEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CounselorResponseDTO {
    // 아이디
    private String username;
    // 전화번호
    private String phoneNum;
    // 전문 분야
    private String specialty;
    // 경력
    private List<String> career = new ArrayList<>();
    // 상담 횟수
    private Integer count;
    // 가장 처음 보낸 채팅 날짜
    private LocalDateTime start;
    // 어드민 계정인지 여부
    private String role;

    public static CounselorResponseDTO toDto(CounselorEntity entity) {
        return CounselorResponseDTO.builder()
                .username(entity.getUsername())
                .phoneNum(entity.getPhoneNum())
                .specialty(entity.getSpecialty())
                .career(entity.getCareer())
                .count(entity.getCount())
                .start(entity.getStart())
                .role(entity.getRole())
                .build();
    }
}
