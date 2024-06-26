package com.phh.tools.license.utils;

import com.phh.tools.license.bean.ValidateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author penghao
 * @createDate 2022/05/11
 * @createTime 16:42
 */
@Component
@Slf4j
public class LicenseThread implements Runnable {

    @Autowired
    LicenseManager licenseManager;

    public static Map<String, ValidateResult> validateResult = null;

    @Value("${xxy.checkTime}")
    private Long checkTime;

    @Bean
    public void startThread() {
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            validateResult = licenseManager.validate();
            if (validateResult != null) {
                ValidateResult result = validateResult.get("Authorize");
                log.debug("license校验结果：" + result.getMessage());
            }
            try {
                //todo 正式改为12个小时校验一次，保持与登录同步即可
//                TimeUnit.HOURS.sleep(12);
                //测试1分钟校验一次
                TimeUnit.SECONDS.sleep(checkTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
