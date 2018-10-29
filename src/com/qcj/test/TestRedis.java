package com.qcj.test;

import com.alibaba.fastjson.JSON;
import com.qcj.entiry.User;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * 模拟把一个表中数据存储到redis
 */
public class TestRedis {

    @Test
    public void test(){
        //连接一个redis实例
        Jedis j = new Jedis("127.0.0.1",6379);
        j.auth("qichangjian");//跟操作redis一样
        /*System.out.println(j);
        List<String> list = j.mget("name","age");//得到两个String类型的key，返回时list mget获得多个key
        list.forEach(System.out::println);
        j.set("sex","nan");*/

        /*Map<String,String> map = new HashMap<>();
        map.put("name","xinxin");
        map.put("age","22");
        map.put("qq","123456");
        j.hmset("user",map);*/



        //思路：就是把要查询的东西先放入到一个业务规则中，类似于存储在视图中
        /**模拟把一个user表中的数据存储到redis
         * 实现：
         * select * from user where age = 22
         * select * from user where age = 22 and sex = 'nan'
         */
        /**
         * 方式一：这种方式实现不了这个按照条件查询
         * 要多重集合配合使用 set集合和hash配合使用
         * 指定业务，查询业务：SYS_USER_AGE_22 这是set的key
         * 指定查询业务：      SYS_USER_SEX_NAN
         * 指定查询业务：      SYS_USER_SEX_NV
         */
        final String SYS_USER_AGE_22 = "SYS_USER_AGE_22";
        final String SYS_USER_SEX_NAN = "SYS_USER_SEX_NAN";
        final String SYS_USER_SEX_NV = "SYS_USER_SEX_NV";
        final String USERS = "users";
        /*//User java中类  User对象数据量很大，查询很频繁，需要把user表中的数据都放入到缓存中取
        //做放入操作   数据对象id类型一般都是UUID类型
        //String uid = "20160128" + UUID.randomUUID().toString();
        //把十条数据放入一个map
        Map<String,String> map = new HashMap<>();//user需要转换成string
        String uid1 = UUID.randomUUID().toString();
        User u1 = new User(uid1,"z1",12,"nan");
        map.put(uid1,toJsonString(u1));
        j.sadd(SYS_USER_SEX_NAN,uid1);//满足业务就插入set

        String uid2 = UUID.randomUUID().toString();
        User u2 = new User(uid2,"z2",22,"nan");
        map.put(uid2,toJsonString(u2));
        j.sadd(SYS_USER_SEX_NAN,uid2);
        j.sadd(SYS_USER_AGE_22,uid2);

        String uid3 = UUID.randomUUID().toString();
        User u3 = new User(uid3,"z3",23,"nan");
        map.put(uid3,toJsonString(u3));
        j.sadd(SYS_USER_SEX_NAN,uid3);

        String uid4 = UUID.randomUUID().toString();
        User u4 = new User(uid4,"w1",24,"nan");
        map.put(uid4,toJsonString(u4));
        j.sadd(SYS_USER_SEX_NAN,uid4);

        String uid5 = UUID.randomUUID().toString();
        User u5 = new User(uid5,"w2",22,"nv");
        map.put(uid5,toJsonString(u5));
        j.sadd(SYS_USER_SEX_NV,uid5);
        j.sadd(SYS_USER_AGE_22,uid5);

        j.hmset("users",map);*/

        //现在要查询符合条件的数据
        /*Set<String> user_ages = j.smembers(SYS_USER_AGE_22);//取得是男的的id 集合 然后遍历取得Users keys中 id为这些的值
        for (Iterator iterator = user_ages.iterator();iterator.hasNext();){
            String string = (String)iterator.next();
            String ret = j.hget(USERS,string);
            System.out.println(ret);
        }*/
        //如果有两个条件就取交集或者并集
        Set<String> user_ages = j.sinter(SYS_USER_AGE_22,SYS_USER_SEX_NAN);//取交集
        for (Iterator iterator = user_ages.iterator();iterator.hasNext();){
            String string = (String)iterator.next();
            String ret = j.hget(USERS,string);
            System.out.println(ret);
            User u = parseBeanObject(ret);
            System.out.println(u.getName());
        }

    }

    @Test
    public void dd(){
        String uid1 = UUID.randomUUID().toString();
        User u1 = new User(uid1,"z1",12,"nan");
        System.out.println(toJsonString(u1));
    }
    /**
     * 序列化
     */
    public String toJsonString(User u)
    {
        String text = JSON.toJSONString(u);
        System.out.println("toJsonString()方法：text=" + text);
        // 输出结果：text={"age":105,"id":"testFastJson001","name":"maks"}
        return text;
    }
    /**
     * 反序列化为javaBean对象
     */
    public User parseBeanObject(String text)
    {
        User user = (User) JSON.parseObject(text, User.class);
        //System.out.println("parseBeanObject()方法：user==" + user.getId() + "," + user.getName() + "," + user.getAge());
        // 输出结果：user==testFastJson001,maks,105
        return user;
    }
}
