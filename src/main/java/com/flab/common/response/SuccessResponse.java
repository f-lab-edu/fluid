package com.flab.common.response;

import lombok.Getter;

/**
 * 모든 성공 응답의 공통적인 형태를 정의한 클래스입니다.
 * @param <T>
 */
@Getter
public class SuccessResponse<T> {

    private final boolean success;
    private final T data;

    public SuccessResponse(T data){
        this.success = true;
        this.data = data;
    }

    public static <T> SuccessResponse<T> of(T data){
        return new SuccessResponse<>(data);
    }
}
