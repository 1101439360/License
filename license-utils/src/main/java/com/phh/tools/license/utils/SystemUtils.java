package com.phh.tools.license.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * @author penghao
 */
@Slf4j
public abstract class SystemUtils {

    private static final String PROCESSORID_STR = "ProcessorId";

    //todo 模拟控制参数1
    protected String getParam0() {
        return "22468D77E6A3";
    }

    //cpu 序列号
    protected String getParam1() {
        BufferedReader reader = null;
        InputStreamReader ir = null;
        try {
            String[] linux = {"/bin/bash", "-c", "dmidecode -t processor | grep 'ID' | awk -F ':' '{print $2}' | head -n 1"};
            String[] windows = {"wmic", "cpu", "get", "ProcessorId"};

            // 获取系统信息
            String property = System.getProperty("os.name");
            Process process = Runtime.getRuntime().exec(property.contains("Window") ? windows : linux);
            process.getOutputStream().close();
            ir = new InputStreamReader(process.getInputStream());
            reader = new BufferedReader(ir);
            // 各个系统的命令结果，可能结构不一致，可以在实际服务器上执行对应命令，根据实际结果进行调试修改取值
            String xxw;
            while ((xxw = reader.readLine()) != null) {
                xxw = xxw.replaceAll(" ", "");
                if (!StringUtils.isEmpty(xxw) && !xxw.equalsIgnoreCase(PROCESSORID_STR)) {
                    break;
                }
            }
            log.info("xxw识别码:{}", xxw);
            return xxw;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取系统信息失败！");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ir != null) {
                try {
                    ir.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
