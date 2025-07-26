package com.flab.design.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateDesignRequest {

    private String title; // 제목
    private String description; // 설명
    private String designJson; // 디자인 결과를 Json으로 받기
    private String thumbnailUrl; // 썸네일 이미지
}
