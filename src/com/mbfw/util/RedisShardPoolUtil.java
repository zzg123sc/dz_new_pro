package com.mbfw.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.List;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

public class RedisShardPoolUtil {

	static ShardedJedisPool pool;

	static {

		JedisPoolConfig config = new JedisPoolConfig();

		config.setTestOnBorrow(true);
		String hostA = "192.168.6.222";
		int portA = 6379;
		String password = "";
		try {
			 InputStream in =MongoDbFileUtil.class.getResourceAsStream("/redisConfig.properties");
	         Properties p = new Properties();
	         p.load(in);
	         config.setMaxActive(MyNumberUtils.toInt(p.getProperty("maxActive")));
	         config.setMaxIdle(MyNumberUtils.toInt(p.getProperty("maxIdle")));
	         config.setTestOnBorrow(true);
	         config.setMaxWait(MyNumberUtils.toInt(p.getProperty("maxWait")));
	         password = p.getProperty("password");
	         config.setTimeBetweenEvictionRunsMillis(MyNumberUtils.toInt(p.getProperty("timeBetweenEvictionRunsMillis")));
	         hostA = p.getProperty("url");
	         portA = MyNumberUtils.toInt(p.getProperty("port"));
	         if(portA <= 0) {
	        	 portA = 6379;
	         }
		} catch (Exception e) {
            e.printStackTrace();
        }

		

		//String hostB = "192.168.91.16";

		//int portB = 6379;

		List<JedisShardInfo> jdsInfoList = new ArrayList<JedisShardInfo>(2);

		JedisShardInfo infoA = new JedisShardInfo(hostA, portA);
		if(MyStringUtils.notBlank(password)) {
			infoA.setPassword(password);
		}

		//JedisShardInfo infoB = new JedisShardInfo(hostB, portB);

		//infoB.setPassword("testsxt");

		jdsInfoList.add(infoA);

		//jdsInfoList.add(infoB);

		pool = new ShardedJedisPool(config, jdsInfoList, Hashing.MURMUR_HASH,

		Sharded.DEFAULT_KEY_TAG_PATTERN);

	}

	/**
	 * 
	 * @param args
	 */

	public static void main(String[] args) {

		for (int i = 0; i < 100; i++) {

			String key = generateKey();


			ShardedJedis jds = null;

			try {

				jds = pool.getResource();

				 

				 
				 

			} catch (Exception e) {

				e.printStackTrace();

			}

			finally {

				pool.returnResource(jds);

			}

		}

	}

	private static int index = 1;

	public static String generateKey() {

		return String.valueOf(Thread.currentThread().getId()) + "_" + (index++);

	}

}