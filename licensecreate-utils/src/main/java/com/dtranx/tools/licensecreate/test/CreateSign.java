package com.dtranx.tools.licensecreate.test;

import com.dtranx.tools.licensecreate.utils.AESUtils;
import com.dtranx.tools.licensecreate.utils.DateUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

/**
 * @author penghao
 * @Description 生成签名
 * @createDate 2022/05/05
 * @createTime 17:41
 */
public class CreateSign {

    public static void main(String[] args) {
        //系统标识---由mac地址+cpu序列号 使用AES加密而来
//        String systemSign = "1jfNqzhz66e8egvhsONUXJMDNalNPTLWDO1lLBetwck=";
        //生效起始时间
//        String generatedTimeStr = "2022-05-09 00:00:00";
        //生效截止时间
//        String expiredTimeStr = "2022-05-11 00:00:00";
        //上一次校验时间（初始值）
//        String lastValidateTimeStr = "2022-05-09 00:00:01";
        //项目部署序列号（版本标识）
//        String version = "dmoiji3xkoa4p33";
        //license生成路径
//        String path = "C:\\Users\\00066\\Desktop\\license.xml";
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入系统标识串（部署的服务获取）：");
        String systemSign = sc.nextLine();
        System.out.println("请输入生效起始时间（格式如：2022-05-05 00:00:00）：");
        String generatedTimeStr = sc.nextLine();
        System.out.println("请输入生效截止时间（格式如：2022-05-05 00:00:00）：");
        String expiredTimeStr = sc.nextLine();
        System.out.println("请输入上一次校验时间初始值（格式如：2022-05-05 00:00:00）：");
        String lastValidateTimeStr = sc.nextLine();
        System.out.println("请输入项目部署唯一版本号（不能带“-”）：");
        String version = sc.nextLine();
        System.out.println("请输入license文件生成路径：");
        String path = sc.nextLine();
        createLicense(systemSign, generatedTimeStr, expiredTimeStr, lastValidateTimeStr, version, path);
        System.out.println("license文件生成成功，文件路径：" + path);
    }

    private static void createLicense(String systemSign, String generatedTimeStr, String expiredTimeStr, String lastValidateTimeStr, String version, String path) {
        try {
            //解密系统标识得到mac地址+cpu序列号
            String macAndCpu = AESUtils.decrypt(systemSign);
            System.out.println("客户服务器mac地址和cpu序列号：" + macAndCpu);

            //MAC复制-CPU序列号-生效起始时间-生效结束结束时间-软件产品序列号（项目版本唯一标识）
//            String content = "A8:A1:59:41:89:36-BFEBFBFF000906EA-20220506-20220507-dmoiji3xkoa4p33";
            StringBuilder signBuilder = new StringBuilder(macAndCpu);
            //生效起始时间
            long generatedTime = DateUtils.getTimeInMillis(generatedTimeStr);
            //生效截止时间
            long expiredTime = DateUtils.getTimeInMillis(expiredTimeStr);
            //项目唯一标识
            signBuilder.append("-").append(generatedTime).append("-").append(expiredTime).append("-").append(version);

            String sign = AESUtils.encrypt(signBuilder.toString());
            System.out.println("AES加密生成签名：");
            System.out.println("-----------------------------------------------------------------------------------------------");
            System.out.println(sign);
            System.out.println("-----------------------------------------------------------------------------------------------");

            //生成licence文件
            Document document = DocumentHelper.createDocument();
            //根节点
            Element rootEle = document.addElement("license");
            //功能数据节点,扩展参数时可在此节点下扩展
            Element dataEle = rootEle.addElement("features");
            Element featureEle = dataEle.addElement("feature");
            featureEle.addAttribute("name", "lastValidateTi");
            featureEle.addAttribute("ti", AESUtils.encrypt(String.valueOf(DateUtils.getTimeInMillis(lastValidateTimeStr))));
            //签名节点
            Element signEle = rootEle.addElement("signature");
            signEle.setText(sign);
            System.out.println(document.asXML());
            OutputFormat format = OutputFormat.createPrettyPrint();
            // 设置编码格式
            format.setEncoding("UTF-8");
            FileWriter fileWriter = new FileWriter(new File(path));
            XMLWriter xmlWriter = new XMLWriter(fileWriter, format);
            // 设置是否转义，默认使用转义字符
            xmlWriter.setEscapeText(false);
            xmlWriter.write(document);
            xmlWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
