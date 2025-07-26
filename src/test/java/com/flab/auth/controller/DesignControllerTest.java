package com.flab.auth.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.FluidApplication;
import com.flab.auth.dto.LoginRequest;
import com.flab.design.dto.CreateDesignRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FluidApplication.class)
@AutoConfigureMockMvc
public class DesignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("디자인 생성 성공 테스트")
    void loginSuccess() throws Exception {
        //given
        CreateDesignRequest request = new CreateDesignRequest(
                "제목",
                "간단한 설명",
                "{ 디자인 JSON }",
                "https://lms.f-lab.kr/course-select");

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/designs/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.designId").value("12345"));


    }


}
