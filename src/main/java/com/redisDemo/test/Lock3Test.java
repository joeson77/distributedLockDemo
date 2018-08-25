package com.redisDemo.test;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redisDemo.extendsClass.LockCase3;
import com.redisDemo.util.RedisUtils;

import redis.clients.jedis.Jedis;

public class Lock3Test {
	
	// 启用多线程
	private final static Executor executor = Executors.newCachedThreadPool();
	
	private static Logger logger = LoggerFactory.getLogger(Lock3Test.class);
	
	public static void main(String[] args) {
		for (int i = 0; i <= 2; i++) {
			final Jedis jedis = RedisUtils.getJedis();
			executor.execute(new Runnable() {
				public void run() {
					LockCase3 lockCase3 = new LockCase3(jedis, "myLock");
					lockCase3.lock();
					logger.info("线程 " + String.valueOf(Thread.currentThread().getId()) + " 休眠");
					lockCase3.sleepBySencond(5);
					logger.info("线程 " + String.valueOf(Thread.currentThread().getId()) + " 苏醒");
					lockCase3.unlock();
				}
			});
		}

	}
}
