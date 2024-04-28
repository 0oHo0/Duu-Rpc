package com.duu.duurpc.protocol;

import lombok.Getter;

/**
 * @author : duu
 * @data : 2024/3/25
 * @from ：https://github.com/0oHo0
 **/
@Getter
public enum ProtocolMessageStatusEnum {
    OK("ok", 20),
    BAD_REQUEST("badRequest", 40),
    BAD_RESPONSE("badResponse", 50);

    private final String text;

    private final int value;

    ProtocolMessageStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * @description: 获取枚举
     * @author: duu
     * @date: 2024/3/25 13:58
     * @param: value
     * @return: ProtocolMessageStatusEnum
     **/
    public static ProtocolMessageStatusEnum getEnumByValue(int value) {
        for (ProtocolMessageStatusEnum anEnum : ProtocolMessageStatusEnum.values()) {
            if (anEnum.value == value) {
                return anEnum;
            }
        }
        return null;
    }
}
