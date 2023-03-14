package com.dtranx.tools.license.bean;

import lombok.Getter;

@Getter
public enum ValidateCodeEnum {

    SUCCESS(200, "验证通过"),
    EXPIRED(1101, "授权已过期"),
    ILLEGAL(1102, "授权码不正确"),
    EXCEPTION(1103, "解析签名异常"),
    FILE_NOT_EXIST(1104, "license文件不存在"),
    UNAUTHORIZED(1105, "产品未授权");

    private Integer code;
    private String message;

    ValidateCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
