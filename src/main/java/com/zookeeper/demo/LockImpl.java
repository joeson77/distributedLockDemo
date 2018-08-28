package com.zookeeper.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockImpl implements Lock,Watcher {
	
	private static Logger logger = LoggerFactory.getLogger(LockImpl.class);
	
	//创建zookeeper
	private ZooKeeper zooKeeper;
	//根节点
	private String ROOT_LOCK = "/locks";
	//竞争的锁名字
	private String lockName;
	//等待的前一个锁
	private String WATCH_LOCK;
	//当前锁
	private String CURRENT_LOCK;
	//计数器
	private CountDownLatch countDownLatch;
	//session超时时间
	private int sessionTimeout = 3000;
	
	private List<Exception> exceptionList = new ArrayList<Exception>();
	
	public LockImpl(String config,String lockName) {
		this.lockName = lockName;
		try {
			zooKeeper = new ZooKeeper(config, sessionTimeout, this);
			Stat stat = zooKeeper.exists(ROOT_LOCK, false);
			//判断根节点是否存在,若不存在则创建
			if (stat == null) {
				zooKeeper.create(ROOT_LOCK, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//节点监视
	public void process(WatchedEvent event) {
		if (this.countDownLatch != null) {
			this.countDownLatch.countDown();
		}
	}

	public void lock() {
		if (exceptionList.size() > 0) {
			throw new LockException(exceptionList.get(0));
		}
		if (this.tryLock()) {
			logger.info(Thread.currentThread().getName() + " " + lockName + " 获得了锁");
			return;
		}else {
			waitForLock(WATCH_LOCK, sessionTimeout);
		}
	}

	public void lockInterruptibly() throws InterruptedException {
		this.lock();
	}

	public boolean tryLock() {
		try {
			String splitStr = "_lock_";
			//不能传入与拼接锁字符串相符的参数
			if (lockName.contains(splitStr)) {
				throw new LockException("锁名错误");
			}
			//创建临时有序节点
			CURRENT_LOCK = zooKeeper.create(ROOT_LOCK + "/" + lockName + splitStr, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			logger.info("已创建节点 : " + CURRENT_LOCK);
			//获取所有子节点
			List<String> subNodes = zooKeeper.getChildren(ROOT_LOCK, false);
			//获取所有lockName的锁
			List<String> lockObjeects = new ArrayList<String>();
			for (String node : subNodes) {
				String _node = node.split(splitStr)[0];
				if (_node.equals(lockName)) {
					lockObjeects.add(node);
				}
			}
			//排序
			Collections.sort(lockObjeects);
			logger.info(Thread.currentThread().getName() + " 的锁是 " + CURRENT_LOCK);
			//判断当前锁是否为排序后的第一个
			if (CURRENT_LOCK.equals(ROOT_LOCK + "/" + lockObjeects.get(0))) {
				return true;
			}
			//若不是第一个则表示为不为最小节点，获取自己节点前一个节点,截取当前实际锁名
			String preNode = CURRENT_LOCK.substring(CURRENT_LOCK.lastIndexOf("/") + 1);
			logger.info("解析到的节点 preNode 名为 : " + preNode);
			//被观望的锁，二分法查找
			WATCH_LOCK = lockObjeects.get(Collections.binarySearch(lockObjeects, preNode) - 1);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		if (this.tryLock()) {
			return false;
		}
		return waitForLock(WATCH_LOCK, sessionTimeout);
	}

	public void unlock() {
		try {
			logger.info("释放锁 " + CURRENT_LOCK);
			zooKeeper.delete(CURRENT_LOCK, -1);
			CURRENT_LOCK = null;
			zooKeeper.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		}
	}

	public Condition newCondition() {
		return null;
	}
	
	//等待获取锁
	public boolean waitForLock(String pre,long waitTime){
		try {
			Stat stat = zooKeeper.exists(ROOT_LOCK + "/" + pre, true);
			if (stat != null) {
				logger.info(Thread.currentThread().getName() + "等待锁" + ROOT_LOCK + "/" + pre);
				this.countDownLatch = new CountDownLatch(1);
				//计数等待，若等到前一个节点消失，则precess监听中中进行countDown，停止等待，获取锁
				this.countDownLatch.await(waitTime,TimeUnit.MILLISECONDS);
				this.countDownLatch = null;
				logger.info(Thread.currentThread().getName() + "等到了锁");
			}
			return true;
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public class LockException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public LockException(String e){
			super(e);
		}
		public LockException(Exception e){
			super(e);
		}
	}

}
