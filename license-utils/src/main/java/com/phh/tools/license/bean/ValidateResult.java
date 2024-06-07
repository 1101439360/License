package com.phh.tools.license.bean;

import lombok.Data;

@Data
public class ValidateResult {

    /**
     * 是否验证通过
     */
    private Boolean isValidate;

    /**
     * 验证结果状态码
     */
    private Integer code;

    /**
     * 验证结果信息
     */
    private String message;

    public static ValidateResult ok() {
        ValidateResult result = new ValidateResult();
        result.setIsValidate(true);
        result.setCode(ValidateCodeEnum.SUCCESS.getCode());
        result.setMessage(ValidateCodeEnum.SUCCESS.getMessage());
        return result;
    }

    public static ValidateResult error(ValidateCodeEnum codeEnum) {
        ValidateResult result = new ValidateResult();
        result.setIsValidate(false);
        result.setCode(codeEnum.getCode());
        result.setMessage(codeEnum.getMessage());
        return result;
    }
}
