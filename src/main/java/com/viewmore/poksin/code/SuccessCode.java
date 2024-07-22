package com.viewmore.poksin.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum SuccessCode {
    SUCCESS_REGISTER(HttpStatus.OK, "회원가입을 성공했습니다."),

    ;


    private final HttpStatus status;
    private final String message;
}