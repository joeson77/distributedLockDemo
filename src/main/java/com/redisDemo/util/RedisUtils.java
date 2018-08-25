package com.redisDemo.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * @author 厉昀键
 * Created on 2018年1月3日
 * 类说明
 * Redis连接池
 */
public class RedisUtils {
	
	public RedisUtils() {
	}
	
	private static JedisPool jedisPool = null;
	
	//获取连接
	public static synchronized Jedis getJedis(){
		if (jedisPool == null) {
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			//指定连接池最大空闲连接数
			jedisPoolConfig.setMaxIdle(50);
			//连接池中创建的最大连接数
			jedisPoolConfig.setMaxTotal(200);
			//设置创建连接的超时时间
			jedisPoolConfig.setMaxWaitMillis(5000000);
			//表示连接池在创建连接的时候会先测试一下连接是否可用，这样可以保证连接池中的连接都可以使用
			jedisPoolConfig.setTestOnBorrow(true);
			jedisPool = new JedisPool(jedisPoolConfig,"127.0.0.1",6379);
		}
		return jedisPool.getResource();
	}
	//返回连接
	public static void returnResources(Jedis jedis){
		jedisPool.returnResourceObject(jedis);
	}
}
