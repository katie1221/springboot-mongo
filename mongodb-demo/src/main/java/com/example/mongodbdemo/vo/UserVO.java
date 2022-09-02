package com.example.mongodbdemo.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author qzz
 */
@Data
public class UserVO {

    private String id;
    private String name;
    private String email;
    private Integer age;
    private Date createTime;
    private Date updateTime;
    private Integer total;

}
