package com.flab.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NicknameCheckRequest {

    @NotBlank
    @Pattern(regexp = "^[가-힣A-Za-z0-9]{2,10}$", message = "닉네임은 2~10자, 한글/영문/숫자만 허용합니다.")
    private String nickname;
}
