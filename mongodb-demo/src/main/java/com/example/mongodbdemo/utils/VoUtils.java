package com.example.mongodbdemo.utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 对象属性复制
 * @author qzz
 */
public class VoUtils {

    /**
     * 把 一个对象的属性值  赋值给 另一个对象的属性值
     * @param source
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T copyProperties(Object source, T target) {
        Field[] targetFields = getAllFields(target);
        Field[] sourceFields = getAllFields(source);
        for (Field field : sourceFields) {
            String fieldFirstUpcase = Utils.capitalFor(field.getName());
            String getMethod = "get" + fieldFirstUpcase;
            String setMethod = "set" + fieldFirstUpcase;
            if (StringUtils.isNotEmpty(field.getName()) && "serialVersionUID".equals(field.getName())) {
                continue;
            }
            try {
                Object value = source.getClass().getMethod(getMethod).invoke(source);
                List<String> fieldNameList = Arrays.stream(targetFields).map(Field::getName).distinct().collect(Collectors.toList());
                if (fieldNameList.contains(field.getName()) && value != null) {
                    target.getClass().getMethod(setMethod, value instanceof List ? List.class : value.getClass()).invoke(target, value);
                }
            } catch (Exception e) {
            }
        }
        return target;
    }

    public static Field[] getAllFields(Object object) {
        Class clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }
}
