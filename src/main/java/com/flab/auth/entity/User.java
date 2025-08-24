package com.flab.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원 정보 테이블을 정의한 entity 입니다.
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // pk

    private String email; // 이메일
    private String nickname; // 닉네임
    private String password; // 비밀번호
    private String role; // 회원 역할

    @Enumerated(EnumType.STRING)
    private Status status; // 계정 상태

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 시각

    @Column(insertable = false, updatable = false)
    private LocalDateTime updatedAt; // 수정 시각
}
