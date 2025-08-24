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
     * 회원가입 API 입니다.
     * @param request
     * @return
     */
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request){

        // 이메일 인증 여부 검사
        authService.assertEmailVerifiedToken(request.getEmail(), request.getEmailVerificationToken());
        authService.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of("회원가입에 성공했습니다."));
    }

    /**
     * 이메일 인증을 위한 인증 코드를 전송하는 API 입니다.
     * @param request
     * @return
     */
    @PostMapping("/email/send-code")
    public ResponseEntity<?> sendCode(@Valid @RequestBody EmailSendRequest request){

        authService.sendEmailCode(request.getEmail());

        return ResponseEntity.ok(
                SuccessResponse.of("인증코드를 전송했습니다.")
        );
    }

    /**
     * 이메일 인증 코드가 정확한지 검증하는 API 입니다.
     * @param request
     * @return
     */
    @PostMapping("/email/verify")
    public ResponseEntity<?> verify(@Valid @RequestBody EmailVerifyRequest request) {
        EmailVerifyResponse res = authService.verifyEmailCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(SuccessResponse.of(res));
    }



    /**
     * 로그인 API 입니다.
     * @param request
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){

        LoginResponse response = authService.login(request);

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
