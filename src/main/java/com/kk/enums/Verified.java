package com.kk.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Verified {
    UNVERIFIED(0, "未验证"),
    VERIFIED(1, "已验证");

    @EnumValue
    private final int code;
    @JsonValue
    private final String desc;

    Verified(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
