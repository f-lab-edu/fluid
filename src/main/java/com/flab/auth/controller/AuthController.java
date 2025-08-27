package com.flab.auth.controller;

import com.flab.auth.dto.*;
import com.flab.auth.service.AuthService;
import com.flab.common.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    /**
     * 구글 계정으로 회원가입 하는 API 입니다.
     * @param request
     * @return
     */
    @PostMapping("/google/sign-up")
    public ResponseEntity<?> signUpWithGoogle(@Valid @RequestBody GoogleLoginRequest request) {
        authService.signUpWithGoogle(request.getIdToken()); // 클라이언트 -> google 로그인 -> id token 전달
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of("구글 계정으로 회원가입에 성공했습니다."));
    }

    /**
     * 구글 계정으로 로그인하는 API입니다.
     */
    @PostMapping("/google/login")
    public ResponseEntity<?> loginWithGoogle(@Valid @RequestBody GoogleLoginRequest request) {
        LoginResponse response = authService.loginWithGoogle(request.getIdToken());
        return ResponseEntity.ok(SuccessResponse.of(response));
    }


    /**
     * Access Token 만료 시, token을 갱신하는 api입니다.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    /**
     * 로그아웃 API 입니다.
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logOut(@Valid @RequestBody LogOutRequest request){

        authService.logout(request.getRefreshToken());

        return ResponseEntity.ok(
                SuccessResponse.of("정상적으로 로그아웃 되었습니다.")
        );
    }
}
