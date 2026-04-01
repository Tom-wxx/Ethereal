package com.ethereal.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatusEnum {

    NORMAL(1, "正常"),
    BANNED(2, "封禁");

    private final int code;
    private final String desc;
}
