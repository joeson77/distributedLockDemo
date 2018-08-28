package com.zookeeper.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestZookeeper {
	
	private static Logger logger = LoggerFactory.getLogger(TestZookeeper.class);
	
	//初始化记录数值
	static int flag = 1000;
	//计数方法
	public static void secsKill(){
		logger.info(String.valueOf(--flag));
	}
	
	public static void main(String[] args) {
		
		Runnable runnable = new Runnable() {
			
			public void run() {
				LockImpl lock = null;
				try {
					lock = new LockImpl("127.0.0.1:2181", "testLock");
					lock.lock();
					secsKill();
					logger.info(Thread.currentThread().getName() + "正在运行");
				} finally {
					if (lock != null) {
						lock.unlock();
					}
				}
			}
		};
		for (int i = 0; i < 10; i++) {
			Thread thread = new Thread(runnable);
			thread.start();
		}
	}
}
