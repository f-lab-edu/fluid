package com.flab.auth.controller;

import com.flab.auth.dto.*;
import com.flab.common.response.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    /**
     * 로그인 API 입니다.
     * @param request
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){

        LoginResponse response = new LoginResponse(
                "dummy-access-token",
                "dummy-refresh-token",
                "로그인에 성공했습니다."
        );

        return ResponseEntity.ok(SuccessResponse.of(response));

    }

    /**
     * 회원가입 API 입니다.
     * @param request
     * @return
     */
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest request){

        return ResponseEntity.ok(
                SuccessResponse.of("회원가입에 성공했습니다.")
        );
    }

    /**
     * 로그아웃 API 입니다.
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logOut(@RequestBody LogOutRequest request){

        return ResponseEntity.ok(
                SuccessResponse.of("정상적으로 로그아웃 되었습니다.")
        );
    }

    /**
     * accessToken을 갱신하는 API입니다.
     * @param request
     * @return
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request){

        LoginResponse response = new LoginResponse(
                "new-access-token",
                "new-refresh-token",
                "accessToken이 갱신되었습니다."
        );

        return ResponseEntity.ok(SuccessResponse.of(response));
    }
}
