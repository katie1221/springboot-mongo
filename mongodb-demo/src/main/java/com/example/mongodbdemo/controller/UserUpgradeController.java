package com.example.mongodbdemo.controller;

import com.example.mongodbdemo.entity.Product;
import com.example.mongodbdemo.utils.DateUtils;
import com.example.mongodbdemo.vo.ProductStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 用户管理
 * @author qzz
 */
@RestController
@RequestMapping("/userUpgrade")
public class UserUpgradeController {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private MongoOperations mongoOperations;


    /****************************************查看*********************************/
    /**
     * 条件查询、组合排序、取一条数据
     * @param name
     * @param category
     * @return
     */
    @RequestMapping("/findOneInfo")
    public Product findOneInfo(@RequestParam("title") String name,@RequestParam("category")String category){
        //从mongodb里获取最新的一条记录
        Criteria criteria = Criteria.where("title").regex(".*?" + name + ".*?")
                .and("category").is(category);
        Query query = Query.query(criteria);
        //排序规则 根据分类倒叙，价格降序
        query.with(Sort.by(
                Sort.Order.desc("category"),
                Sort.Order.desc("price")
        ));
        query.limit(1);
        Product product=mongoTemplate.findOne(query,Product.class);
        return product;
    }

    /**
     * 查询结果 按分类名称 分组显示(单字段分组)
     * @param name
     * @return
     */
    @RequestMapping("/findInfoByCategory")
    public Map<String, List<Product>> findInfoByCategory(){
        Criteria criteria = new Criteria();
        Query query = Query.query(criteria);
        //排序规则 根据分类倒叙，排序升序
        query.with(Sort.by(
                Sort.Order.desc("category"),
                Sort.Order.asc("sort")
        ));
        List<Product> list=mongoTemplate.find(query,Product.class);
        //按分类名称 分组显示
        Map<String,List<Product>> listMap = list.stream().collect(Collectors.groupingBy(x->x.getCategory()));
        return listMap;
    }

    /**
     * 查询结果 按分类名称、规格 分组显示（多字段分组--- 组合成一个字段）
     * @return
     */
    @RequestMapping("/findInfoByMultiplyCategory")
    public Map<String, List<Product>> findInfoByMultiplyCategory(){
        Criteria criteria = new Criteria();
        Query query = Query.query(criteria);
        //排序规则 根据分类倒叙，排序升序
        query.with(Sort.by(
                Sort.Order.desc("category"),
                Sort.Order.asc("sort")
        ));
        List<Product> list=mongoTemplate.find(query,Product.class);
        //按分类名称和规格 分组显示
        Map<String,List<Product>> listMap = list.stream().collect(Collectors.groupingBy(item->item.getCategory()+"-"+item.getSpecification()));
        return listMap;
    }
    /**
     * 若已知 分组结果是 一对一，可以使用如下 方式：
     * Map<String, Product> productMap = list.stream().collect(Collectors.toMap(item-> item.getCategory()+"-"+item.getSpecification(), x -> x));
     */
    public void test(){
        String[] str = new String[3];
        str[0]="16.00";
        str[0]="11.00";
        str[0]="13.00";
        //字符串数组 求和
        Double sum = Stream.of(str).mapToDouble(g -> Double.parseDouble(g)).sum();
        System.out.println(sum);
    }


    /**
     * 分组统计总个数、总销售数量
     * @return
     */
    @RequestMapping("/findInfoByCategoryCount")
    public Map<String, ProductStatisticsVO> findInfoByCategoryCount(){
        Map<String, ProductStatisticsVO> result = new HashMap<>();
        Criteria criteria = new Criteria();
        Query query = Query.query(criteria);
        //排序规则 根据分类倒叙，排序升序
        query.with(Sort.by(
                Sort.Order.desc("category"),
                Sort.Order.asc("sort")
        ));
        List<Product> list=mongoTemplate.find(query,Product.class);
        //按分类名称和规格 分组显示
        Map<String,List<Product>> listMap = list.stream().collect(Collectors.groupingBy(item->item.getCategory()+"-"+item.getSpecification()));
        //遍历统计个数
        listMap.forEach((k,v)->{
            ProductStatisticsVO productStatisticsVO = new ProductStatisticsVO();
            productStatisticsVO.setCategory(k);
            //统计总个数
            productStatisticsVO.setTotal(v.size());
            //统计销售数量
            IntSummaryStatistics intSummaryStatistics=v.stream().collect(Collectors.summarizingInt(Product::getSaleNum));
            System.out.println(intSummaryStatistics);
            productStatisticsVO.setSaleNumSum(String.valueOf(intSummaryStatistics.getSum()));
            result.put(k,productStatisticsVO);
        });
        return result;
    }

    /**
     * 同一个字段 按范围查询
     * @return
     */
    @RequestMapping("/findInfoByTime")
    public List<Product> findInfoByTime(@RequestBody Product product){
        Criteria criteria = new Criteria();
        //创建时间  开始时间  结束时间
        if(!ObjectUtils.isEmpty(product.getStartTime()) || !ObjectUtils.isEmpty(product.getEndTime())){
            criteria = criteria.and("create_time");
            if(!ObjectUtils.isEmpty(product.getStartTime())){
                criteria.gte(product.getStartTime());
            }
            if(!ObjectUtils.isEmpty(product.getEndTime())){
                criteria.lte(product.getEndTime());
            }
        }
        //下面的写法报错：Due to limitations of the org.bson.Document, you can't add a second 'create_time' expression specified as 'create_time : Document{{$lte=Thu Jun 02 12:08:00 CST 2022}}'.
        // Criteria already contains 'create_time : Document{{$gte=Sun May 29 12:08:00 CST 2022}}'.
//        if(!ObjectUtils.isEmpty(product.getStartTime())){
//            criteria = criteria.and("create_time").gte(product.getStartTime());
//        }
//        if(!ObjectUtils.isEmpty(product.getEndTime())){
//            criteria = criteria.and("create_time").lte(product.getEndTime());
//        }
        Query query = Query.query(criteria);
        //排序规则 根据分类倒叙，排序升序
        query.with(Sort.by(
                Sort.Order.desc("category"),
                Sort.Order.asc("sort")
        ));
        List<Product> list=mongoTemplate.find(query,Product.class);
        return list;
    }


    /**
     * 分页获取数据列表
     * @param page 当前页数
     * @param pageSize 每页显示数量
     * @return
     */
    @RequestMapping("/findInfoByPage")
    public Map<String,Object> findInfoByPage(@RequestParam("page") Integer page,@RequestParam("pageSize") Integer pageSize){
        Map<String,Object> result = new HashMap<>();

        Criteria criteria = new Criteria();
        Query query = Query.query(criteria);
        //排序规则 根据分类倒叙，排序升序
        query.with(Sort.by(
                Sort.Order.desc("category"),
                Sort.Order.asc("sort")
        ));
        //获取总个数
        Long count = mongoTemplate.count(query,Product.class);
        //分页
        PageRequest pageRequest = PageRequest.of(page-1,pageSize);
        //分页=== 也可以用 query.skip(当前页-1).limit(页的大小)
        query.with(pageRequest);

        List<Product> list=mongoTemplate.find(query,Product.class);
        result.put("total",count);
        result.put("list",list);
        return result;
    }

    /**
     * 本日、本月、本年 聚合函数统计
     * @param dataType 1:本日 2：本月 3：本年
     * @return
     */
    @RequestMapping("/findInfoByStatistics")
    public List<Map> findInfoByStatistics(@RequestParam("dataType") Integer dataType){
        //从mongodb里获取最新的一条记录
        Criteria criteria = new Criteria();
        //获取当前时间
        LocalDateTime localDateTime = LocalDateTime.now();
        Date now = new Date();
        if(dataType == 1){
            //本日 开始时间 2022-06-08 00:00:00
            String begin = DateUtils.getDateString(DateUtils.getStartOfDay(now),"yyyy-MM-dd HH:mm:ss");
            //本日 结束时间 2022-06-08 23:59:59
            String end = DateUtils.getDateString(DateUtils.getEndOfDay(now),"yyyy-MM-dd HH:mm:ss");
            criteria.and("create_time").gte(begin).lte(end);
        }else if(dataType == 2){
            //本月 开始时间  如：2022-06-01 00:00:00
            String startTime= DateUtils.getDateString(DateUtils.getFirstThisDate(now),"yyyy-MM-dd HH:mm:ss");
            //本月 结束时间  如：2022-06-30 23:59:59
            String endTime= DateUtils.getDateString(DateUtils.getLastThisDate(now),"yyyy-MM-dd HH:mm:ss");
            criteria.and("create_time").gte(startTime).lte(endTime);
        }else if(dataType == 3){
            //本年 开始时间 如：2022-01-01 00:00:00
            String startTime= DateUtils.getFirstDateForYear(localDateTime.getYear())+" 00:00:00";
            //本年 结束时间 如：2022-12-31 23:59:59
            String endTime= DateUtils.getLastDateForYear(localDateTime.getYear())+" 23:59:59";
            criteria.and("create_time").gte(startTime).lte(endTime);
        }

        //聚合函数查询统计信息
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                //按分类名称 统计 销售数量
                Aggregation.group("category").sum("sale_num").as("total"),
                //按照total降序
                Aggregation.sort(Sort.Direction.DESC,"total")
        );
        AggregationResults<Map> aggregationResults = mongoTemplate.aggregate(aggregation,Product.class,Map.class);
        List<Map> list = aggregationResults.getMappedResults();

        return list;
    }

    /**
     * 近7日、近30日 聚合函数统计销量 趋势
     * @param dataType 1:近7日 2：近30日
     * @return
     */
    @RequestMapping("/findInfoByStatisticsTrend")
    public List<Map<String,Object>> findInfoByStatisticsTrend(@RequestParam("dataType") Integer dataType){
        List<Map<String,Object>> productTrendVOS = new ArrayList<>();

        //从mongodb里获取最新的一条记录
        Criteria criteria = new Criteria();
        //获取当前时间
        LocalDateTime beginTime;
        LocalDateTime endTime;
        List<String> timeIntervalList = null;

        if(dataType == 1){
            //近7日
            beginTime = LocalDateTime.of(LocalDateTime.now().minusDays(6).toLocalDate(), LocalTime.MIN).plusHours(8);
            endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).plusHours(8);
            timeIntervalList = DateUtils.getTimeInterval(7);
            //LocalDateTime 转 字符串（yyyy-MM-dd）
            String startDate = beginTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String endDate = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            criteria.and("create_time").gte(startDate).lte(endDate);
        }else if(dataType == 2){
            //近30日
            beginTime = LocalDateTime.of(LocalDateTime.now().minusDays(29).toLocalDate(), LocalTime.MIN).plusHours(8);
            endTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MAX).plusHours(8);
            timeIntervalList = DateUtils.getTimeInterval(30);
            //LocalDateTime 转 字符串（yyyy-MM-dd）
            String startDate = beginTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String endDate = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            criteria.and("create_time").gte(startDate).lte(endDate);
        }

        Query query = new Query();
        query.with(Sort.by(Sort.Order.asc("sort")));
        query.addCriteria(criteria);
        List<Product> productList = mongoTemplate.find(query, Product.class);
        //按照创建时间(创建日期)和分类名称 分组 统计 销量
        Map<String, List<Product>> productMap = productList.stream().collect(Collectors.groupingBy(item -> DateUtils.strToDateValue(item.getCreateTime())+"_"+item.getCategory()));



        timeIntervalList.forEach(t->{
            Map<String,Object> map = new HashMap<>();
            List<Map<String,Object>> mapList = new ArrayList<>();
            productMap.forEach((k, v) -> {
                Map<String,Object> data = new HashMap<>();
                //统计 销量
                IntSummaryStatistics intSummaryStatistics = v.stream().collect(Collectors.summarizingInt(Product::getSaleNum));
                //创建日期
                String createDate = k.split("_")[0];
                //分类名称
                String category = k.split("_")[1];

                if(createDate.equals(t)){
                    data.put("category",category);
                    data.put("createDate",createDate);
                    data.put("sum",intSummaryStatistics.getSum());
                    mapList.add(data);
                }
            });
            map.put(t,mapList);
            productTrendVOS.add(map);
        });
        return productTrendVOS;
    }


    /**
     * 根据某字段去重统计(分组统计) Aggregation 分组、分页
     * @return
     */
    @RequestMapping("/findDistinctInfo")
    public Map<String,Object>  findDistinctInfo(@RequestParam("page") Integer Page,@RequestParam("pageSize") Integer pageSize){
        Map<String,Object> resultMap = new HashMap<>();
        //从mongodb里获取最新的一条记录
        Criteria criteria = new Criteria();

        //根据 "category","create_time" 去重统计  使用mongoTemplate.aggregate去重，支持排序。推荐使用 优点：可指定返回类型。支持排序 缺点：排序是查询效率会变的非常低
        Aggregation agg = Aggregation.newAggregation(
                // 挑选所需的字段，类似select *，*所代表的字段内容
                Aggregation.project("title", "category","sale_num","create_time"),
                // sql where 语句筛选符合条件的记录
                Aggregation.match(criteria),
                // 分组条件，设置分组字段
                Aggregation.group("category","create_time")
                        //first，as里最后包含展示的字段
                        .first("title").as("title")
                        .first("category").as("category")
                        .first("sale_num").as("sale_num")
                        .first("create_time").as("create_time")
                ,
                // 排序（根据某字段排序 倒序）
                Aggregation.sort(Sort.by(
                        Sort.Order.desc("create_time")
                )),
                // 重新挑选需要字段（上面 as 后的 字段）
                Aggregation.project("title", "category","sale_num"),
                //分页
                Aggregation.skip((long) (Page > 0 ? (Page - 1) * pageSize : 0)),
                Aggregation.limit(pageSize)
        );
        AggregationResults<Product> results = mongoOperations.aggregate(agg, "product", Product.class);
        List<Product> list= results.getMappedResults();
        //统计去重后的 总个数
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                //按"category","create_time" 多字段分组去重统计
                Aggregation.group("category","create_time").first("category").as("category")
        );
        AggregationResults<Map> aggregate = mongoTemplate.aggregate(aggregation, Product.class, Map.class);

        resultMap.put("total",aggregate.getMappedResults().size());
        resultMap.put("list",list);
        return resultMap;
    }



    /**
     * 根据分类名称 分组统计销售数量
     * @param
     * @return
     */
    @RequestMapping("/findInfoByGroup")
    public List<Map> findInfoByGroup(){
        //从mongodb里获取最新的一条记录   status:状态 1：上架  2：下架
        Criteria criteria = Criteria.where("status").is(1);
        //聚合函数查询统计信息
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                //按分类名称 统计 销售数量
                Aggregation.group("category").sum("sale_num").as("total"),
                //按照total降序
                Aggregation.sort(Sort.Direction.DESC,"total")
        );
        AggregationResults<Map> aggregationResults = mongoTemplate.aggregate(aggregation,Product.class,Map.class);
        List<Map> list = aggregationResults.getMappedResults();

        return list;
    }

    /**
     * 根据分类名称和规格 分组统计销售数量
     * @param
     * @return
     */
    @RequestMapping("/findInfoByGroup1")
    public List<Map> findInfoByGroup1(){
        //从mongodb里获取最新的一条记录   status:状态 1：上架  2：下架
        Criteria criteria = Criteria.where("status").is(1);
        //聚合函数查询统计信息
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                //按分类名称 统计 销售数量
                Aggregation.group("category","specification").sum("sale_num").as("total"),
                //按照total降序
                Aggregation.sort(Sort.Direction.DESC,"total")
        );
        AggregationResults<Map> aggregationResults = mongoTemplate.aggregate(aggregation,Product.class,Map.class);
        List<Map> list = aggregationResults.getMappedResults();

        return list;
    }

    /**
     * 根据按日期 分组统计销售数量
     * @param
     * @return
     */
    @RequestMapping("/findInfoByGroup2")
    public List<Map> findInfoByGroup2(){
        //从mongodb里获取最新的一条记录   status:状态 1：上架  2：下架
        Criteria criteria = Criteria.where("status").is(1);
        //聚合函数查询统计信息
        Aggregation aggregation = Aggregation.newAggregation(
                // 挑选所需的字段，类似select *，*所代表的字段内容
                Aggregation.project("sale_num")
                        .and("create_time").substring(0,10).as("createDate"),
                // sql where 语句筛选符合条件的记录
                Aggregation.match(criteria),
                //按分类名称 统计 销售数量
                Aggregation.group("createDate").sum("sale_num").as("total")
                        //first，as里最后包含展示的字段
                        .first("createDate").as("createDate"),

                //按照total降序
                Aggregation.sort(Sort.Direction.DESC,"total")
        );
        AggregationResults<Map> aggregationResults = mongoTemplate.aggregate(aggregation,Product.class,Map.class);
        List<Map> list = aggregationResults.getMappedResults();

        return list;
    }

    /**
     * 时间范围检索
     * @param product
     * @return
     */
    @RequestMapping("/findInfoByTime1")
    public List<Product> testTimeRange(@RequestBody Product product){
        Query query = new Query();
        ArrayList<Criteria> list = new ArrayList<Criteria>();
        if(!ObjectUtils.isEmpty(product.getStartTime())){
            list.add(Criteria.where("create_time").gte(product.getStartTime()));
        }
        if(!ObjectUtils.isEmpty(product.getEndTime())){
            list.add(Criteria.where("create_time").lte(product.getEndTime()));
        }
        Criteria[] arr = new Criteria[list.size()];
        if (arr.length > 0){
            list.toArray(arr);
            Criteria criteria = new Criteria().andOperator(arr);
            query.addCriteria(criteria);
        }
        //排序规则 根据分类倒叙，排序升序
        query.with(Sort.by(
                Sort.Order.desc("category"),
                Sort.Order.asc("sort")
        ));
        List<Product> productList=mongoTemplate.find(query,Product.class);
        return productList;
    }

    /**
     * (条件1 or 条件2) and 条件3 查询
     * 思路：拆分 (条件1 or 条件2) and 条件3 =  (条件1 and 条件3) or (条件2 and 条件3)
     * @param name
     */
    @RequestMapping("/findUserListByExample")
    public List<Product> findUserListByExample(@RequestParam("name") String name){
        //(条件1 and 条件3)
        Criteria criteria = Criteria.where("title").regex(".*?" + name + ".*?")
                .and("status").is(1);

        //(条件2 and 条件3)
        Criteria criteria2 = Criteria.where("category").regex(".*?" + name + ".*?")
                .and("status").is(1);

        //组合(条件1 and 条件3) or (条件2 and 条件3)
        criteria.orOperator(criteria2);

        Query query = Query.query(criteria);

        //排序规则 根据分类倒叙，价格降序
        query.with(Sort.by(
                Sort.Order.desc("category"),
                Sort.Order.desc("price")
        ));
        List<Product> productList=mongoTemplate.find(query,Product.class);
        return productList;
    }
}
