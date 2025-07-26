package com.flab.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class SignUpRequest {

    private String email; // 이메일
    private String userName; // 닉네임
    private String password; // 비밀번호

}
