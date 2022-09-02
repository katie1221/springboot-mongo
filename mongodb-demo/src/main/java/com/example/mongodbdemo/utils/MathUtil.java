package com.example.mongodbdemo.utils;

import java.math.BigDecimal;

/**
 * 数学计算工具类
 * @author qzz
 */
public class MathUtil {


    /**
     * 两数相减，获取百分比 保留scale位小数
     * @param s1
     * @param s2
     * @param scale
     * @return
     */
    public static Double getPercentage(Double s1,Double s2,Integer scale){
        //s1 减去 s2
        BigDecimal xc =BigDecimal.valueOf(s1).subtract(BigDecimal.valueOf(s2));
        Double fwc =(xc.doubleValue()/s2)*100;
        //四舍五入 ，保留一位小数
        double perValue = BigDecimal.valueOf(fwc).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        return perValue;
    }
}
