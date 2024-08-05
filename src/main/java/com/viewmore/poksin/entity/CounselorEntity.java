package com.viewmore.poksin.entity;

import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CounselorEntity extends MainUserEntity{
    // 전화번호
    private String phoneNum;
    // 전문 분야
    private String specialty;
    // 경력
    private List<String> career = new ArrayList<>();
    // 상담 횟수
    private Integer count;
    // 첫 채팅 날짜
    private LocalDateTime start;

    @Builder(builderMethodName = "counselorEntityBuilder")
    public CounselorEntity(String username, String password, String phoneNum, String specialty, List<String> career, Integer count, LocalDateTime start, String role) {
        super(username, password, role);
        this.phoneNum = phoneNum;
        this.specialty = specialty;
        this.career = career;
        this.count = count;
        this.start = start;
    }
}
