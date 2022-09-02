package com.example.mongodbdemo.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * @author qzz
 */
public class DateUtilsTest {

    public static void main(String[] args) {

        //1.字符串 转 时间
        String time ="2022-06-09 15:53:00";
        Date date = DateUtils.strToDate(time,"yyyy-MM-dd HH:mm:ss");
        System.out.println("字符串 转 时间:"+date);

        //2.时间 转 字符串
        Date now = new Date();
        String nowStr = DateUtils.getDateString(now,"yyyy-MM-dd");
        System.out.println("时间 转 字符串:"+nowStr);

        //3.指定月的第一天、最后一天
        String t1= DateUtils.getDateString(DateUtils.getFirstThisDate(now),"yyyy-MM-dd");
        String t2= DateUtils.getDateString(DateUtils.getLastThisDate(now),"yyyy-MM-dd");
        System.out.println("获取指定月第一天为：" + t1);
        System.out.println("获取指定月最后一天为：" + t2);

        //4.指定年的第一天、最后一天
        String t11= DateUtils.getFirstDateForYear(2022);
        String t21= DateUtils.getLastDateForYear(2022);
        System.out.println("获取指定年第一天为：" + t11);
        System.out.println("获取指定年最后一天为：" + t21);

        //近30天
        LocalDateTime beginTime;
        LocalDateTime endTime;
        List<String> timeIntervalList;

        beginTime = LocalDateTime.of(LocalDateTime.now().minusDays(29).toLocalDate(), LocalTime.MIN).plusHours(8);
        endTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MAX).plusHours(8);
        timeIntervalList = DateUtils.getTimeInterval(30);

        System.out.println("近30天的第一天:"+beginTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        System.out.println("近30天的最后一天:"+endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//        timeIntervalList.forEach(t->{
//            System.out.println(t);
//        });

        //近7天
        beginTime = LocalDateTime.of(LocalDateTime.now().minusDays(6).toLocalDate(), LocalTime.MIN).plusHours(8);
        endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).plusHours(8);
        timeIntervalList = DateUtils.getTimeInterval(7);

        System.out.println("近7天的第一天:"+beginTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        System.out.println("近7天的最后一天:"+endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

    }
}
