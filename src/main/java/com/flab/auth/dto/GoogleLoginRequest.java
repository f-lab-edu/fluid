package com.flab.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 구글 로그인/회원 가입 요청 dto입니다.
 */
@Getter
@NoArgsConstructor
public class GoogleLoginRequest {

    @NotBlank(message = "구글 idToken은 필수 값입니다.")
    private String idToken;
}
