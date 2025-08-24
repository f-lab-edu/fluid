package com.flab.common.exception;

import com.flab.common.response.BusinessException;
import com.flab.common.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //TODO :  에러 발생 시 log 남기기

    /**
     * 비즈니스 로직에서 명시적으로 발생시키는 예외 -> ErrorResponse 객체로 감싸 반환
     * @param ex
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex){
        ErrorCode errorCode = ex.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode, ex.getErrorMessage()));
    }

    /**
     * Validation 실패 시 발생하는 예외 처리 -> BAD_REQUEST 에러로 매핑,  ErrorResponse 객체로 감싸 반환
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = (fieldError != null) ? fieldError.getDefaultMessage() : "입력 값이 유효하지 않습니다.";

        return ResponseEntity
                .status(ErrorCode.VALIDATION_ERROR.getStatus())
                .body(ErrorResponse.of(ErrorCode.VALIDATION_ERROR, message));
    }


    /**
     * 비즈니스 예외 이외의 모든 예외 처리 -> INTERNAL_SERVER_ERROR 에러로 매핑, ErrorResponse 객체로 감싸 반환
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }
}
