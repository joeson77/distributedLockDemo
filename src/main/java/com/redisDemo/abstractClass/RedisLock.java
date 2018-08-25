package com.redisDemo.abstractClass;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redisDemo.extendsClass.LockCase5;

import redis.clients.jedis.Jedis;

public abstract class RedisLock implements Lock {
	
	private static Logger logger = LoggerFactory.getLogger(RedisLock.class);
	
	protected Jedis jedis;
	protected String lockKey;
	//被守护的线程
	protected Thread theThread;
	
	public RedisLock(Jedis jedis,String lockKey){
		this.jedis = jedis;
		this.lockKey = lockKey;
	}
	
	public void sleepBySencond(int sencond){
		try {
			Thread.sleep(sencond * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void lockInterruptibly(){}
	
	public Condition newCondition() {
		return null;
	}
	
	public boolean tryLock() {
		return false;
	}
	
	public boolean tryLock(long time, TimeUnit unit){
		return false;
	}
	
	
	
	/** 在增加一个scheduleExpirationRenewal方法用于开启刷新过期时间的线程 */
	protected volatile boolean isOpenExpirationRenewal = true;
	
	/**
	* 开启定时刷新
	*/
	protected void scheduleExpirationRenewal(Thread thread){
		theThread = thread;
		Thread renewalThread = new Thread(new ExpirationRenewal());
		renewalThread.setDaemon(true);
		renewalThread.start();
	}
	
	/**
	* 刷新key的过期时间
	*/
	private class ExpirationRenewal implements Runnable{

		public void run() {
			while (isOpenExpirationRenewal) {
				logger.info("守护线程执行延迟失效时间中。。。");
				String checkAndExpireScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
						"return redis.call('expire',KEYS[1],ARGV[2]) " +
						"else " +
						"return 0 end";
				jedis.eval(checkAndExpireScript, 1, lockKey, String.valueOf(theThread.getId()), "5");
				logger.info("已为线程 :" + String.valueOf(theThread.getId()) + " 重新设置5秒加锁时间");
				//休眠10秒
				sleepBySencond(2);
			}
		}
		
	}
	
}
