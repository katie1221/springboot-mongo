package com.example.mongodbdemo.controller;

import com.example.mongodbdemo.entity.User;
import com.example.mongodbdemo.utils.Result;
import com.example.mongodbdemo.vo.UserVO;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 用户管理
 * @author qzz
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private GridFsTemplate gridFsTemplate;


    /****************************************新增*********************************/
    /**
     * 新增
     * insert\save区别:插入重复数据
     * 	 insert: 若新增数据的主键已经存在，则会抛 org.springframework.dao.DuplicateKeyException 异常提示主键重复，不保存当前数据。
     * 	 save: 若新增数据的主键已经存在，则会对当前已经存在的数据进行修改操作。
     * @param user
     * @return
     */
    @RequestMapping("/add")
    public User addUser(@RequestBody User user){
        user.setCreateTime(new Date());
        return mongoTemplate.insert(user);
    }

    /**
     * 批量新增
     * @return
     */
    @PostMapping("/batchInsert")
    public Result batchInsert(@RequestBody List<User> userList){
        //添加多条记录方式一
        mongoTemplate.insertAll(userList);
        //添加多条记录方式二
        //mongoTemplate.insert(userList,User.class);

        //上面两个方法不能同时操作相同的数据（不能同时存在）
        return Result.success();
    }

    /****************************************删除*********************************/
    /**
     * 删除（remove）:根据实体类对象删除
     * @param id
     * @return
     */
    @RequestMapping("/remove")
    public Result remove(@RequestParam("id") String id){
        User user = new User();
        user.setId(id);
        DeleteResult deleteResult =mongoTemplate.remove(user);
        return Result.success(deleteResult.getDeletedCount());
    }

    /**
     * 删除（remove）:根据条件（Query对象）删除---- 推荐使用这个
     * @param id
     * @return
     */
    @RequestMapping("/removeByQuery")
    public Result removeByQuery(@RequestParam("id") String id){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));

        DeleteResult deleteResult =mongoTemplate.remove(query,User.class);
        return Result.success(deleteResult.getDeletedCount());
    }

    /****************************************修改*********************************/

    /**
     * 更新一条记录(updateFirst)：更新匹配条件的第一条数据
     * @param user
     * @return
     */
    @RequestMapping("/updateFirst")
    public Result updateFirst(@RequestBody User user){
        user.setUpdateTime(new Date());
        //条件
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(user.getId()));

        Update update = new Update();
        update.set("name",user.getName());
        update.set("age",user.getAge());
        update.set("email",user.getEmail());
        update.set("updateTime",new Date());

        UpdateResult updateResult =mongoTemplate.updateFirst(query,update,User.class);
        return Result.success(updateResult.getMatchedCount());
    }

    /**
     * 更新(updateMulti)：更新匹配条件的全部数据
     * @param user
     * @return
     */
    @RequestMapping("/updateMulti")
    public Result updateMulti(@RequestBody User user){
        user.setUpdateTime(new Date());
        //条件
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(user.getId()));

        Update update = new Update();
        update.set("name",user.getName());
        update.set("age",user.getAge());
        update.set("email",user.getEmail());

        UpdateResult updateResult =mongoTemplate.updateMulti(query,update,User.class);
        return Result.success(updateResult.getMatchedCount());
    }

    /**
     * 更新（upsert）mongoTemplate.upsert(query,update,User.class)：如果数据不存在就增加一条记录
     * @param user
     * @return
     */
    @RequestMapping("/upsert")
    public Result upsert(@RequestBody User user){
        user.setUpdateTime(new Date());
        //条件
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(user.getId()));

        Update update = new Update();
        update.set("name",user.getName());
        update.set("age",user.getAge());
        update.set("email",user.getEmail());

        UpdateResult updateResult =mongoTemplate.upsert(query,update,User.class);
        return Result.success(updateResult.getMatchedCount());
    }

    /****************************************修改*********************************/

    /**
     * 查询全部 findAll
     * @return
     */
    @RequestMapping("/all")
    public Result getAll(){
        List<User> list = mongoTemplate.findAll(User.class);
        return Result.success(list);
    }

    /**
     * 条件查询  或（or）
     */
    @RequestMapping("/findQuery")
    public Result findQuery(){
        //条件1
        Criteria criteria1=new Criteria();
        criteria1.and("id").is("001");
        //条件2
        Criteria criteria2=new Criteria();
        criteria2.and("name").is("张三");

        //把上面的两个条件  或 起来
        Criteria criteria = new Criteria();
        criteria.orOperator(criteria1,criteria2);

        Query query = new Query();
        query.addCriteria(criteria);

        List<User> list = mongoTemplate.find(query,User.class);
        return Result.success(list);
    }


    /**
     * 条件查询  且（and）
     */
    @RequestMapping("/findQueryAnd")
    public Result findQueryAnd(){

        //and 比较简单
        Criteria criteria = new Criteria();
        criteria.and("name").is("张三").and("age").gt(10);
        Query query = new Query();
        query.addCriteria(criteria);

        List<User> list = mongoTemplate.find(query,User.class);
        return Result.success(list);
    }

    /**
     * 条件查询  模糊查询（regex）
     */
    @RequestMapping("/findQueryRegex")
    public Result findQueryRegex(){

        Criteria criteria = new Criteria();
        //正则
        criteria.and("name").regex("^.*"+0+".*$");
        Query query = new Query();
        query.addCriteria(criteria);

        List<User> list = mongoTemplate.find(query,User.class);
        return Result.success(list);
    }

    /**
     * 条件查询  查总数（count）
     */
    @RequestMapping("/findQueryCount")
    public Result findQueryCount(){

        Criteria criteria = new Criteria();
        //正则
        criteria.and("name").regex("^.*"+0+".*$");
        Query query = new Query();
        query.addCriteria(criteria);

        long count = mongoTemplate.count(query,User.class);
        return Result.success(count);
    }

    /**
     * 条件查询  排序（sort）
     */
    @RequestMapping("/findQuerySort")
    public Result findQuerySort(){

        Criteria criteria = new Criteria();
        //
        criteria.and("age").gt(10);
        Query query = new Query();
        query.addCriteria(criteria);
        //排序规则 根据id倒叙 (springboot2.2.1（含）以上的版本Sort已经不能再实例化了，构造方法已经是私有的了！可以改用Sort.by获得Sort对象)
        Sort sort = Sort.by(Sort.Direction.DESC,"_id");
        query.with(sort);

        //(springboot2.2.1以下的版本 使用 这种方式进行排序)
        //Sort sort = new Sort(Sort.Direction.DESC,"_id");

        List<User> userList = mongoTemplate.find(query,User.class);
        return Result.success(userList);
    }

    /**
     * 条件查询  分页
     * @param name
     * @param offset 当前页
     * @param limit 每页显示个数
     * @return
     */
    @RequestMapping("/page")
    public Result list(String name,Integer offset,Integer limit){

        //条件体
        Query query = new Query();

        if(StringUtils.isNotBlank(name)){
            query.addCriteria(Criteria.where("name").is(name));
        }
        //排序规则 id降序
        Sort sort = Sort.by(Sort.Direction.DESC,"_id");
        query.with(sort);
        //当前页+页的大小（当前页应该是实际页数减1）
        PageRequest page = PageRequest.of(offset-1,limit);
        //分页=== 也可以用 query.skip(当前页-1).limit(页的大小)
        query.with(page);

        List<User> userList = mongoTemplate.find(query,User.class);
        return Result.success(userList);
    }

    /**
     * 聚合查询  Aggregation
     * @return
     */
    @RequestMapping("/aggregation")
    public Result aggregation(){

        Criteria criteria = new Criteria();
        criteria.and("age").gt(10);

        //Aggregation.group("id").count().as("total")
        //count聚合函数（和 SQL一样）
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                //按age分组统计个数
                Aggregation.group("age").count().as("total"),
                //按照total降序
                Aggregation.sort(Sort.Direction.DESC,"total"));

        //user表名 UserVO.class返回的类型，可以自行封装实体类属性名 要和聚合的别名一样才可以注入，类似mybatis
        AggregationResults<UserVO> aggregationResults = mongoTemplate.aggregate(aggregation,"User", UserVO.class);

        List<UserVO> userList = aggregationResults.getMappedResults();
        return Result.success(userList);
    }

    /**
     * 查询单条记录
     */
    @RequestMapping("/findOne")
    public Result findOne(){

        Criteria criteria = new Criteria();
        //条件年龄大于16
        criteria.and("age").gt(16);
        Query query = new Query();
        query.addCriteria(criteria);
        Sort sort = Sort.by(Sort.Direction.DESC,"_id");
        query.with(sort);

        User user = mongoTemplate.findOne(query,User.class);
        return Result.success(user);
    }

    /**
     * 获取数据库中的所有文档集合
     */
    @RequestMapping("/findAllCollection")
    public Result findAllCollection(){
        Set<String> set = mongoTemplate.getCollectionNames();
        return Result.success(set);
    }




    /********************************内嵌数据 增删改查************************************************/
    /**
     * 内嵌数据增加    增（update.push(字段,值)）
     * @param id
     * @return
     */
    @RequestMapping("/updatePush")
    public Result updatePush(@RequestParam("id") String id){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));

        User user = new User();
        user.setId("003");
        user.setName("xiao");

        Update update = new Update();
        //users是字段名 把user添加到users的集合中内嵌的数据(在集合中显示的值是数组格式)
        update.push("users",user);

        UpdateResult updateResult =mongoTemplate.updateFirst(query,update,User.class);
        return Result.success(updateResult.getMatchedCount());
    }

    /**
     * 内嵌数据删除    增（update.pull(字段,对象)）
     * @param id
     * @return
     */
    @RequestMapping("/updatePull")
    public Result updatePull(@RequestParam("id") String id){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));

        User user = new User();
        user.setId("003");

        Update update = new Update();
        //users是字段名 users中查找匹配的user进行删除
        update.pull("users",user);

        UpdateResult updateResult =mongoTemplate.updateFirst(query,update,User.class);
        return Result.success(updateResult.getMatchedCount());
    }

    /**
     * 内嵌数据修改    改（update.set）
     * update.set("users.$.name",值) 修改users.name为例子
     * 在条件上一般加上“users.name”作为筛选条件
     * @param id
     * @return
     */
    @RequestMapping("/updateSet")
    public Result updateSet(@RequestParam("id") String id){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        //修改条件---明确到修改 users数组中的哪些数据， 没写则在全users数组中匹配
        query.addCriteria(Criteria.where("users.name").is("xiao"));

        Update update = new Update();
        //users是字段名 users中查找匹配的user进行删除
        update.set("users.$.name","数据改了");

        //如果是修改多条数据  把  $ 改成 $[]
        //update.set("users.$[].name","数据全部改了");

        UpdateResult updateResult =mongoTemplate.updateFirst(query,update,User.class);
        return Result.success(updateResult.getMatchedCount());
    }

    /**
     * 内嵌数据查询    查（find）
     * 查询基本不变字段名：users.id
     * @param id
     * @return
     */
    @RequestMapping("/find")
    public Result find(@RequestParam("id") String id){
        Query query = new Query();
        query.addCriteria(Criteria.where("users.id").is("003"));
        List<User> list = mongoTemplate.find(query,User.class);
        return Result.success(list);
    }

    /**
     * 内嵌数据查询    查询返回指定字段
     * query.fields().include("字段")//包含该字段
     * query.fields().exclude("字段")//不包含该字段
     * @param id
     * @return
     */
    @RequestMapping("/findFields")
    public Result findFields(@RequestParam("id") String id){
        Query query = new Query();
        query.addCriteria(Criteria.where("users.id").is("003"));
        query.fields().exclude("users");
        List<User> list = mongoTemplate.find(query,User.class);
        return Result.success(list);
    }

    /**
     * 文件上传
     * @param file
     */
    public void upload(MultipartFile file){
        try {
            ObjectId store =gridFsTemplate.store(file.getInputStream(),file.getOriginalFilename(),file.getContentType());
            String fileId=store.toString();//文件id--- 下载要用，要保存起来
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件上传
     * @param fileId 上传时返回的文件id
     */
    public void download(String fileId, HttpServletResponse response) throws IOException {
        String id = fileId;
        //根据文件id 获取下载信息
        GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id)));
        if(file!=null){
            //获取文件
            GridFsResource gridFsResource = new GridFsResource(file, GridFSBuckets.create(mongoTemplate.getDb())
                    .openDownloadStream(file.getObjectId()));
            //文件名称
            String fileName = URLEncoder.encode(gridFsResource.getFilename(),"UTF-8");
            //设置下载属性
            response.setContentType(gridFsResource.getContentType());
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-disposition","attachment;filename="+fileName);
            ServletOutputStream outputStream = response.getOutputStream();
            //把获取到的文件流输入到 response的输出流（下载）
            IOUtils.copy(gridFsResource.getInputStream(),outputStream);
        }
    }


    /**
     * 条件查询  组合排序（sort）
     */
    @RequestMapping("/findQueryMultipleSort")
    public Result findQueryMultipleSort(){

        Criteria criteria = new Criteria();
        criteria.and("age").gt(10);
        Query query = new Query();
        query.addCriteria(criteria);
        //排序规则 根据id倒叙 (springboot2.2.1（含）以上的版本Sort已经不能再实例化了，构造方法已经是私有的了！可以改用Sort.by获得Sort对象)
//        query.with(Sort.by(
//                Sort.Order.desc("age"),
//                Sort.Order.desc("createTime")
//        ));

        //(springboot2.2.1以下的版本 使用 这种方式进行排序)
        //Sort sort = new Sort(Sort.Direction.DESC,"_id");

        List<User> userList = mongoTemplate.find(query,User.class);
        return Result.success(userList);
    }
}
