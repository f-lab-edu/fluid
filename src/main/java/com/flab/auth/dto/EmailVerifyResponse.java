package com.flab.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailVerifyResponse {

    private String emailVerificationToken; // 이메일 인증 완료 토큰
}
