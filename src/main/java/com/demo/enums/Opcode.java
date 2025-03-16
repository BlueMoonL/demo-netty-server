package com.demo.enums;

import lombok.Getter;

@Getter
public enum Opcode {

    SENSOR_INFO(0x10, "센서 정보"),
    GPS_INFO(0x11, "위치 정보");

    private final int code;
    private final String description;

    Opcode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    // 코드 값으로 Opcode 찾기
    public static Opcode fromCode(int code) {
        for (Opcode opcode : Opcode.values()) {
            if (opcode.code == code) {
                return opcode;
            }
        }
        throw new IllegalArgumentException("Unknown OPCODE: " + code);
    }
}