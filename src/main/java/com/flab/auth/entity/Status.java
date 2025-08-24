package com.flab.auth.entity;

import lombok.Getter;

@Getter
public enum Status {

    ACTIVE("정상"),
    BLOCKED("이용 제한"),
    DELETED("탈퇴");

    private final String description;

    Status(String description){
        this.description = description;
    }

    public boolean isActive()  { return this == ACTIVE; }
    public boolean isBlocked() { return this == BLOCKED; }
    public boolean isDeleted() { return this == DELETED; }

    public boolean canLogin() {
        return this == ACTIVE;
    }
}
