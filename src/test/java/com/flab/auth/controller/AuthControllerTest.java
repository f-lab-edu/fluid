package com.flab.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.FluidApplication;
import com.flab.auth.dto.LogOutRequest;
import com.flab.auth.dto.LoginRequest;
import com.flab.auth.dto.SignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.flab.common.response.ErrorCode.LOGIN_FAILED;
import static com.flab.common.response.ErrorCode.USER_NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FluidApplication.class)// 모든 빈 로드, 내장 was 띄우기
@AutoConfigureMockMvc //MocMVC 서버를 자동으로 생성, 설정
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginSuccess() throws Exception {
        //given
        LoginRequest request = new LoginRequest("user@flab.com", "password123");

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("dummy-access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("dummy-refresh-token"))
                .andExpect(jsonPath("$.data.message").value("로그인에 성공했습니다."));


    }

    @Test
    @DisplayName("로그인 실패 테스트 - 존재하지 않는 아이디")
    void loginFail_userNotFound() throws Exception {
        //given
        LoginRequest request = new LoginRequest("fail@fail.com", "password123");

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorDetail.errorCode").value(USER_NOT_FOUND.getError()))
                .andExpect(jsonPath("$.errorDetail.errorMessage").value(USER_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 비밀번호 불일치")
    void loginFail_wrongPassword() throws Exception {
        //given
        LoginRequest request = new LoginRequest("user@flab.com", "wrongpassword");

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        //then
        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorDetail.errorCode").value(LOGIN_FAILED.getError()))
                .andExpect(jsonPath("$.errorDetail.errorMessage").value(LOGIN_FAILED.getMessage()));
    }


    @Test
    @DisplayName("회원가입 성공 테스트")
    void signUpSuccess() throws Exception {
        //given
        SignUpRequest request = new SignUpRequest("user@flab.com", "최윤하", "password1234");

        //when
        ResultActions resultActions = mockMvc.perform(
                post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("회원가입에 성공했습니다."));
    }



    @Test
    @DisplayName("로그아웃 성공 테스트")
    void logoutSuccess() throws Exception {
        // given
        LogOutRequest request = new LogOutRequest("dummy-refresh-token");

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/auth/log-out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("정상적으로 로그아웃 되었습니다."));
    }


}
