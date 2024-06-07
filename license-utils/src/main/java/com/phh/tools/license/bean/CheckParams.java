package com.phh.tools.license.bean;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author penghao
 * @createDate 2022/05/10
 * @createTime 10:05
 */
@Data
@Builder
public class CheckParams {

    private long lastValidateTime;

   private List<String> customParams;

    private Long generatedTime;

    private Long expiredTime;

    private String version;

}
