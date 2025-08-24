package com.flab.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400 BAD REQUEST
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "입력 값이 유효하지 않습니다."),

    // 401 UNAUTHORIZED
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증되지 않은 사용자입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "현재 요청을 수행할 권한이 없습니다."),
    // 404 NOT FOUND
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "존재하지 않는 사용자입니다."),
    DESIGN_NOT_FOUND(HttpStatus.NOT_FOUND, "DESIGN_NOT_FOUND", "해당 디자인을 찾을 수 없습니다."),
    //409 CONFLICT
    CONFLICT(HttpStatus.CONFLICT, "CONFLICT", "이미 존재하는 리소스입니다."),

    // 500 INTERNAL SERVER ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String error;
    private final String message;

}
