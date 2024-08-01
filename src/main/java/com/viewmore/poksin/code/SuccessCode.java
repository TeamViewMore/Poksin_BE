package com.viewmore.poksin.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum SuccessCode {
    /**
     * User
     */
    SUCCESS_REGISTER(HttpStatus.CREATED, "회원가입을 성공했습니다."),
    SUCCESS_COUNSELOR_REGISTER(HttpStatus.CREATED, "상담사 회원가입을 성공했습니다."),
    SUCCESS_LOGIN(HttpStatus.OK, "로그인을 성공했습니다. 헤더 토큰을 확인하세요."),
    SUCCESS_RETRIEVE_USER(HttpStatus.OK, "유저 정보를 성공적으로 조회했습니다."),
    SUCCESS_REISSUE(HttpStatus.OK, "토큰 재발급을 성공했습니다. 헤더 토큰을 확인하세요."),
    SUCCESS_UPDATE_USER(HttpStatus.OK, "유저 정보를 성공적으로 수정했습니다."),
    SUCCESS_RETRIEVE_ALL_USERS(HttpStatus.OK, "모든 사용자를 성공적으로 조회했습니다."),
    SUCCESS_LOGOUT(HttpStatus.OK, "성공적으로 로그아웃했습니다."),
    SUCCESS_DELETE_USER(HttpStatus.OK, "유저가 성공적으로 삭제되었습니다."),

    /**
     * Evidence
     */
    SUCCESS_CREATE_EVIDENCE(HttpStatus.CREATED, "증거가 성공적으로 생성되었습니다."),
    SUCCESS_RETRIEVE_MONTH_EVIDENCE(HttpStatus.OK, "월별 증거를 성공적으로 조회했습니다."),
    SUCCESS_RETRIEVE_DAY_EVIDENCE(HttpStatus.OK, "일별 증거를 성공적으로 조회했습니다."),
    SUCCESS_DELETE_EVIDENCE(HttpStatus.OK, "증거를 성공적으로 삭제했습니다."),
    SUCCESS_DETAIL_VIDEO_EVIDENCE(HttpStatus.OK, "증거 영상을 성공적으로 불러왔습니다."),

    /**
     * Chat
     */
    SUCCESS_EXIST_CHATROOM(HttpStatus.OK, "이미 해당 username으로 채팅방이 존재합니다."),
    SUCCESS_CREATE_CHATROOM(HttpStatus.CREATED, "채팅방이 성공적으로 생성되었습니다."),
    SUCCESS_FIND_CHATROOM(HttpStatus.OK, "모든 채팅방을 조회했습니다."),

    ;
    private final HttpStatus status;
    private final String message;
}
