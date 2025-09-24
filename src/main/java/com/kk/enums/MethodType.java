package com.kk.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum MethodType {
    PHONE(0, "phone"),
    GOOGLE(1, "google"),
    GITHUB(2, "github");

    @EnumValue
    private final int code;
    @JsonValue
    private final String desc;

    MethodType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
