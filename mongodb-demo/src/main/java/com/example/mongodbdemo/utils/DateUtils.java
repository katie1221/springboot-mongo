package com.example.mongodbdemo.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

/**
 * @author qzz
 */
public class DateUtils {

    public static final String DATESHOWFORMAT = "yyyy-MM-dd";
    public static final String DATETIMESHOWFORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 字符串转时间
     */
    public static Date strToDate(String date,String formats){
        SimpleDateFormat sdf = new SimpleDateFormat(formats);
        ParsePosition parsePosition = new ParsePosition(0);
        return sdf.parse(date,parsePosition);
    }

    /**
     * 获得某天最小时间 2022-06-08 00:00:00
     */
    public static Date getStartOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获得某天最大时间  如：2022-06-08 23:59:59
     */
    public static Date getEndOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());;
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }


    /**
     * 日期格式化(Date转换为String)
     *
     * @param _date
     *            日期
     * @param patternString
     *            处理结果日期的显示格式，如："YYYY-MM-DD"
     * @return
     */
    public static String getDateString(Date _date, String patternString) {
        String dateString = "";
        if (_date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(patternString);
            dateString = formatter.format(_date);
        }
        return dateString;
    }


    /**
     * 获取指定月第一天
     *
     * @return
     */
    public static Date getFirstThisDate(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDate = ca.getTime();
        String dateString = getDateString(firstDate, DATESHOWFORMAT);
        return getDateToString(dateString, DATESHOWFORMAT);
    }

    /**
     * 获取指定时间的月份最后一天
     * @param date
     * @return
     */
    public static Date getLastThisDate(Date date)
    {
        int year = getYear(date);
        int month = getMonth(date);
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR,year);
        //设置月份
        cal.set(Calendar.MONTH, month-1);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String lastDayOfMonth = sdf.format(cal.getTime()) + " 23:59:59";
        return getDateToString(lastDayOfMonth, DATETIMESHOWFORMAT);
    }


    /**
     * 获取日期的年
     *
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        return ca.get(Calendar.YEAR);
    }

    /**
     * 获取日期的月
     *
     * @param date
     * @return
     */
    public static int getMonth(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        return ca.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取日期的日
     *
     * @param date
     * @return
     */
    public static int getDay(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        return ca.get(Calendar.DATE);
    }


    /**
     * 日期格式化(String转换为Date)
     *
     * @param dateStr
     *            日期字符串
     * @param patten
     *            处理结果日期的显示格式，如："YYYY-MM-DD"
     * @return
     */
    public static Date getDateToString(String dateStr, String patten) {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(patten, Locale.CHINA);
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 打印指定年份的第一天
     *
     * @param year 年份
     */
    public static String getFirstDateForYear(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, calendar.getActualMinimum(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }

    /**
     * 打印指定年份的最后一天
     *
     * @param year 年份
     */
    public static String getLastDateForYear(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, calendar.getActualMaximum(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return  new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }

    // 获得当前周- 周一的日期
    public static List<String> getTimeInterval(int days) {
        LocalDate begin = LocalDate.now().minusDays(days - 1 );
        List<String> list = new ArrayList();
        for (int i = 0; i < days; i++) {
            list.add(begin.toString());
            begin = begin.plusDays(1);
        }
        return list;
    }


    /**
     * 时间字符串截取 日期值
     */
    public static String strToDateValue(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition parsePosition = new ParsePosition(0);
        return sdf.format(sdf.parse(date,parsePosition));
    }
}
