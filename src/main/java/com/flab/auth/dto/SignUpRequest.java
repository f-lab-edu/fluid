package com.flab.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank(message = "이메일 인증 토큰이 필요합니다.")
    private String emailVerificationToken;

    @NotBlank
    @Pattern(regexp = "^[가-힣A-Za-z0-9]{2,10}$", message = "닉네임은 2~10자, 한글/영문/숫자만 허용합니다.")
    private String nickname;

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!-/:-@\\[-`{-~])[!-~]{8,32}$",
            message = "비밀번호는 8~32자, 영문 대/소문자,숫자,특수문자를 각 하나 이상 포함해야 합니다."
    )
    private String password;
}
