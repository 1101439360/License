package com.dtranx.tools.license.bean;

import lombok.Builder;
import lombok.Data;

/**
 * @author penghao
 * @createDate 2022/05/10
 * @createTime 10:05
 */
@Data
@Builder
public class CheckParams {

    private long lastValidateTime;

    private String macAddress;

    private String cpuSerial;

    private Long generatedTime;

    private Long expiredTime;

    private String version;

}
