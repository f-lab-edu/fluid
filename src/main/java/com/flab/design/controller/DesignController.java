package com.flab.design.controller;

import com.flab.common.response.SuccessResponse;
import com.flab.design.dto.CreateDesignRequest;
import com.flab.design.dto.CreateDesignResponse;
import com.flab.design.dto.DesignListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/designs")
public class DesignController {

    /**
     * 디자인 생성 API 입니다.
     *
     * @param request
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<?> createDesign(@RequestBody CreateDesignRequest request){

        CreateDesignResponse response = new CreateDesignResponse(12345L);

        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    /**
     * 게시된 디자인 목록을 페이징 처리하여 반환하는 API 입니다.
     */
    @GetMapping("/list")
    public ResponseEntity<?> getDesignList(Pageable pageable){

        List<DesignListResponse> dummyList = List.of(
                new DesignListResponse(1L, "디자인1", "설명1", "thumb1.png"),
                new DesignListResponse(2L, "디자인2", "설명2", "thumb2.png")
        );

        Page<DesignListResponse> response = new PageImpl<>(dummyList, pageable, dummyList.size());

        return ResponseEntity.ok(SuccessResponse.of(response));
    }
}
