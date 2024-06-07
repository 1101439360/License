package com.phh.tools.license.utils;


import com.phh.tools.license.bean.CheckParams;
import com.phh.tools.license.bean.ValidateCodeEnum;
import com.phh.tools.license.bean.ValidateResult;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * @author PH
 */
@Slf4j
@Component
public class LicenseManager {

    @Autowired
    MySystemUtils mySystemUtils;

    public Map<String, ValidateResult> validate() {
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

        //校验自定义参数
        checkCustomParams(result, checkParams);
        if (result.size() > 1) {
            return result;
        }

        //校验有效期限
        long currentTi = System.currentTimeMillis();
        if (notAfterLastValidateTime(checkParams.getLastValidateTime(), currentTi) || notAfter(checkParams.getGeneratedTime(), currentTi)
                || notBefore(checkParams.getExpiredTime(), currentTi)) {
            result.put("Authorize", ValidateResult.error(ValidateCodeEnum.EXPIRED));
            return result;
        }

        result.put("Authorize", ValidateResult.ok());
        return result;
    }

    private void checkCustomParams(Map<String, ValidateResult> result, CheckParams checkParams) {
        //校验mac地址
        List<String> customParams = checkParams.getCustomParams();
        if (!customParams.get(0).equals(mySystemUtils.getParam0())) {
            result.put("Authorize", ValidateResult.error(ValidateCodeEnum.UNAUTHORIZED));
            return;
        }
        //校验cpu序列号
        if (!customParams.get(1).equals(mySystemUtils.getParam1())) {
            result.put("Authorize", ValidateResult.error(ValidateCodeEnum.UNAUTHORIZED));
            return;
        }
        //todo 校验自定义参数
        //if (!customParams.get(2).equals(mySystemUtils.getParam2())) {
        //            result.put("Authorize", ValidateResult.error(ValidateCodeEnum.UNAUTHORIZED));
        //            return result;
        //        }
    }

    public String getSystemSign() {
        //control param 0
        String param0 = mySystemUtils.getParam0();
        //control param 1
        String param1 = mySystemUtils.getParam1();
        //todo custom control param
//        return AESUtils.encrypt(macAddress + "-" + cpuNum+ "-" + "cutomParam");
        return AESUtils.encrypt(param0 + "-" + param1);
    }


    public void updateSign(String sign) {
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
            //更新检查结果
            validateAfterUpdateSign();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("更新授权码失败！");
        }

    }

    public void validateAfterUpdateSign() {
        LicenseThread.validateResult = validate();
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


    private CheckParams getCheckParams(Map<String, ValidateResult> result) {
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
        long lastValidateTime = Long.parseLong(Objects.requireNonNull(AESUtils.decrypt(lastValidateTimeStr)));
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
        //前三个参数：生成时间、失效时间、版本号 再加上至少一个自定义控制参数
        if (signArr.length < 4) {
            log.error("授权码不正确");
            result.put("Authorize", ValidateResult.error(ValidateCodeEnum.ILLEGAL));
            return null;
        }

        //自定义控制参数
        List<String> customParams = new ArrayList<>(
                Arrays.asList(
                        Arrays.copyOfRange(signArr, 3, signArr.length - 1)
                )
        );
        return CheckParams.builder().lastValidateTime(lastValidateTime).generatedTime(Long.parseLong(signArr[0]))
                .expiredTime(Long.parseLong(signArr[1])).version(signArr[2]).customParams(customParams).build();
    }

    private Document readLicense() {
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
}
