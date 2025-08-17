package com.flab.design.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DesignListResponse {

    private Long designId;
    private String title;
    private String description;
    private String thumbnailUrl;

}
