package com.example.mongodbdemo.vo;

import lombok.Data;

/**
 * @author qzz
 */
@Data
public class ProductStatisticsVO {

    /**
     * 分组名称
     */
    private String category;
    /**
     * 销售数量
     */
    private String saleNumSum;
    /**
     * 个数
     */
    private Integer total;
}
