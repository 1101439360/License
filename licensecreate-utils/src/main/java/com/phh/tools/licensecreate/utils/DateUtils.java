package com.phh.tools.licensecreate.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author penghao
 * @Description TODO
 * @createDate 2022/05/09
 * @createTime 13:27
 */
public class DateUtils {

    public static Long getTimeInMillis(String formatTime){
        try {
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date=format.parse(formatTime);
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(date);
            return calendar.getTimeInMillis();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("日期转换失败");
        }
        return 0L;
    }
}
