package com.example.mongodbdemo.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * 商品集合
 * @author qzz
 */
@Data
@Document("product")
public class Product {

    /**
     * 主键标识,该属性的值会自动对应，mongodb的主键字段”_id“,如果该属性名就叫”id“,则该注解可以省略
     */
    @Id
    private String id;
    /**
     * 该属性对应 mongodb的字段的名字，如果一致，则无需该注解
     */
    @Field("title")
    private String title;
    /**
     * 价格
     */
    private Double price;
    /**
     * 分类名称
     */
    private String category;
    /**
     * 规格
     */
    private String specification;
    /**
     * 销售数量
     */
    @Field("sale_num")
    private Integer saleNum;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 内容
     */
    private String remark;

    /**
     * 创建时间
     */
    @Field("create_time")
    private String createTime;
    /**
     * 更新时间
     */
    @Field("update_time")
    private String updateTime;

    /**
     * 状态 1：上架  2：下架
     */
    private Integer status;

    private String startTime;
    private String endTime;
}


