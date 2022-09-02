package com.example.mongodbdemo.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.Date;

/**
 * @Document用于指定数据库的conllectionjav
 * @field用于指定数据库字段spring
 * @id用于标识主键mongodb
 * @GeneratedValue 自动生成id数据
 * @author qzz
 */
@Data
@Document("User")
public class User{

    /**
     * 主键标识,该属性的值会自动对应，mongodb的主键字段”_id“,如果该属性名就叫”id“,则该注解可以省略
     */
    @Id
    private String id;
    /**
     * 该属性对应 mongodb的字段的名字，如果一致，则无需该注解
     */
    @Field("name")
    private String name;
    private String email;
    private Integer age;
    @Field("create_time")
    private Date createTime;
    @Field("update_time")
    private Date updateTime;
}


