package com.redisDemo.test;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redisDemo.extendsClass.LockCase5;
import com.redisDemo.util.RedisUtils;

import redis.clients.jedis.Jedis;

public class Lock5Test {
	// 启用多线程
		private final static Executor executor = Executors.newCachedThreadPool();
		
		private static Logger logger = LoggerFactory.getLogger(Lock3Test.class);
		
		public static void main(String[] args) {
			for (int i = 0; i <= 2; i++) {
				final Jedis jedis = RedisUtils.getJedis();
				executor.execute(new Runnable() {
					public void run() {
						LockCase5 lockCase5 = new LockCase5(jedis, "myLock");
						lockCase5.lock();
						logger.info("线程 " + String.valueOf(Thread.currentThread().getId()) + " 休眠");
						lockCase5.sleepBySencond(5);
						logger.info("线程 " + String.valueOf(Thread.currentThread().getId()) + " 苏醒");
						lockCase5.unlock();
					}
				});
			}
		}
}
