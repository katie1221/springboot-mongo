package com.example.mongodbdemo.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author qzz
 */
public class Utils {

    /**
     * 首字母大写
     *
     * @param name 指定替换字符串
     * @return 范例：
     */
    public static String capitalFor(String name) {
        if (StringUtils.isBlank(name)) {
            return name;
        }
        if (name.length() == 1) {
            name = name.toUpperCase();
        } else {
            String str = name.substring(0, 2);
            if (Character.isLowerCase(str.charAt(0))) {
                // 第一个是否为小写
                if (Character.isUpperCase(str.charAt(1))) {
                    // 第二个是否为大写
                    return name;
                } else {
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                }
            }
        }
        return name;
    }


    /**
     * 转换  把 272627 转换成  2.7-2.6-2.7
     * @param fgpfs
     * @return
     */
    public String getNewFgpfs(String fgpfs){
        if(StringUtils.isEmpty(fgpfs)){
            return "";
        }
        //转换
        int num = fgpfs.length()/2;
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < num; i++) {
            int j = 2 * i;
            String substring = fgpfs.substring(j, j + 2);
            //拆分每个两位数
            String str=(Integer.parseInt(substring)/10)+"."+(Integer.parseInt(substring)%10);
            temp.append(str).append("-");
        }
        return temp.substring(0, temp.lastIndexOf("-"));
    }
}
