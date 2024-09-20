package com.viewmore.poksin.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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

    // 상담 횟수 (전체 채팅방 수)
    @OneToOne
    @JoinColumn(name = "chat_room_count_id")
    private ChatRoomCountEntity chatRoomCount;

    // 첫 채팅 날짜
    private LocalDateTime start;

    @Builder(builderMethodName = "counselorEntityBuilder")
    public CounselorEntity(String username, String password, String phoneNum, String specialty, List<String> career, ChatRoomCountEntity chatRoomCount, LocalDateTime start, String role) {
        super(username, password, role);
        this.phoneNum = phoneNum;
        this.specialty = specialty;
        this.career = career;
        this.chatRoomCount = chatRoomCount; // 초기화
        this.start = start;
    }
}
