package com.dtranx.tools.license.utils;


import lombok.extern.slf4j.Slf4j;

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
public class Systemutils {

    protected static String getMacAddress() {
        try {
            java.util.Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            StringBuilder sb = new StringBuilder();
            while (en.hasMoreElements()) {
                NetworkInterface iface = en.nextElement();
                List<InterfaceAddress> addrs = iface.getInterfaceAddresses();
                for (InterfaceAddress addr : addrs) {
                    InetAddress ip = addr.getAddress();
                    NetworkInterface network = NetworkInterface.getByInetAddress(ip);
                    if (network == null) {
                        continue;
                    }
                    if (network.getName().toLowerCase().startsWith("ens")) {
                        byte[] mac = network.getHardwareAddress();
                        if (mac == null) {
                            continue;
                        }
                        for (int i = 0; i < mac.length; i++) {
                            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                        }
                        String xxy = sb.toString().replaceAll("-", "").toUpperCase();
                        log.info("xxy地址：{}", xxy);
                        return xxy;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("读取本机系统信息失败！");
        }
        return null;
    }

    protected static String getCpuNum() {
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
            String xxw = reader.readLine();
            if (xxw != null) {
                xxw = xxw.replaceAll(" ", "");
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

    public static void main(String[] args) throws Exception {
//        List<String> macs = getMacAddress();
//        System.out.println("本机的mac网卡的地址列表" + macs);
        System.out.println(getCpuNum());
    }
}
