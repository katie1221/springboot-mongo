package com.example.mongodbdemo.utils;

import lombok.Data;

/**
 * 返回结果集封装
 * @author qzz
 */
@Data
public class Result<T> {

    /**
     * 响应码
     */
    private Integer code;
    /**
     * 响应信息
     */
    private String message;
    /**
     * 返回值
     */
    private T data;

    public static Result success(Object data){
        if(data == null){
            return success();
        }
        return new Result(200,"操作成功",data);
    }

    public static Result success() {
        return new Result(200,"操作成功",null);
    }

    public Result(Integer code, String message, T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    public static Result fail() {
        return new Result(400,"操作失败",null);
    }

    public static Result fail(Object data) {
        return new Result(400,"操作失败",data);
    }

    public static Result fail(String message) {
        return new Result(400,message);
    }

    public static Result fail(Integer code, String message,Object data) {
        return new Result(code,message,data);
    }
}
