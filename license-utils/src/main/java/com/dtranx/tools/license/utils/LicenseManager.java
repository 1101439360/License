package com.dtranx.tools.license.utils;


import com.dtranx.tools.license.bean.CheckParams;
import com.dtranx.tools.license.bean.ValidateCodeEnum;
import com.dtranx.tools.license.bean.ValidateResult;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author PH
 */
@Slf4j
@Component
public class LicenseManager {

    public static Map<String, ValidateResult> validate() {
        Map<String, ValidateResult> result = new HashMap<>();
        CheckParams checkParams = null;
        try {
            checkParams = getCheckParams(result);
            if (checkParams == null) {
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("Authorize", ValidateResult.error(ValidateCodeEnum.EXCEPTION));
            return result;
        }

        //校验mac地址
        if (!checkParams.getMacAddress().equals(Systemutils.getMacAddress())) {
            result.put("Authorize", ValidateResult.error(ValidateCodeEnum.UNAUTHORIZED));
            return result;
        }
        //校验cpu序列号
        if (!checkParams.getCpuSerial().equals(Systemutils.getCpuNum())) {
            result.put("Authorize", ValidateResult.error(ValidateCodeEnum.UNAUTHORIZED));
            return result;
        }
        long currentTi = System.currentTimeMillis();
        //校验时间
        if (notAfterLastValidateTime(checkParams.getLastValidateTime(), currentTi) || notAfter(checkParams.getGeneratedTime(), currentTi)
                || notBefore(checkParams.getExpiredTime(), currentTi)) {
            result.put("Authorize", ValidateResult.error(ValidateCodeEnum.EXPIRED));
            return result;
        }

        result.put("Authorize", ValidateResult.ok());
        return result;
    }

    public static String getSystemSign() {
        String MacAddress = Systemutils.getMacAddress();
        String cpuNum = Systemutils.getCpuNum();
        return AESUtils.encrypt(MacAddress + "-" + cpuNum);
    }


    public static void updateSign(String sign) {
        try {
            Document document = readLicense();
            Element rootElement = document.getRootElement();
            Element signatureEle = rootElement.element("signature");
            signatureEle.setText(sign);
            OutputFormat format = OutputFormat.createPrettyPrint();
            // 设置编码格式
            format.setEncoding("UTF-8");
            String path = System.getProperty("user.dir");
            FileWriter fileWriter = new FileWriter(new File(path + File.separator + "license.xml"));
            XMLWriter xmlWriter = new XMLWriter(fileWriter, format);
            // 设置是否转义，默认使用转义字符
            xmlWriter.setEscapeText(false);
            xmlWriter.write(document);
            xmlWriter.close();
            log.info("更新授权码成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("更新授权码失败！");
        }

    }

    private static boolean notAfterLastValidateTime(long lastValidateTime, long currentTi) {
        return lastValidateTime >= currentTi;
    }

    private static boolean notBefore(Long expiredTime, long currentTi) {
        return expiredTime <= currentTi;
    }

    private static boolean notAfter(long generatedTime, long currentTi) {
        return generatedTime >= currentTi;
    }


    private static CheckParams getCheckParams(Map<String, ValidateResult> result) {
        //读取license文件
        Document document = readLicense();
        if (document == null) {
            log.error("license 读取失败！");
            result.put("Authorize", ValidateResult.error(ValidateCodeEnum.FILE_NOT_EXIST));
            return null;
        }
        Element rootElement = document.getRootElement();
        Element dataEle = rootElement.element("features");
        List<Element> featuresEles = dataEle.elements();
        Element lastValidateTimeEle = featuresEles.get(0);
        //提取上一次验证时间
        String lastValidateTimeStr = lastValidateTimeEle.attributeValue("ti");
        long lastValidateTime = Long.parseLong(AESUtils.decrypt(lastValidateTimeStr));
        log.debug("上一次校验时间：{}", lastValidateTime);
        //提取签名内容
        Element signEle = rootElement.element("signature");
        String signStr = signEle.getText();
        String sign = AESUtils.decrypt(signStr);
        if (sign == null) {
            log.error("授权码不正确");
            result.put("Authorize", ValidateResult.error(ValidateCodeEnum.ILLEGAL));
            return null;
        }
        log.debug("签名内容：{}", sign);
        String[] signArr = sign.split("-");
        if (signArr.length != 5) {
            log.error("授权码不正确");
            result.put("Authorize", ValidateResult.error(ValidateCodeEnum.ILLEGAL));
            return null;
        }

        CheckParams params = CheckParams.builder().lastValidateTime(lastValidateTime).macAddress(signArr[0])
                .cpuSerial(signArr[1]).generatedTime(Long.parseLong(signArr[2])).expiredTime(Long.parseLong(signArr[3]))
                .version(signArr[4]).build();
        return params;
    }

    private static Document readLicense() {
        Document document = null;
        try {
            SAXReader saxReader = new SAXReader();
            String path = System.getProperty("user.dir");
            document = saxReader.read(new File(path + File.separator + "license.xml"));
            return document;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
//        String sign = AESUtils.decrypt("VorZodH/B6eeNLPA09TNJ8fpjlvrsckBk3VW3Pvr2qzhQVdeL38xS8unNFFxzQrjZ70f4wIoi1Tg1wlZq9DFKuVyp2zD20A//lDswyaD8NsmwMR72R2Ua+Gb0dp+PpM3b9gx2iIFIAtKOyaJlMMV8H4az/EKc/d733lyHfY3wbhsmo4vUvsqPYiriaj+psPu7DgO0DsQqw0xjAblpcrfL1xc42E3STEi9NTNbbBTsLU=");

        String s="HPdW5CR3bRzVGEDMkZtsfQMHbcJ6SabTLJqdNsvJ7aU=";
        System.out.println(AESUtils.decrypt(s));
    }
}
