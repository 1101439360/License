package com.phh.tools.license.utils;

import org.springframework.stereotype.Component;

@Component
public class MySystemUtils extends SystemUtils {
    //todo 可以重写param1和param2的参数内容

    //可以继续添加控制的参数获取方法
    // todo 自定义控制参数
    protected String getParam2() {
        return "param3";
    }
}
