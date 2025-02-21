package com.golmok.golmokstar.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RequestStatus {
    PENDING, ACCEPTED, REJECTED;

    // JSON으로 RequestStatus를 받는 경우, 대소문자 자동 변환이 불가능함.
    // @JsonCreator을 통해 Jackson이 메서드를 ENUM 변환 시 사용하게 한다.
    // ENUM 객체가 유효성 검증 로직의 책임을 가져도 되는지 고민해 봐야 함.
    @JsonCreator
    public static RequestStatus fromString(String value) {
        for (RequestStatus status : RequestStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 요청 상태입니다. (accepted/rejected만 허용)");
    }
}