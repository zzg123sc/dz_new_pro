package com.mbfw.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;


public class RedisUtil {
    public static void test() {
        ShardedJedis jds = null;
        try {
            jds = RedisShardPoolUtil.pool.getResource();
            jds.set("lmf", "limingfei");
             
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            RedisShardPoolUtil.pool.returnResource(jds);
        }
    }
    
    /**
     * delByKey 删除缓存对象
     * 
     * @Description 传入ShardedJedis 对象
     * @param jds
     * @param key
     * @return 
     * long
     * @see
     */
    public static long delByKey(ShardedJedis jds, String key) {
        if(jds != null) {
            return jds.del(key);
        } else {
            return delByKey(key);
        }
    }
    
    /**
     * getList 获取list中的值
     * 
     * @Description 获取list中的值
     * @param jds
     * @param key
     * @param start 下标开始值
     * @param end   下标结束值 结束值放-1则为 最后一位
     * @return 
     * List<String>
     * @see
     */
    public static List<String> getList(ShardedJedis jds, String key, long start, long end) {
        if(jds != null) {
            return jds.lrange(key, start, end);
        } else {
            return getList(key, start, end);
        }
    }
    
    /**
     * getList 获取list中的值
     * 
     * @Description 获取list中的值
     * @param key 
     * @param start  下标开始值
     * @param end   下标结束值   结束值放-1则为 最后一位
     * @return 
     * List<String>
     * @see
     */
    public static List<String> getList(String key, long start, long end) {
        ShardedJedis jds = getJedis();
        List<String> result = jds.lrange(key, start, end);
        returnJedis(jds);
        return result;
    }
    
    /**
     * delListValue 删除list中 value值
     * 
     * @Description 删除list中 value值
     * @param key   list的key至
     * @param count 删除的个数   为0时代表删除全部
     * @param value value值
     * @return 
     * long
     * @see
     */
    public static long delListValue(ShardedJedis jds, String key, long count, String value) {
        if(jds != null) {
            return jds.lrem(key, count, value);
        } else {
            return delListValue(key, count, value);
        }
    }
    
    /**
     * delListValue 删除list中 value值
     * 
     * @Description 删除list中 value值
     * @param key   list的key至
     * @param count 删除的个数
     * @param value value值
     * @return 
     * long
     * @see
     */
    public static long delListValue(String key, long count, String value) {
        ShardedJedis jds = getJedis();
        long result = jds.lrem(key, count, value);
        returnJedis(jds);
        return result;
    }
    
    /**
     * add2List 往缓存中添加list类型的缓存 往list尾部添加字符串
     * 
     * @Description 往缓存中添加list类型的缓存 往list尾部添加字符串
     * @param key 缓存名称
     * @param strings   值
     * void
     * @see
     */
    public static void add2RightList(String key, String... strings) {
    	if(strings != null) {
	        ShardedJedis jds = getJedis();
	        jds.rpush(key, strings);
	        returnJedis(jds);
    	}
    }
    
    /**
     * add2List 往缓存中添加list类型的缓存  往list尾部添加字符串
     * 
     * @Description 往缓存中添加list类型的缓存 往list尾部添加字符串
     * @param jds
     * @param key   缓存名称
     * @param strings   值
     * void
     * @see
     */
    public static void add2RightList(ShardedJedis jds, String key, String... strings) {
        if(jds != null) {
            jds.rpush(key, strings);
        } else {
            add2RightList(key, strings);
        }
    }
    
    /**
     * add2List 往缓存中添加list类型的缓存 往list头部添加字符串
     * 
     * @Description 往缓存中添加list类型的缓存 往list头部添加字符串
     * @param key 缓存名称
     * @param strings   值
     * void
     * @see
     */
    public static void add2LeftList(String key, String... strings) {
        ShardedJedis jds = getJedis();
        jds.lpush(key, strings);
        returnJedis(jds);
    }
    
    /**
     * add2List 往缓存中添加list类型的缓存  往list头部添加字符串
     * 
     * @Description 往缓存中添加list类型的缓存 往list头部添加字符串
     * @param jds
     * @param key   缓存名称
     * @param strings   值
     * void
     * @see
     */
    public static void add2LeftList(ShardedJedis jds, String key, String... strings) {
        if(jds != null) {
            jds.lpush(key, strings);
        } else {
            add2LeftList(key, strings);
        }
    }
    
    /**
     * delByKey 删除缓存对象根据key
     * 
     * @Description 删除缓存对象根据key
     * @param key 
     * @return 
     * long
     * @see
     */
    public static long delByKey(String key) {
        long result = 0;
        ShardedJedis jds = getJedis();
        result = jds.del(key);
        returnJedis(jds);
        return result;
    }
    
    public static long delMap(ShardedJedis jds, String key, String... fields) {
        if(jds != null) {
            return jds.hdel(key, fields);
        } else {
            return delMap(key, fields);
        }
    }
    
    /**
     * delMap 删除map对象中的键值对 
     * 
     * @Description 删除map对象中的键值对 
     * @param key  map 名
     * @param fields 键值对的  fields  可传多个
     * @return 返回删除个数
     * long
     * @see
     */
    public static long delMap(String key, String... fields) {
        long result = 0;
        ShardedJedis jds = getJedis();
        result = jds.hdel(key, fields);
        returnJedis(jds);
        return result;
    }
    
    /**
     * getMap 从map中获取一个map的一个属性的value
     * 
     * @Description 从map中获取一个map的一个属性的value 传入ShardedJedis对象 不释放
     * @param jds
     * @param key
     * @param field
     * @return 
     * String
     * @see
     */
    public static String getMap(ShardedJedis jds, String key, String field) {
        if(jds != null) {
            return jds.hget(key, field);
        } else {
            return getMap(key, field);
        }
    }
    
    /**
     * getMap  从map中获取一个map的一个属性的value
     * 
     * @Description 从map中获取一个map的一个属性的value 自动获取ShardedJedis对象 并释放
     * @param key
     * @param field
     * @return 
     * String
     * @see
     */
    public static String getMap(String key, String field) {
        ShardedJedis jds = getJedis();
        String result = null;
        if(jds != null) {
            result = jds.hget(key, field);
        }
        returnJedis(jds);
        return result;
    }
    
    /**
     * getMap 从缓存中读取Map全部属性
     * 
     * @Description 从缓存中读取Map全部属性
     * @param jds jds对象
     * @param key
     * @return 
     * Map<String,String>
     * @see
     */
    public static Map<String, String> getMap(ShardedJedis jds, String key) {
        if(jds != null) {
            return jds.hgetAll(key);
        } else {
            return getMap(key);
        }
    }
    
    /**
     * getMap 从缓存中读取Map全部属性
     * 
     * @Description 从缓存中读取Map全部属性  不传递 ShardedJedis对象 自动获取并释放
     * @param key
     * @return 
     * Map<String,String>
     * @see
     */
    public static Map<String, String> getMap(String key) {
        ShardedJedis jds = getJedis();
        Map<String, String> map = null;
        if(jds != null) {
            map = jds.hgetAll(key);
        }
        returnJedis(jds);
        return map;
    }
    
    /**
     * add2Map 添加至map中
     * 
     * @Description 添加至map中
     * @param jds
     * @param key   
     * @param map   
     * void
     * @see
     */
    public static void add2Map(ShardedJedis jds, String key, Map<String, String> map) {
        Set<String> set = map.keySet();
        Object[] fieldObj = set.toArray();
        String[] fields = new String[fieldObj.length];
        String[] values = new String[fieldObj.length];
        for (int i = 0; i < fieldObj.length; i++) {
            fields[i] = (String)fieldObj[i];
            values[i] = map.get(fieldObj[i]);
        }
        add2Map(jds, key, fields, values);
    }
    
    /**
     * add2Map redis添加map至缓存中
     * 
     * @Description 批量添加map中的 key-value对
     * @param key   map的key
     * @param fields    map中的字段值
     * @param values void   map中的value值
     * @see
     */
    public static void add2Map(ShardedJedis jds, String key, String[] fields, String[] values) {
        if(MyStringUtils.notBlank(key) && fields != null && values != null && jds != null) {
            if(values.length >= fields.length) {
                for (int i = 0; i < fields.length; i++) {
                    if(values[i] != null && fields[i] != null) {
                        jds.hset(key, fields[i], values[i]);
                    }
                }
            }
        }
    }
    
    /**
     * add2Map redis添加map至缓存中
     * 
     * @Description 不传jds自动获取并执行操作后释放
     * @param key
     * @param fields
     * @param values 
     * void
     * @see
     */
    public static void add2Map(String key, String[] fields, String[] values) {
        ShardedJedis jds = getJedis();
        if(MyStringUtils.notBlank(key) && fields != null && values != null && jds != null) {
            if(values.length >= fields.length) {
                for (int i = 0; i < fields.length; i++) {
                    jds.hset(key, fields[i], values[i]);
                }
            }
        }
        returnJedis(jds);
    }
    
    /**
     * add2Map redis添加map至缓存中 添加一个键值对
     * 
     * @Description 添加一个键值对
     * @param jds
     * @param key
     * @param field
     * @param value 
     * void
     * @see
     */
    public static void add2Map(ShardedJedis jds, String key, String field, String value) {
        if(jds != null && MyStringUtils.notBlank(key) && MyStringUtils.notBlank(field)) {
            jds.hset(key, field, value);
        }
    }
    
    /**
     * 
     * deleteMhKey  {删除模糊查询的key}
     * 
     * @Description {删除模糊查询的key}
     * @param key void
     * @see
     */
    public static void deleteMhKey(String key){
    	ShardedJedis jds=getJedis();
    	Collection<Jedis> jedisC = jds.getAllShards();  
    	if(jedisC==null || jedisC.isEmpty()) {
    		return;
    	}
    	Iterator<Jedis> iter = jedisC.iterator();  
        while (iter.hasNext()) {  
            Jedis _jedis = iter.next();  
            Set<String> keys = _jedis.keys(key + "*");  
            if(keys!=null && !keys.isEmpty()) {
            	_jedis.del(keys.toArray(new String[keys.size()]));  
            }
        }
    }
    
    /**
     * add2Map redis添加map至缓存中 添加一个键值对
     * 
     * @Description 添加一个键值对
     * @param jds
     * @param key
     * @param field
     * @param value 
     * void
     * @see
     */
    public static void add2Map(String key, String field, String value) {
        ShardedJedis jds = getJedis();
        if(jds != null && MyStringUtils.notBlank(key) && MyStringUtils.notBlank(field)) {
            jds.hset(key, field, value);
        }
        returnJedis(jds);
    }
    
    /**
     * getJedis 获取jds对象
     * 
     * @Description 获取jds对象
     * @return ShardedJedis
     * @see
     */
    public static ShardedJedis getJedis() {
        ShardedJedis jds = null;
        try {
        	if(RedisShardPoolUtil.pool != null) {
            	jds = RedisShardPoolUtil.pool.getResource();
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jds;
    }
    
    /**
     * existKey 判断key是否存在
     * 
     * @Description 判断key是否存在
     * @param jds
     * @param key
     * @return 
     * boolean
     * @see
     */
    public static boolean existKey(ShardedJedis jds, String key) {
        return jds.exists(key);
    }
    
    /**
     * getString 获取String类型的数据
     * 
     * @Description 获取String类型的数据
     * @param jds
     * @param key
     * @return 
     * String
     * @see
     */
    public static String getString(ShardedJedis jds, String key) {
        return jds.get(key);
    }
    
    /**
     * returnJedis 使用完毕后释放Jedis对象
     * 
     * @Description 使用完毕后释放Jedis对象
     * @param jds 
     * void
     * @see
     */
    public static void returnJedis(ShardedJedis jds) {
    	if (jds==null) {
    		return;
    	}
        RedisShardPoolUtil.pool.returnResource(jds);
    }
    
    /**
     *  
     * getIncr  {获取key的自增数}
     * 
     * @Description {获取key的自增数}
     * @param key
     * @return long
     * @see
     */
    public static long getIncr(String key) {
        ShardedJedis jds = getJedis();
        long _id = jds.incr(key);
        returnJedis(jds);
        return _id;
        // return getJedis().incr(key);
    }
    
    /**
     * blpop 根据时间 扫描redis及时返回数据
     * 
     * @Description 根据时间 扫描redis及时返回数据
     * @param time  监听的时间间隔
     * @param keyArr  监听的list数组的key
     * @return List<String>
     * @see
     */
    public static List<String> blpop(int time, String[] keyArr) {
    	ShardedJedis jds = RedisUtil.getJedis();
		Collection<Jedis> jedisC = jds.getAllShards();
        Iterator<Jedis> iter = jedisC.iterator();  
        if (iter.hasNext()) {  
            Jedis _jedis = iter.next();
            List<String> tableOrglist = _jedis.blpop(time,  keyArr);
            returnJedis(jds);
            return tableOrglist;
        } else {
        	returnJedis(jds);
        	return null;
        }
    }
    
    /**
     * addString 往redis中添加字符串
     * 
     * @Description TODO{功能详细描述}
     * @param key
     * @param value void
     * @see
     */
    public static void addString(String key, String value) {
        ShardedJedis jds = getJedis();
        jds.set(key, value);
        returnJedis(jds);
    }
    /**
     * addString 往redis中添加字符串
     * 
     * @Description TODO{功能详细描述}
     * @param key
     * @param value void
     * @see
     */
    public static void addString(ShardedJedis jds, String key, String value) {
        jds.set(key, value);
    }
    
    /**
     * 
     * getStringVal  {获取值}
     * 
     * @Description {获取值}
     * @param key
     * @return String
     * @see
     */
    public static String getStringVal(String key) {
        ShardedJedis jds = getJedis();
        String val = jds.get(key);
        returnJedis(jds);
        return val;
    }
    
    /**
     * blpopAll 获取redis中list中的全部数据并删除
     * 
     * @Description 获取redis中list中的全部数据并删除
     * @author wxf
     * @param keyArr
     * @return List<String>
     * @see
     */
    public static List<String> blpopAll(String[] keyArr) {
        ShardedJedis jds = RedisUtil.getJedis();
        List<String> list = new ArrayList<String>();
        if(keyArr != null) {
            for (String key : keyArr) {
                List<String> oneList = jds.lrange(key, 0, -1);
                if(MyCollectionUtils.notEmpty(oneList)) {
                   int size = oneList.size();
                   jds.ltrim(key, size, -1);
                   list.addAll(oneList);
                }
            }
        }
        RedisUtil.returnJedis(jds);
        return list;
    }

}
