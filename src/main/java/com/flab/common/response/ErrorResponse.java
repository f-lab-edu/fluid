package com.flab.common.response;

import lombok.Getter;

/**
 * 모든 실패응답의 공통적인 형태를 정의한 클래스입니다.
 */
public class ErrorResponse {

    private final boolean success;
    private final ErrorDetail errorDetail;

    public ErrorResponse(ErrorCode errorCode){
        this.success = false;
        this.errorDetail = new ErrorDetail(
                errorCode.getStatus().value(),
                errorCode.getError(),
                errorCode.getMessage()
        );
    }

    public static ErrorResponse of(ErrorCode code) {
        return new ErrorResponse(code);
    }

    @Getter
    public static class ErrorDetail {
        private final int statusCode; //상태코드
        private final String errorCode; //에러코드명
        private final String errorMessage; // 에러메세지

        public ErrorDetail(int statusCode, String errorCode, String errorMessage) {
            this.statusCode = statusCode;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }
}
