package com.flab.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LogOutRequest {
    private String refreshToken;
}
