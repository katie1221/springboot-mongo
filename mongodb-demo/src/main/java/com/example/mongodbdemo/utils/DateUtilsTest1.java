package com.example.mongodbdemo.utils;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author qzz
 */
public class DateUtilsTest1 {

    public static void main(String[] args) {
        Date now = new Date();

        LocalDateTime localDateTime = LocalDateTime.now();

        //本日 开始时间 2022-06-08 00:00:00
        String begin = DateUtils.getDateString(DateUtils.getStartOfDay(now),"yyyy-MM-dd HH:mm:ss");
        //本日 结束时间 2022-06-08 23:59:59
        String end = DateUtils.getDateString(DateUtils.getEndOfDay(now),"yyyy-MM-dd HH:mm:ss");
        System.out.println("本日 开始时间:"+begin);
        System.out.println("本日 结束时间:"+end);

        //本月 第一天  如：2022-06-01
        String startTime11= DateUtils.getDateString(DateUtils.getFirstThisDate(now),"yyyy-MM-dd HH:mm:ss");
        //本月 最后一天  如：2022-06-30
        String endTime11= DateUtils.getDateString(DateUtils.getLastThisDate(now),"yyyy-MM-dd HH:mm:ss");
        System.out.println("本月 开始时间:"+startTime11);
        System.out.println("本月 结束时间:"+endTime11);

        //本年 开始时间 如：2022-01-01 00:00:00
        String startTime1= DateUtils.getFirstDateForYear(localDateTime.getYear())+" 00:00:00";
        //本年 结束时间 如：2022-12-31 23:59:59
        String endTime1= DateUtils.getLastDateForYear(localDateTime.getYear())+" 23:59:59";
        System.out.println("本年 开始时间:"+startTime1);
        System.out.println("本年 结束时间:"+endTime1);

    }
}
