package com.flab.auth.controller;

import com.flab.auth.dto.LogOutRequest;
import com.flab.auth.dto.LoginRequest;
import com.flab.auth.dto.LoginResponse;
import com.flab.auth.dto.SignUpRequest;
import com.flab.common.response.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
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
     * @param refreshToken
     * @return
     */
    @PostMapping("/log-out")
    public ResponseEntity<?> logOut(@RequestBody LogOutRequest refreshToken){

        return ResponseEntity.ok(
                SuccessResponse.of("정상적으로 로그아웃 되었습니다.")
        );
    }
}
