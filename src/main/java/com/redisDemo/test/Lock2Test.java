package com.redisDemo.test;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redisDemo.extendsClass.LockCase2;
import com.redisDemo.util.RedisUtils;

import redis.clients.jedis.Jedis;

public class Lock2Test {
	
	private final static Executor executor = Executors.newCachedThreadPool();// 启用多线程
	
	private static Logger logger = LoggerFactory.getLogger(LockCase2.class);
	
	public static void main(String[] args) {
		for (int i = 0; i <= 2; i++) {
			final Jedis jedis = RedisUtils.getJedis();
			executor.execute(new Runnable() {
				public void run() {
					LockCase2 lockCase2 = new LockCase2(jedis, "myLock");
					lockCase2.lock();
					logger.info("线程 " + String.valueOf(Thread.currentThread().getId()) + " 休眠");
					lockCase2.sleepBySencond(5);
					logger.info("线程 " + String.valueOf(Thread.currentThread().getId()) + " 苏醒");
					lockCase2.unlock();
				}
			});
		}

	}

}
